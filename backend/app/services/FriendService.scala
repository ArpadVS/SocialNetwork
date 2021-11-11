package services

import com.google.inject.Inject
import models._
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.Result
import play.api.mvc.Results.{BadRequest, Ok}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}


class FriendService @Inject()(friendshipList: FriendshipList, friendRequestList: FriendRequestList, userList: UserList)
                             (implicit val ec: ExecutionContext) {

  implicit val otherUserInfoDTO: OFormat[OtherUserInfoDTO] = Json.format[OtherUserInfoDTO]
  implicit val requestDTO: OFormat[RequestDTO] = Json.format[RequestDTO]

  def sendRequest(id: Long, otherUserId: Long): Future[String] = {
    friendRequestList.createRequest(FriendRequest(0, id, otherUserId))
  }

  def acceptRequest(reqId: Long, receiverId: Long): Future[Result] = {
    friendRequestList.getRequestById(reqId).flatMap(reqOpt => {
      if (reqOpt.isEmpty) {
        Future(BadRequest("Request with given id doesn't exist!"))
      } else if (reqOpt.get.receiverId != receiverId) {
        Future(BadRequest("You cannot accept this request!"))
      } else {
        val r = reqOpt.get
        friendshipList.createFriendship(Friendship(0L, r.senderId, r.receiverId)).flatMap(_ => {
          friendRequestList.answerRequest(reqId).flatMap(_ => {
            Future(Ok("Friend request accepted!"))
          })
        })
      }
    })
  }

  def rejectRequest(reqId: Long, userId: Long): Future[Int] = {
    friendRequestList.rejectRequest(requestId = reqId, receiverId = userId)
  }

  def areFriends(u1: Long, u2: Long): Future[Boolean] = {
    friendshipList.areFriends(u1, u2).flatMap(friends => {
      if (friends.isEmpty) {
        Future(false)
      } else Future(true)
    })
  }

  def isRequestSent(u1: Long, u2: Long): Future[Boolean] = {
    friendRequestList.isFriendRequestSent(u1, u2).flatMap(fRequests => {
      if (fRequests.isEmpty) {
        Future(false)
      } else Future(true)
    })
  }

  def getFriends(id: Long): Future[Seq[User]] = {
    val friendIds: mutable.Set[Long] = mutable.Set(id)
    val retVal: mutable.ListBuffer[User] = mutable.ListBuffer()

    //  collecting ids of friends
    friendshipList.getFriends(id).map(friendships => {
      friendships.foreach(friendship => {
        friendIds += (friendship.user1Id, friendship.user2Id)
      })
      //  finding users by ids
    }).flatMap(_ => {
      userList.getUsersBySet(friendIds).map(users => {
        users.foreach(user => {
          if (user.id != id) {
            val u = user
            u.password = ""
            retVal += u
          }
        })
      }).flatMap(_ => Future(retVal))
    })
  }


  def getFriendRequests(id: Long): Future[Result] = {
    friendRequestList.getAllRequestsForUser(id).flatMap(requests => {
      Future(Ok(Json.toJson(requests)))
    })
  }

  def getRelationshipWithUser(myId: Long, otherUserId: Long): Future[Result] = {
    areFriends(myId, otherUserId).flatMap(hasFriendship => {
      // if already friends no need to go further, cuz already friends
      if (hasFriendship) {
        Future(Ok(Json.toJson(OtherUserInfoDTO(isFriend = true, isRequestSent = false, isRequestReceived = false, -1))))
      } else {
        friendRequestList.isFriendRequestSent(myId, otherUserId).flatMap(fRequests => {
          // if not friends nor is there a request, then no relationship
          if (fRequests.isEmpty) {
            Future(Ok(Json.toJson(OtherUserInfoDTO(isFriend = false, isRequestSent = false, isRequestReceived = false, -1))))
          } else {
            // if I already sent a request
            if (fRequests.get.senderId == myId) {
              Future(Ok(Json.toJson(OtherUserInfoDTO(isFriend = false, isRequestSent = true, isRequestReceived = false, -1))))
            } else {
              // if i already received a request
              Future(Ok(Json.toJson(OtherUserInfoDTO(isFriend = false, isRequestSent = false, isRequestReceived = true, fRequests.get.id))))
            }

          }
        })
      }
    })
  }
}

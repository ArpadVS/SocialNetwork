package controllers

import models.{User, UserDTO}
import play.api.libs.json._
import play.api.mvc._
import services.FriendService
import utils.{AuthenticatedAction, JwtUser, JwtUtil}

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FriendController @Inject()(
                                  cc: ControllerComponents,
                                  friendService: FriendService,
                                  authenticatedAction: AuthenticatedAction) extends AbstractController(cc) {

  implicit val userFormat: OFormat[User] = Json.format[User]
  implicit val formatUserDetails: OFormat[JwtUser] = Json.format[JwtUser]
  implicit val userDTO: OFormat[UserDTO] = Json.format[UserDTO]

  //  on user profile if not friend link api/friendrequest/send/:id
  def sendRequest(otherUserId: Long): Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    val jwtToken2 = request.headers.get("jw_token").getOrElse("")
    JwtUtil.decodePayload(jwtToken2).fold {
      Future.successful(Unauthorized("Invalid credential"))
    } { payload =>
      val userInfo = Json.parse(payload).validate[JwtUser].get
      if (userInfo.id == otherUserId) {
        Future.successful(BadRequest("Cannot send friend request to yourself"))
      } else {
        friendService.areFriends(userInfo.id, otherUserId).flatMap(friends => {
          if (friends) {
            Future.successful(BadRequest("Cannot send request, you are already friends"))
          } else {
            friendService.isRequestSent(userInfo.id, otherUserId).flatMap(fRequests => {
              if (fRequests) {
                Future.successful(BadRequest("Cannot send request, request already sent"))
              } else {
                friendService.sendRequest(userInfo.id, otherUserId).flatMap(retval => {
                  Future.successful(Ok(retval))
                })
              }
            })
          }
        })
      }
    }
  }

  //  at list for request on accept button api/friendrequest/accept/:id  -> request id
  def acceptRequest(reqId: Long): Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    val jwtToken2 = request.headers.get("jw_token").getOrElse("")
    JwtUtil.decodePayload(jwtToken2).fold {
      Future.successful(Unauthorized("Invalid credential"))
    } { payload =>
      val userInfo = Json.parse(payload).validate[JwtUser].get

      try {
        friendService.acceptRequest(reqId, userInfo.id)
      } catch {
        case _: Exception => Future(BadRequest("You cannot accept this request"))
      }
    }
  }


  //  at list for request on accept button api/friendrequest/accept/:id  -> request id
  //  if rejected, request deleted and no further actions
  def rejectRequest(reqId: Long): Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    val jwtToken2 = request.headers.get("jw_token").getOrElse("")
    JwtUtil.decodePayload(jwtToken2).fold {
      Future.successful(Unauthorized("Invalid credential"))
    } { payload =>
      val userInfo = Json.parse(payload).validate[JwtUser].get
      try {
        friendService.rejectRequest(reqId, userInfo.id).flatMap(retv => {
          if (retv != 0) {
            Future(Ok("Request cancelled"))
          } else {
            Future(BadRequest("Bad request"))
          }
        })
      } catch {
        case _: Exception => Future(BadRequest("You cannot reject this request"))
      }
    }
  }


  def getFriendRequests: Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    val jwtToken2 = request.headers.get("jw_token").getOrElse("")
    JwtUtil.decodePayload(jwtToken2).fold {
      Future.successful(Unauthorized("Invalid credential"))
    } { payload =>
      val userInfo = Json.parse(payload).validate[JwtUser].get
      try {
        println(userInfo.id)
        friendService.getFriendRequests(userInfo.id)
      } catch {
        case _: Exception => Future(BadRequest("You cannot get requests!"))
      }
    }
  }

  def getFriends: Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    val jwtToken2 = request.headers.get("jw_token").getOrElse("")
    JwtUtil.decodePayload(jwtToken2).fold {
      Future.successful(Unauthorized("Invalid credential"))
    } { payload =>
      val userInfo = Json.parse(payload).validate[JwtUser].get
      try {
        friendService.getFriends(userInfo.id).map(users => {
          Ok(Json.toJson(UserDTO.usersToDTOs(users)))
        })
      } catch {
        case _: Exception => Future(BadRequest("You cannot reject this request"))
      }
    }
  }

  def getRelationshipWithUser(id: Long): Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    val jwtToken2 = request.headers.get("jw_token").getOrElse("")
    JwtUtil.decodePayload(jwtToken2).fold {
      Future.successful(Unauthorized("Invalid credential"))
    } { payload =>
      val userInfo = Json.parse(payload).validate[JwtUser].get
      friendService.getRelationshipWithUser(userInfo.id, id)
    }

  }
}

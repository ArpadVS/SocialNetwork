package models

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

case class FriendRequest(id: Long, senderId: Long, receiverId: Long)

class FriendRequests(tag: Tag) extends Table[FriendRequest](tag, "friend_requests") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def senderId = column[Long]("sender_id")

  def receiverId = column[Long]("receiver_id")

  override def * = (id, senderId, receiverId) <> (FriendRequest.tupled, FriendRequest.unapply)

  val users = TableQuery[Users]

  def fkSender = foreignKey(name = "sender_fk", senderId, users)(_.id)

  def fkReceiver = foreignKey(name = "receiver_fk", receiverId, users)(_.id)

}

class FriendRequestList @Inject()(
                                   protected val dbConfigProvider: DatabaseConfigProvider)
                                 (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {


  var friendRequests = TableQuery[FriendRequests]
  var friendships = TableQuery[Friendships]
  var users = TableQuery[Users]

  def createRequest(req: FriendRequest): Future[String] = {
    dbConfig.db
      .run(friendRequests += req)
      .map(_ => "Request sent")
      .recover {
        case ex: Exception =>
          printf(ex.getMessage)
          ex.getMessage
      }
  }

  // the api will look like /api/friendrequest/accept/5  -> 5 is request ID, no need for other info
  def answerRequest(id: Long): Future[Int] = {
    dbConfig.db.run(friendRequests.filter(_.id === id).delete)
  }

  def getRequestById(requestId: Long): Future[Option[FriendRequest]] = {
    dbConfig.db.run(friendRequests.filter(_.id === requestId).result.headOption)
  }

  def rejectRequest(requestId: Long, receiverId: Long): Future[Int] = {
    println(s"Request id $requestId receiver is $receiverId")
    dbConfig.db.run(friendRequests.filter(_.id === requestId).filter(_.receiverId === receiverId).delete)
  }

  def getAllRequestsForUser(uId: Long): Future[Seq[RequestDTO]] = {
    dbConfig.db.run(friendRequests.filter(_.receiverId === uId).result)

    val innerJoin = for {
      (r, u) <- friendRequests.filter(_.receiverId === uId) join users on (_.senderId === _.id)
    } yield (r.id, r.senderId, r.receiverId, u.firstName, u.lastName, u.email, u.picture)

    dbConfig.db.run(innerJoin.result).map(result => {
      result.map(r => {
        RequestDTO(r._1, r._2, r._3, r._4, r._5, r._6, r._7)
      })
    })
  }

  def isFriendRequestSent(sender: Long, receiver: Long): Future[Option[FriendRequest]] = {
    val q1 = friendRequests.filter(_.senderId === sender).filter(_.receiverId === receiver)
    val q2 = friendRequests.filter(_.senderId === receiver).filter(_.receiverId === sender)
    val union = q1 union q2
    dbConfig.db.run(union.result.headOption)
  }

}
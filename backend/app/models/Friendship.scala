package models

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

case class Friendship(id: Long, user1Id: Long, user2Id: Long)

class Friendships(tag: Tag) extends Table[Friendship](tag, "friendships") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def user1Id = column[Long]("user1_id")

  def user2Id = column[Long]("user2_id")

  override def * = (id, user1Id, user2Id) <> (Friendship.tupled, Friendship.unapply)

  val users = TableQuery[Users]

  def fkUser1 = foreignKey(name = "user1_fk", user1Id, users)(_.id)

  def fkUser2 = foreignKey(name = "user2_fk", user2Id, users)(_.id)
}


class FriendshipList @Inject()(
                                protected val dbConfigProvider: DatabaseConfigProvider)
                              (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  var friendships = TableQuery[Friendships]
  var friendRequests = TableQuery[FriendRequests]

  def createFriendship(req: Friendship): Future[String] = {
    dbConfig.db
      .run(friendships += req)
      .map(_ => "Friendship created")
      .recover {
        case ex: Exception =>
          printf(ex.getMessage)
          ex.getMessage
      }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(friendships.filter(_.id === id).delete)
  }

  def areFriends(u1: Long, u2: Long): Future[Seq[Friendship]] = {
    val q1 = friendships.filter(_.user1Id === u1).filter(_.user2Id === u2)
    val q2 = friendships.filter(_.user1Id === u2).filter(_.user2Id === u1)
    val union = q1 union q2
    dbConfig.db.run(union.result)
  }

  def getFriends(uId: Long): Future[Seq[Friendship]] = {
    val q1 = friendships.filter(_.user1Id === uId)
    val q2 = friendships.filter(_.user2Id === uId)
    val union = q1 union q2
    dbConfig.db.run(union.result)
  }
}

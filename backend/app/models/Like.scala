package models

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

case class Like(id: Long, userId: Long, postId: Long)

class Likes(tag: Tag) extends Table[Like](tag, "likes") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def userId = column[Long]("user_id")

  def postId = column[Long]("post_id")

  override def * = (id, userId, postId) <> (Like.tupled, Like.unapply)

  val users = TableQuery[Users]
  val posts = TableQuery[Posts]

  def fkUser = foreignKey(name = "likeuser_fk", userId, users)(_.id)

  def fkPost = foreignKey(name = "likepost_fk", postId, posts)(_.id)

  // user can like a post only once
  def idx = index("model_unique", (userId, postId), unique = true)
}

class LikeList @Inject()(
                          protected val dbConfigProvider: DatabaseConfigProvider)
                        (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  var likes = TableQuery[Likes]

  def add(like: Like): Future[String] = {
    dbConfig.db
      .run(likes += like)
      .map(_ => "Like successfully added")
      .recover {
        case ex: Exception =>
          printf(ex.getMessage)
          ex.getMessage
      }
  }

  def deleteById(id: Long): Future[Int] = {
    dbConfig.db.run(likes.filter(_.id === id).delete)
  }

  def delete(uId: Long, pId: Long): Future[Int] = {
    dbConfig.db.run(likes.filter(_.postId === pId).filter(_.userId === uId).delete)
  }

  def findPostIdsLikedByUser(uId: Long): Future[Seq[Like]] = {
    dbConfig.db.run(likes.filter(_.userId === uId).result)
  }

  def deleteLikesForPost(postId: Long): Future[Int] = {
    dbConfig.db.run(likes.filter(_.postId === postId).delete)
  }

}
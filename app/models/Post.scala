package models

import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

case class Post(id: Long, userId: Long, text: String, likes: Int, created: LocalDate)

case class PostFormData(userId: Long, text: String, likes: Int)

object PostForm {
  val form: Form[PostFormData] = Form(
    mapping(
      "userId" -> longNumber(min = 0),
      "text" -> nonEmptyText,
      "likes" -> number(min = 0)
    )(PostFormData.apply)(PostFormData.unapply)
  )
}

class PostTableDef(tag: Tag) extends Table[Post](tag, "posts") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def userId = column[Long]("user_id")

  def text = column[String]("text")

  def likes = column[Int]("likes")

  def created = column[LocalDate]("created")

  override def * = (id, userId, text, likes, created) <> (Post.tupled, Post.unapply)

  val authors = TableQuery[UserTableDef]
  def author = foreignKey("user_fk", userId, authors)(_.id)
}


class PostList @Inject()(
                          protected val dbConfigProvider: DatabaseConfigProvider)
                        (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  var postList = TableQuery[PostTableDef]

  def add(postItem: Post): Future[String] = {
    dbConfig.db
      .run(postList += postItem)
      .map(res => "PostItem successfully added" + res)
      .recover {
        case ex: Exception =>
          printf(ex.getMessage)
          ex.getMessage
      }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(postList.filter(_.id === id).delete)
  }

  def update(postItem: Post): Future[Int] = {
    dbConfig.db
      .run(postList.filter(_.id === postItem.id)
        .map(x => (x.text, x.likes, x.created))
        .update(postItem.text, postItem.likes, postItem.created)
      )
  }

  def get(id: Long): Future[Option[Post]] = {
    dbConfig.db.run(postList.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[Post]] = {
    dbConfig.db.run(postList.result)
  }
}

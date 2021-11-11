package models

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import java.time.LocalDate
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

case class Post(id: Long, userId: Long, var text: String, var likes: Int, created: LocalDate)

case class PostCreationDTO(text: String)

object PostCreationDTO {
  implicit val postCreationDTO: OFormat[PostCreationDTO] = Json.format[PostCreationDTO]
}

class Posts(tag: Tag) extends Table[Post](tag, "posts") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def userId = column[Long]("user_id")

  def text = column[String]("text")

  def likes = column[Int]("likes")

  def created = column[LocalDate]("created")

  override def * = (id, userId, text, likes, created) <> (Post.tupled, Post.unapply)

  val authors = TableQuery[Users]

  def author = foreignKey("user_fk", userId, authors)(_.id)
}


class PostList @Inject()(
                          protected val dbConfigProvider: DatabaseConfigProvider)
                        (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  var posts = TableQuery[Posts]
  var users = TableQuery[Users]

  def add(postItem: Post): Future[String] = {
    dbConfig.db
      .run(posts += postItem)
      .map(res => "PostItem successfully added" + res)
      .recover {
        case ex: Exception =>
          printf(ex.getMessage)
          ex.getMessage
      }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(posts.filter(_.id === id).delete)
  }

  def update(postItem: Post): Future[Int] = {
    dbConfig.db
      .run(posts.filter(_.id === postItem.id)
        .map(x => (x.text, x.likes, x.created))
        .update(postItem.text, postItem.likes, postItem.created)
      )
  }

  def updatePostLikeCounter(postItem: Post, step: Int): Future[Int] = {
    dbConfig.db
      .run(posts.filter(_.id === postItem.id)
        .map(x => (x.text, x.likes, x.created))
        .update(postItem.text, postItem.likes + step, postItem.created)
      )

  }

  def get(id: Long): Future[Option[Post]] = {
    dbConfig.db.run(posts.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[Post]] = {
    dbConfig.db.run(posts.result)
  }

  def getTimeLine(ids: mutable.Set[Long]): Future[Seq[PostWithLiked]] = {
    val innerJoin = for {
      (p, u) <- posts.filter(_.userId inSet ids).sortBy(_.created.desc.nullsLast) join users on (_.userId === _.id)
    } yield (p.id, p.userId, u.firstName, u.lastName, u.picture,
      p.text, p.likes, p.created, false)

    dbConfig.db.run(innerJoin.result).map(result => {
      result.map(r => {
        PostWithLiked(r._1, r._2, r._3, r._4, r._5, r._6, r._7, r._8, r._9)
      })
    })
  }

  def getPostsFromUser(id: Long): Future[Seq[Post]] = {
    dbConfig.db.run(posts.filter(_.userId === id).sortBy(_.created.desc.nullsLast).result)
  }

}

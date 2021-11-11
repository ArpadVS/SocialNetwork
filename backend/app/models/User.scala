package models


import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import java.time.LocalDate
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}


case class User(id: Long, email: String, var password: String, firstName: String, lastName: String,
                picture: String, registrationDate: LocalDate)

case class UserFormData(email: String, password: String, firstName: String, lastName: String)

object UserFormData {
  implicit val userFormData: OFormat[UserFormData] = Json.format[UserFormData]
}

case class UserUpdateDTO(email: String, firstName: String, lastName: String)

object UserUpdateDTO {
  implicit val userFormData: OFormat[UserUpdateDTO] = Json.format[UserUpdateDTO]
}

case class LoggedInUser(userId: Long, email: String, firstName: String, lastName: String, accessToken: String)

object UserForm {

  val form: Form[UserFormData] = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText(minLength = 3, maxLength = 30),
      "firstName" -> nonEmptyText(minLength = 2),
      "lastName" -> nonEmptyText(minLength = 3, maxLength = 30),
    )(UserFormData.apply)(UserFormData.unapply)
  )
}

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def email = column[String]("email", O.Unique)

  def password = column[String]("password")

  def firstName = column[String]("first_name")

  def lastName = column[String]("last_name")

  def picture = column[String]("picture")

  def registrationDate = column[LocalDate]("registration_date")

  override def * = (id, email, password, firstName, lastName, picture, registrationDate) <>
    (User.tupled, User.unapply)

}


class UserList @Inject()(
                          protected val dbConfigProvider: DatabaseConfigProvider)
                        (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  var users = TableQuery[Users]

  def add(userItem: User): Future[String] = {
    dbConfig.db
      .run(users += userItem)
      .map(_ => "User registered successfully")
      .recover {
        case ex: Exception =>
          printf(ex.getMessage)
          ex.getMessage
      }
  }

  def findByEmail(email: String): Future[Option[User]] = {
    dbConfig.db.run(users.filter(_.email === email).result.headOption)
  }

  def searchByName(term: String): Future[Seq[User]] = {
    val q1 = users.filter(_.lastName.toLowerCase like s"%$term%")
    val q2 = users.filter(_.firstName.toLowerCase like s"%$term%")
    val union = q1 union q2
    dbConfig.db.run(union.result)
  }

  def getUsersBySet(ids: mutable.Set[Long]): Future[Seq[User]] = {
    dbConfig.db.run(users.filter(_.id inSet ids).result)
  }

  def update(userItem: User): Future[Int] = {
    dbConfig.db
      .run(users.filter(_.id === userItem.id)
        .map(x => (x.email, x.firstName, x.lastName))
        .update(userItem.email, userItem.firstName, userItem.lastName)
      )
  }

  def updatePicture(id: Long, picture: String): Future[Int] = {
    dbConfig.db
      .run(users.filter(_.id === id)
        .map(x => x.picture)
        .update(picture)
      )
  }

  def get(id: Long): Future[Option[User]] = {
    dbConfig.db.run(users.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[User]] = {
    dbConfig.db.run(users.result)
  }

}
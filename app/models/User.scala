package models


import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}


case class User(id: Long, email:String, var password: String, firstName: String, lastName:String, registrationDate: LocalDate)
case class UserFormData(email:String, password: String, firstName: String, lastName: String)

object UserForm {

  val form = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText(minLength = 3, maxLength = 30),
      "firstName" -> nonEmptyText(minLength = 2),
      "lastName" -> nonEmptyText(minLength = 3, maxLength = 30),
    )(UserFormData.apply)(UserFormData.unapply)
  )
}

class UserTableDef(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email", O.Unique)
  def password = column[String]("password")
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def registrationDate = column[LocalDate]("registration_date")

  override def * = (id, email, password, firstName, lastName, registrationDate) <> (User.tupled, User.unapply)

}


class UserList @Inject()(
                          protected val dbConfigProvider: DatabaseConfigProvider)
                        (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  var userList = TableQuery[UserTableDef]

  def add(userItem: User): Future[String] = {
    dbConfig.db
      .run(userList += userItem)
      .map(_ => "User registered successfully")
      .recover {
        case ex: Exception =>
          printf(ex.getMessage)
          ex.getMessage
      }
  }

  def findByEmail(email: String): Future[User] = {
    dbConfig.db.run(userList.filter(_.email === email).result.head)
  }

  def get(id: Long): Future[Option[User]] = {
    dbConfig.db.run(userList.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[User]] = {
    dbConfig.db.run(userList.result)
  }

}
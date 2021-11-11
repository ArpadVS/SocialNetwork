package services

import com.google.inject.Inject
import models._
import play.Environment
import play.api.libs.Files
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.Results._
import play.api.mvc.{MultipartFormData, Result}
import utils.{BcryptHashHelper, JwtUtil}

import java.nio.file.Paths
import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._


class UserService @Inject()(items: UserList, env: Environment) {

  implicit val userFormat: OFormat[User] = Json.format[User]

  def tryLogin(data: LoginFormData): Future[Boolean] = {
    items.findByEmail(data.email).flatMap(user => {
      if (user.isDefined) {
        if (user.get.email == data.email) { //just for sure
          Future(BcryptHashHelper.checkPassword(data.password, user.get.password))
        } else Future(false)
      }
      else Future(false)
    })

  }

  def getUserInfoWithToken(user: User): LoggedInUser = {
    val token: String = getTokenForUser(user)
    LoggedInUser(userId = user.id, email = user.email, user.firstName, user.lastName, accessToken = token)
  }

  def getTokenForUser(loggedInUser: User): String = {
    loggedInUser.password = ""
    val payLoad: String = s"""{"email": "${loggedInUser.email}", "id": ${loggedInUser.id}}"""
    val token: String = JwtUtil.createToken(payLoad)
    println("Logged in as user" + loggedInUser)
    token
  }

  def searchUsersByName(term: String): Future[Seq[User]] = {
    items.searchByName(term)
  }

  def register(formData: UserFormData): Future[String] = {
    val newUser = User(0, formData.email, BcryptHashHelper.createPassword(formData.password), formData.firstName,
      formData.lastName, "default.png", LocalDate.now())
    items.add(newUser)
  }

  def updateUser(update: UserUpdateDTO, uId: Long, isEmailChanged: Boolean): Future[Result] = {
    val updatedUser = User(uId, update.email.trim, "", update.firstName.trim, update.lastName.trim,
      "default.png", LocalDate.now())
    if (!isValid(updatedUser.email)) Future(BadRequest("Invalid email format"))
    else if (updatedUser.firstName.length < 2) Future(BadRequest("First name too short"))
    else if (updatedUser.lastName.length < 2) Future(BadRequest("Last name too short"))
    else {
      //if email changed from original, check if email already registered in db
      if (isEmailChanged) {
        findByEmail(update.email).flatMap(checkUser => {
          if (checkUser.isEmpty) {
            items.update(updatedUser).flatMap(_ => Future(Ok(getTokenForUser(updatedUser))))
          } else {
            println("Email already used")
            Future(BadRequest("Email already used!"))
          }
        })
      } else {
        items.update(updatedUser).flatMap(_ => Future(Ok("User updated")))
      }
    }
  }

  def changeProfilePicture(id: Long, picture: MultipartFormData.FilePart[Files.TemporaryFile]): Future[Result] = {
    // val fileSize    = picture.fileSize
    // val contentType = picture.contentType
    // val filename = Paths.get(picture.filename).getFileName
    items.get(id).flatMap(u => {
      if (u.isEmpty) {
        Future(InternalServerError("An error occurred"))
      } else {
        try {
          val fileName = s"user_$id.png"
          val fullPath: String = env.rootPath().getPath + "\\public\\profiles\\" + fileName
          picture.ref.copyTo(Paths.get(fullPath), replace = true)
          items.updatePicture(id, fileName)
          Future(Ok("Picture uploaded"))
        } catch {
          case _: Exception => Future(InternalServerError("An error occurred"))
        }
      }
    })

  }

  def listAllItems: Future[Seq[User]] = {
    items.listAll
  }

  def findByEmail(email: String): Future[Option[User]] = {
    items.findByEmail(email)
  }

  def findById(id: Long): Future[Result] = {
    if (id < 0) {
      Future(BadRequest("User id cannot be negative!"))
    } else {
      items.get(id).flatMap(user => {
        if (user.isEmpty) {
          Future(BadRequest("User with given id doesn't exist!"))
        } else {
          user.get.password = ""
          Future(Ok(Json.toJson(UserDTO.userToDTO(user.get))))
        }
      })
    }
  }

  private def isValid(email: String): Boolean =
    if ("""^[-a-z0-9!#$%&'*+/=?^_`{|}~]+(\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*@([a-z0-9]([-a-z0-9]{0,61}[a-z0-9])?\.)*(aero|arpa|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel|[a-z][a-z])$""".r.findFirstIn(email).isEmpty) false else true


}

package controllers

import models._
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._
import services.UserService
import utils.{AuthenticatedAction, JwtUser, JwtUtil}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthenticationController @Inject()(
                                          cc: ControllerComponents,
                                          userService: UserService,
                                          authenticatedAction: AuthenticatedAction) extends AbstractController(cc) {

  implicit val userFormat: OFormat[User] = Json.format[User]
  implicit val formatUserDetails: OFormat[JwtUser] = Json.format[JwtUser]
  implicit val formatLoggedInUser: OFormat[LoggedInUser] = Json.format[LoggedInUser]
  implicit val userDTO: OFormat[UserDTO] = Json.format[UserDTO]

  def login: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val data: Option[LoginFormData] = request.body.asJson.get.asOpt[LoginFormData]
    if (data.isDefined) {
      userService.tryLogin(data.get).flatMap(successfulLogin => {
        if (successfulLogin) {
          userService.findByEmail(data.get.email).map { loggedInUser =>
            Ok(Json.toJson(userService.getUserInfoWithToken(loggedInUser.get)))
          }
        } else {
          println("Login not successful")
          Future.successful(BadRequest("Invalid email or password!"))
        }

      })
    } else Future.successful(BadRequest("Bad request format!"))
  }

  def register: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    UserForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => {
        errorForm.errors.foreach(println)
        Future.successful(BadRequest("Form data in invalid!"))
      },
      data => {
        userService.findByEmail(data.email).flatMap(checkUser => {
          if (checkUser.isEmpty) {
            userService.register(data).map(u => Created(u))
          } else {
            println("Email already used")
            Future(BadRequest("Email already used!"))
          }
        })
      })
  }

  def updateUser(): Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    try {
      val detail: Option[UserUpdateDTO] = request.body.asJson.get.asOpt[UserUpdateDTO]
      val userId: Long = getUserIdFromToken(request)
      if (detail.isDefined && userId != -1) {
        userService.updateUser(detail.get, userId, isEmailChanged(detail.get.email, request))
      } else Future(BadRequest("Request body has wrong format!"))
    } catch {
      case _: Exception => Future(BadRequest("No body for request!"))
    }
  }

  def newProfilePicture(): Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    val id = getUserIdFromToken(request)
    request.body.asMultipartFormData.get
      .file("picture")
      .map { picture =>
        userService.changeProfilePicture(id, picture)
      }
      .getOrElse {
        Future(BadRequest("An error occurred"))
      }
  }

  def searchUsers: Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    try {
      val data: Option[SearchUserDTO] = request.body.asJson.get.asOpt[SearchUserDTO]
      if (data.isDefined) {
        userService.searchUsersByName(data.get.text.trim.toLowerCase).flatMap(users => {
          users.foreach(u => u.password = "")
          Future(Ok(Json.toJson(UserDTO.usersToDTOs(users))))
        })
      } else {
        Future(BadRequest("No parameter 'text' for search"))
      }
    } catch {
      case _: Exception => Future(BadRequest("No request body for search"))
    }
  }

  def getAllUsers: Action[AnyContent] = authenticatedAction.async {
    implicit request: Request[AnyContent] =>
      userService.listAllItems.map {
        users =>
          Ok(Json.toJson(UserDTO.usersToDTOs(users)))
      }
  }

  def getUserById(id: Long): Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    userService.findById(id)
  }

  def whoAmI: Action[AnyContent] = authenticatedAction.async { implicit request: Request[AnyContent] =>
    val jwtToken = request.headers.get("jw_token").getOrElse("")
    JwtUtil.decodePayload(jwtToken).fold {
      Future.successful(Unauthorized("Invalid credential"))
    } { payload =>
      val userInfo = Json.parse(payload).validate[JwtUser].get
      userService.findById(userInfo.id)

    }
  }

  private def getUserIdFromToken(request: Request[AnyContent]): Long = {
    val jwtToken2 = request.headers.get("jw_token").getOrElse("")
    JwtUtil.decodePayload(jwtToken2).fold {
      val ret: Long = -1
      ret
    } { payload =>
      val userInfo = Json.parse(payload).validate[JwtUser].get
      userInfo.id
    }
  }

  private def isEmailChanged(email: String, request: Request[AnyContent]): Boolean = {
    val jwtToken2 = request.headers.get("jw_token").getOrElse("")
    JwtUtil.decodePayload(jwtToken2).fold {
      false
    } { payload =>
      val userInfo = Json.parse(payload).validate[JwtUser].get
      userInfo.email != email.trim
    }
  }
}

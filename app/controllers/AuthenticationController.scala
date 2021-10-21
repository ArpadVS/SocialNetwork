package controllers

import models.{User, UserForm, LoginFormData}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._
import services.UserService
import utils.BcryptHashHelper
import play.api.data.Form
import play.api.data.Forms._

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthenticationController @Inject()(
                                          cc: ControllerComponents,
                                          userService: UserService) extends AbstractController(cc) {

  implicit val userFormat: OFormat[User] = Json.format[User]

  val loginForm: Form[LoginFormData] = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText(minLength = 3, maxLength = 30)
    )(LoginFormData.apply)(LoginFormData.unapply)
  )

  def login: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    loginForm.bindFromRequest.fold(
      errorForm => {
        errorForm.errors.foreach(println)
        Future.successful(BadRequest("Form data in invalid!"))
      },
      data => {
        if (userService.tryLogin(data)){
          userService.findByEmail(data.email).map{ loggedInUser =>
            loggedInUser.password = ""
            Ok(Json.toJson(loggedInUser))
          }
        }else{
          println("Login not successful")
          Future.successful(BadRequest("Invalid email or password"))
        }
      }
    )
  }

  def register: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    UserForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => {
        errorForm.errors.foreach(println)
        Future.successful(BadRequest("Form data in invalid!"))
      },
      data => {
        try {
          userService.findByEmail(data.email).map(_ => {
            println("Email already used")
          })

          val newUser = User(0, data.email, BcryptHashHelper.createPassword(data.password), data.firstName, data.lastName, LocalDate.now())
          // change redirect later to 200 Ok
          userService.register(newUser).map(u => Created(u))
        } catch {
          case _: Exception =>
            Future.successful(BadRequest("An error occurred"))
        }
      })
  }

  def getAllUsers: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userService.listAllItems.map { item =>
      item.foreach(u => u.password = "")
      Ok(Json.toJson(item))
    }
  }

  def getUserById(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userService.findById(id) map { item =>
      item.map(u => u.password = "")
      Ok(Json.toJson(item))
    }
  }

}

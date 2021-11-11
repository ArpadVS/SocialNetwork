package utils

import com.google.inject.Inject
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.Results._
import play.api.mvc._
import services.UserService

import scala.concurrent.{ExecutionContext, Future}


case class JwtUser(email: String, id: Long)

class AuthenticatedAction @Inject()(parser: BodyParsers.Default,
                                    userService: UserService)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    implicit val formatUserDetails: OFormat[JwtUser] = Json.format[JwtUser]
    val jwtToken = request.headers.get("jw_token").getOrElse("")

    if (JwtUtil.isValidToken(jwtToken)) {
      JwtUtil.decodePayload(jwtToken).fold {
        Future.successful(Unauthorized("Invalid credential"))
      } { payload =>
        val userInfo = Json.parse(payload).validate[JwtUser].get
        //  println(userInfo)
        userService.findByEmail(userInfo.email).flatMap(u1 => {
          if (u1.isDefined) {
            if (userInfo.email == u1.get.email && userInfo.id == u1.get.id) {
              block(request)
              //Ok("Authorized but why am i here")
            } else Future(Unauthorized("Invalid credential"))
          } else Future(Unauthorized("Invalid credential"))
        }
        )
      }
    } else {
      Future.successful(Unauthorized("Invalid token"))
    }
  }
}

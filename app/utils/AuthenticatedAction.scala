package utils

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import services.UserService

import scala.concurrent.{ExecutionContext, Future}


case class JwtUser(email: String, id: Long)

class AuthenticatedAction @Inject()(parser: BodyParsers.Default,
                                    userService: UserService)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    println("Logging")
    // ///////////////////////////////////////////////////////////////
    implicit val formatUserDetails = Json.format[JwtUser]
    val jwtToken = request.headers.get("jw_token").getOrElse("")

    if (JwtUtil.isValidToken(jwtToken)) {
      println(JwtUtil.decodePayload(jwtToken))
      JwtUtil.decodePayload(jwtToken).fold {
        Future.successful(Unauthorized("Invalid credential"))
      } { payload =>
        val userInfo = Json.parse(payload).validate[JwtUser].get
        println(userInfo)
        userService.findByEmail(userInfo.email).map(u1 => {
          if (userInfo.email == u1.email && userInfo.id == u1.id) {
            //  block(request)        WHY CANT I GET OUT
            Ok("Authorized but why am i here")
          } else {
            Unauthorized("Invalid credential")
          }
        }
        )
        //this works only if Option<JwtUSer>
        //maybeUserInfo.fold(Future.successful(Unauthorized("Invalid credential")))(userInfo => block(UserRequest(userInfo, request)))
      }
    } else {
      Future.successful(Unauthorized("Invalid token"))
    }
  }
}

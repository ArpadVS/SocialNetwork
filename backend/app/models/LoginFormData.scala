package models

import play.api.libs.json.{Json, OFormat}

case class LoginFormData(email: String, password: String)

object LoginFormData {
  implicit val loginFormData: OFormat[LoginFormData] = Json.format[LoginFormData]
}

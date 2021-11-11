package models

import play.api.libs.json.{Json, OFormat}

case class SearchUserDTO(text: String)

object SearchUserDTO {
  implicit val searchUserDTO: OFormat[SearchUserDTO] = Json.format[SearchUserDTO]
}

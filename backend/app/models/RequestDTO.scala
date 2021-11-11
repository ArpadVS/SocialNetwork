package models

import play.api.libs.json.{Json, OFormat}

case class RequestDTO(requestId: Long, senderId: Long, receiverId: Long, firstName: String, lastName: String
                      , email: String, picture: String)

object RequestDTO {
  implicit val requestDTO: OFormat[RequestDTO] = Json.format[RequestDTO]
}

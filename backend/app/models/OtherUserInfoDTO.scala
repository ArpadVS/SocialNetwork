package models

import play.api.libs.json.{Json, OFormat}

/*
  isFriend- true if authenticated user and other user are friends
  isRequestSent- true if authenticated user sent a friend request to other user
  isRequestReceived- true if other user sent request to me(authenticated)
  requestId: if isRequestReceived true, we attach the id of that request
 */
case class OtherUserInfoDTO(isFriend: Boolean, isRequestSent: Boolean, isRequestReceived: Boolean, requestId: Long)

object OtherUserInfoDTO {
  implicit val otherUserInfoDTO: OFormat[OtherUserInfoDTO] = Json.format[OtherUserInfoDTO]
}
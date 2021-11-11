package models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate


case class PostWithLiked(id: Long, userId: Long, firstName: String, lastName: String, picture: String,
                         var text: String, var likes: Int, created: LocalDate, isLiked: Boolean)

object PostWithLiked {
  implicit val postWithLiked: OFormat[PostWithLiked] = Json.format[PostWithLiked]
}
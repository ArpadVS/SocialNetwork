package models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class UserDTO(id: Long, email: String, firstName: String, lastName: String,
                   picture: String, registrationDate: LocalDate)

object UserDTO {
  implicit val userDTO: OFormat[UserDTO] = Json.format[UserDTO]

  def userToDTO(u: User): UserDTO = {
    UserDTO(u.id, u.email, u.firstName, u.lastName, u.picture, u.registrationDate)
  }

  def usersToDTOs(users: Seq[User]): Seq[UserDTO] = {
    users.map(u => UserDTO(u.id, u.email, u.firstName, u.lastName, u.picture, u.registrationDate))
  }

}

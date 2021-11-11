package utils

import org.mindrot.jbcrypt.BCrypt

object BcryptHashHelper {

  def createPassword(clearString: String): String = {
    if (clearString == "") {
      //needs throw and exception handling in controller (return BadRequest 3XX)
      throw new IllegalArgumentException("Empty string for password")
    }
    BCrypt.hashpw(clearString, BCrypt.gensalt())
  }

  def checkPassword(candidate: String, encryptedPassword: String): Boolean = {
    if (candidate == "") return false
    if (encryptedPassword == "") return false
    BCrypt.checkpw(candidate, encryptedPassword)
  }

}

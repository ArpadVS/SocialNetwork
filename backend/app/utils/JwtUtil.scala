package utils

import authentikat.jwt._

object JwtUtil {
  val JwtSecretKey = "SocialNetwork"
  val JwtSecretAlgo = "HS256"

  def createToken(payload: String): String = {
    val header = JwtHeader(JwtSecretAlgo)
    val claimsSet = JwtClaimsSet(payload)

    JsonWebToken(header, claimsSet, JwtSecretKey)
  }

  def isValidToken(jwtToken: String): Boolean =
    JsonWebToken.validate(jwtToken, JwtSecretKey)

  def decodePayload(jwtToken: String): Option[String] =
    jwtToken match {
      case JsonWebToken(_, claimsSet, _) => Option(claimsSet.asJsonString)
      case _ => None
    }
}

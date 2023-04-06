package auth

import at.favre.lib.crypto.bcrypt.BCrypt
import zio.ZLayer

case class BCryptPasswordEncoder() extends PasswordEncoder {
  private val HASH_FUNCTION_ROUND_AMOUNTS = 10

  def encode(password: String): String = {
    BCrypt.withDefaults().hashToString(HASH_FUNCTION_ROUND_AMOUNTS, password.toCharArray)
  }

  def validate(password: String, passHash: String): Boolean = {
    BCrypt.verifyer().verify(password.toCharArray, passHash).verified
  }
}

object BCryptPasswordEncoder {
  def live: ZLayer[Any, Nothing, BCryptPasswordEncoder] = ZLayer.succeed(BCryptPasswordEncoder())
}

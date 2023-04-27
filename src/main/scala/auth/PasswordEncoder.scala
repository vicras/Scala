package auth

trait PasswordEncoder {
  def encode(password: String): String

  def validate(password: String, passHash: String): Boolean
}



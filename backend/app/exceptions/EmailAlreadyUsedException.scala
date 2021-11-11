package exceptions

final case class EmailAlreadyUsedException(private val message: String = "",
                                           private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

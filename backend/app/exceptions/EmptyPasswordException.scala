package exceptions

final case class EmptyPasswordException(private val message: String = "",
                                           private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

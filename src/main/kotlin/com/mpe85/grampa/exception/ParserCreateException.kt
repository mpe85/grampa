package com.mpe85.grampa.exception

/**
 * Thrown when an error occurs during the parser creation process.
 *
 * @author mpe85
 */
class ParserCreateException : GrampaException {

  /**
   * Construct a [ParserCreateException] using an exception message.
   *
   * @param[message] An optional exception detail message
   */
  constructor(message: String?) : super(message)

  /**
   * Construct a [ParserCreateException] using an exception cause.
   *
   * @param[cause] An optional exception cause
   */
  constructor(cause: Throwable?) : super(cause)

  /**
   * Construct a [ParserCreateException] using a message and a cause.
   *
   * @param[message] An optional exception detail message
   * @param[cause] An optional exception cause
   */
  constructor(message: String?, cause: Throwable?) : super(message, cause)

  companion object {
    private const val serialVersionUID = 3813999425394378648L
  }

}

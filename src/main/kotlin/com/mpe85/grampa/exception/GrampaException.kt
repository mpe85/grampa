package com.mpe85.grampa.exception

/**
 * Base class for all Grampa exceptions
 *
 * @author mpe85
 */
open class GrampaException : RuntimeException {

  /**
   * C'tor.
   *
   * @param message An optional exception detail message
   */
  constructor(message: String?) : super(message)

  /**
   * C'tor.
   *
   * @param cause An optional exception cause
   */
  constructor(cause: Throwable?) : super(cause)

  /**
   * C'tor.
   *
   * @param message An optional exception detail message
   * @param cause An optional exception cause
   */
  constructor(message: String?, cause: Throwable?) : super(message, cause)

  companion object {
    private const val serialVersionUID = 5796921255320228162L
  }

}
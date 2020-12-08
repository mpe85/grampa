package com.mpe85.grampa.exception

/**
 * Thrown when an error occurs during the run of an action.
 *
 * @author mpe85
 */
class ActionRunException : GrampaException {

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
    private const val serialVersionUID = -1674353148882821963L
  }

}
package com.mpe85.grampa.event

import org.greenrobot.eventbus.Subscribe

/**
 * Abstract listener for parse events. Concrete listeners must extend this class.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
abstract class ParseEventListener<T> {

  /**
   * Called on a [PreParseEvent].
   *
   * @param[event] The posted event
   */
  @Subscribe
  open fun beforeParse(event: PreParseEvent<T>) {
  }

  /**
   * Called on a [PreMatchEvent].
   *
   * @param[event] The posted event
   */
  @Subscribe
  open fun beforeMatch(event: PreMatchEvent<T>) {
  }

  /**
   * Called on a [MatchSuccessEvent].
   *
   * @param[event] The posted event
   */
  @Subscribe
  open fun afterMatchSuccess(event: MatchSuccessEvent<T>) {
  }

  /**
   * Called on a [MatchFailureEvent].
   *
   * @param[event] The posted event
   */
  @Subscribe
  fun afterMatchFailure(event: MatchFailureEvent<T>) {
  }

  /**
   * Called on a [PostParseEvent].
   *
   * @param[event] The posted event
   */
  @Subscribe
  open fun afterParse(event: PostParseEvent<T>) {
  }

}

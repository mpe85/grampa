package com.mpe85.grampa.event

import org.greenrobot.eventbus.Subscribe

/**
 * Listener for parse events. Concrete listeners should extend this class.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
open class ParseEventListener<T> {

  /**
   * Called on a [PreParseEvent].
   *
   * @param[event] The posted event
   */
  @Subscribe
  open fun beforeParse(event: PreParseEvent<T>) = Unit

  /**
   * Called on a [PreMatchEvent].
   *
   * @param[event] The posted event
   */
  @Subscribe
  open fun beforeMatch(event: PreMatchEvent<T>) = Unit

  /**
   * Called on a [MatchSuccessEvent].
   *
   * @param[event] The posted event
   */
  @Subscribe
  open fun afterMatchSuccess(event: MatchSuccessEvent<T>) = Unit

  /**
   * Called on a [MatchFailureEvent].
   *
   * @param[event] The posted event
   */
  @Subscribe
  fun afterMatchFailure(event: MatchFailureEvent<T>) = Unit

  /**
   * Called on a [PostParseEvent].
   *
   * @param[event] The posted event
   */
  @Subscribe
  open fun afterParse(event: PostParseEvent<T>) = Unit

}

package com.mpe85.grampa.event

import org.greenrobot.eventbus.Subscribe

/**
 * Abstract listener for parse events. Concrete listeners must extend this class.
 *
 * @author mpe85
 * @param T the type of the stack elements
 */
abstract class ParseEventListener<T> {

  @Subscribe
  open fun beforeParse(event: PreParseEvent<T>) {
  }

  @Subscribe
  open fun beforeMatch(event: PreMatchEvent<T>) {
  }

  @Subscribe
  open fun afterMatchSuccess(event: MatchSuccessEvent<T>) {
  }

  @Subscribe
  fun afterMatchFailure(event: MatchFailureEvent<T>) {
  }

  @Subscribe
  open fun afterParse(event: PostParseEvent<T>) {
  }

}

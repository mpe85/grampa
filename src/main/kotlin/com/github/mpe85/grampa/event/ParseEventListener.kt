package com.github.mpe85.grampa.event

import org.greenrobot.eventbus.Subscribe

/**
 * Listener for parse events. Concrete listeners should extend this class.
 *
 * @param[T] The type of the stack elements
 * @author mpe85
 */
public open class ParseEventListener<T> {

    /**
     * Called on a [PreParseEvent].
     *
     * @param[event] The posted event
     */
    @Subscribe public open fun beforeParse(event: PreParseEvent<T>): Unit = Unit

    /**
     * Called on a [PreMatchEvent].
     *
     * @param[event] The posted event
     */
    @Subscribe public open fun beforeMatch(event: PreMatchEvent<T>): Unit = Unit

    /**
     * Called on a [MatchSuccessEvent].
     *
     * @param[event] The posted event
     */
    @Subscribe public open fun afterMatchSuccess(event: MatchSuccessEvent<T>): Unit = Unit

    /**
     * Called on a [MatchFailureEvent].
     *
     * @param[event] The posted event
     */
    @Subscribe public open fun afterMatchFailure(event: MatchFailureEvent<T>): Unit = Unit

    /**
     * Called on a [PostParseEvent].
     *
     * @param[event] The posted event
     */
    @Subscribe public open fun afterParse(event: PostParseEvent<T>): Unit = Unit
}

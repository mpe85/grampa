package com.mpe85.grampa.event

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.SubscriberExceptionContext
import com.mpe85.grampa.rule.RuleContext
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.mockk.mockk
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SuppressFBWarnings(
  value = ["SIC_INNER_SHOULD_BE_STATIC_ANON"],
  justification = "Performance is not of great importance in unit tests."
)
@TestInstance(PER_CLASS)
class ParseEventListenerTest {

  @Test
  fun postEvent_valid() {
    class TestListener : ParseEventListener<String>() {
      override fun beforeParse(event: PreParseEvent<String>) {
        called = true
      }

      var called = false
    }

    val listener = TestListener()
    val bus = EventBus()
    bus.register(listener)
    bus.post(PreParseEvent(mockk<RuleContext<String>>(relaxed = true)))
    assertTrue(listener.called)
  }

  @Test
  fun postEvent_exception() {
    class TestListener : ParseEventListener<String>() {
      override fun beforeParse(event: PreParseEvent<String>) {
        throw RuntimeException("failure")
      }
    }

    val listener = TestListener()
    val sb = StringBuilder()
    val bus = EventBus { ex: Throwable, _: SubscriberExceptionContext? -> sb.append(ex.message) }
    bus.register(listener)
    bus.post(PreParseEvent(mockk<RuleContext<String>>(relaxed = true)))
    assertEquals("failure", sb.toString())
  }
}

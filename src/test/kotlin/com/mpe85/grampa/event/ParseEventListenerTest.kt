package com.mpe85.grampa.event

import com.mpe85.grampa.rule.RuleContext
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.SubscriberExceptionEvent
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

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
    val bus = EventBus.builder().logNoSubscriberMessages(false).build()
    bus.register(listener)
    bus.post(PreParseEvent(mockk<RuleContext<String>>(relaxed = true)))
    assertTrue(listener.called)
  }

  @Test
  fun postEvent_exception() {
    val ex = RuntimeException("failure")


    class TestListener : ParseEventListener<String>() {
      override fun beforeParse(event: PreParseEvent<String>) = throw ex

      @Subscribe
      fun exHandler(event: SubscriberExceptionEvent) {
        exEvent = event
      }

      var exEvent: SubscriberExceptionEvent? = null
    }

    val listener = TestListener()
    val bus = EventBus.builder().logNoSubscriberMessages(false).build()
    val event = PreParseEvent(mockk<RuleContext<String>>(relaxed = true))
    bus.register(listener)
    bus.post(event)

    assertNotNull(listener.exEvent)
    assertEquals(listener, listener.exEvent?.causingSubscriber)
    assertEquals(event, listener.exEvent?.causingEvent)
    assertEquals(ex, listener.exEvent?.throwable)
  }
}

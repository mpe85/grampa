package com.github.mpe85.grampa.event

import com.github.mpe85.grampa.context.ParserContext
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.SubscriberExceptionEvent

class ParseEventListenerTests : StringSpec({
    "Listen to event" {
        val listener = object : ParseEventListener<String>() {
            var called = false
            override fun beforeParse(event: PreParseEvent<String>) {
                called = true
            }
        }

        EventBus.builder().logNoSubscriberMessages(false).build().apply {
            register(listener)
            post(PreParseEvent(mockk<ParserContext<String>>(relaxed = true)))
        }

        listener.called shouldBe true
    }
    "Handle subscriber exception" {
        val ex = RuntimeException("failure")

        val listener = object : ParseEventListener<String>() {
            var exEvent: SubscriberExceptionEvent? = null
            override fun beforeParse(event: PreParseEvent<String>) = throw ex

            @Subscribe
            fun exHandler(event: SubscriberExceptionEvent) {
                exEvent = event
            }
        }

        val event = PreParseEvent(mockk<ParserContext<String>>(relaxed = true))
        EventBus.builder().logNoSubscriberMessages(false).build().apply {
            register(listener)
            post(event)
        }

        listener.exEvent shouldNotBe null
        listener.exEvent?.causingSubscriber shouldBe listener
        listener.exEvent?.causingEvent shouldBe event
        listener.exEvent?.throwable shouldBe ex
    }
})

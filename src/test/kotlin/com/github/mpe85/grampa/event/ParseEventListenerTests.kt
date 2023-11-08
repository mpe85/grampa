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
    "ParseEventListener is called when event is posted" {
        val listener = object : ParseEventListener<String>() {
            var called = false
            override fun beforeParse(event: PreParseEvent<String>) {
                called = true
            }
        }

        EventBus.builder().logNoSubscriberMessages(false).build().apply {
            register(listener)
            post(PreParseEvent(mockk<ParserContext<String>>()))
        }

        listener.called shouldBe true
    }
    "SubscriberExceptionEvent is posted when handler of ParseEventListener throws exception" {
        val ex = RuntimeException("failure")

        val listener = object : ParseEventListener<String>() {
            var exEvent: SubscriberExceptionEvent? = null
            override fun beforeParse(event: PreParseEvent<String>) = throw ex

            @Subscribe
            fun exHandler(event: SubscriberExceptionEvent) {
                exEvent = event
            }
        }

        val event = PreParseEvent(mockk<ParserContext<String>>())
        EventBus.builder().logNoSubscriberMessages(false).build().apply {
            register(listener)
            post(event)
        }

        listener.exEvent shouldNotBe null
        listener.exEvent?.causingSubscriber shouldBe listener
        listener.exEvent?.causingEvent shouldBe event
        listener.exEvent?.throwable shouldBe ex
    }
    "ParseEventListener methods return Unit" {
        ParseEventListener<Unit>().apply {
            beforeParse(mockk()) shouldBe Unit
            beforeMatch(mockk()) shouldBe Unit
            afterMatchSuccess(mockk()) shouldBe Unit
            afterMatchFailure(mockk()) shouldBe Unit
            afterParse(mockk()) shouldBe Unit
        }
    }
})

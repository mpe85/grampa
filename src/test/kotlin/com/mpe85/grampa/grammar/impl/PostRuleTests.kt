package com.mpe85.grampa.grammar.impl

import com.mpe85.grampa.event.MatchSuccessEvent
import com.mpe85.grampa.event.ParseEventListener
import com.mpe85.grampa.event.PostParseEvent
import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll
import org.greenrobot.eventbus.Subscribe

class PostRuleTests : StringSpec({
    class Listener : ParseEventListener<Unit>() {
        var event: Int? = null
            private set

        @Subscribe
        fun onInt(event: Int?) {
            this.event = event
        }

        override fun afterMatchSuccess(event: MatchSuccessEvent<Unit>) = event.context shouldNotBe null
        override fun afterParse(event: PostParseEvent<Unit>) = event.result shouldNotBe null
    }
    "Post(event) rule posts value to event bus" {
        checkAll<Int> { int ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = post(int)
            }).apply {
                val listener = Listener().also {
                    registerListener(it)
                }
                run("").apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe ""
                    restOfInput shouldBe ""
                    listener.event shouldBe int
                }
            }
        }
    }
    "Post(supplier) rule posts supplied value to event bus" {
        checkAll<Int> { int ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = post { int }
            }).apply {
                val listener = Listener().also {
                    registerListener(it)
                }
                run("").apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe ""
                    restOfInput shouldBe ""
                    listener.event shouldBe int
                }
            }
        }
    }
})

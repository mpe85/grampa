package com.mpe85.grampa.parser

import com.mpe85.grampa.event.ParseEventListener
import com.mpe85.grampa.event.PreMatchEvent
import com.mpe85.grampa.grammar.impl.AbstractGrammar
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.StringSpec

class ParserTests : StringSpec({
    class IntegerTestListener : ParseEventListener<Int>() {
        override fun beforeMatch(event: PreMatchEvent<Int>) = throw RuntimeException()
    }
    "Register listener" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = empty()
        }).apply {
            shouldNotThrowAny { registerListener(IntegerTestListener()) }
            shouldNotThrowAny { run("a") }
        }
    }
    "Unregister listener" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = empty()
        }).apply {
            IntegerTestListener().let {
                shouldNotThrowAny { registerListener(it) }
                shouldNotThrowAny { unregisterListener(it) }
            }
            shouldNotThrowAny { run("a") }
        }
    }
})

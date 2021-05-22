package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.rule.impl.toRule
import com.ibm.icu.lang.UCharacter.toString
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll

class RepeatRuleTests : StringSpec({
    "Repeat(n) rule matches if child rule matches exactly n times" {
        checkAll(legalCodePoints(), Arb.int(2..10)) { cp, n ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = repeat(cp.value.toRule(), n)
            }).apply {
                val repeated = toString(cp.value).repeat(n)
                run(repeated).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe repeated
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Repeat(n) rule does not match if child rule matches less than n times" {
        checkAll(legalCodePoints(), Arb.int(2..10)) { cp, n ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = repeat(cp.value.toRule(), n)
            }).apply {
                val repeated = toString(cp.value).repeat(n - 1)
                run(repeated).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe repeated
                }
            }
        }
    }
    "Repeat(n) rule matches if child rule matches more than n times" {
        checkAll(legalCodePoints(), Arb.int(2..10)) { cp, n ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = repeat(cp.value.toRule(), n)
            }).apply {
                val repeated = toString(cp.value).repeat(n + 1)
                run(repeated).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe repeated.removeSuffix(toString(cp.value))
                    restOfInput shouldBe toString(cp.value)
                }
            }
        }
    }
    "Repeat(min,max) rule matches if child rule matches exactly min times" {
        checkAll(legalCodePoints(), Arb.set(Arb.int(2..10), 2)) { cp, ints ->
            val (min, max) = ints.sorted()
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = repeat(cp.value.toRule(), min, max)
            }).apply {
                val repeated = toString(cp.value).repeat(min)
                run(repeated).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe repeated
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Repeat(min,max) rule matches if child rule matches exactly max times" {
        checkAll(legalCodePoints(), Arb.set(Arb.int(2..10), 2)) { cp, ints ->
            val (min, max) = ints.sorted()
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = repeat(cp.value.toRule(), min, max)
            }).apply {
                val repeated = toString(cp.value).repeat(max)
                run(repeated).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe repeated
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Repeat(min,max) rule does not match if child rule matches less than min times" {
        checkAll(legalCodePoints(), Arb.set(Arb.int(2..10), 2)) { cp, ints ->
            val (min, max) = ints.sorted()
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = repeat(cp.value.toRule(), min, max)
            }).apply {
                val repeated = toString(cp.value).repeat(min - 1)
                run(repeated).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe repeated
                }
            }
        }
    }
    "Repeat(min,max) rule matches if child rule matches more than max times" {
        checkAll(legalCodePoints(), Arb.set(Arb.int(2..10), 2)) { cp, ints ->
            val (min, max) = ints.sorted()
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = repeat(cp.value.toRule(), min, max)
            }).apply {
                val repeated = toString(cp.value).repeat(max + 1)
                run(repeated).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe repeated.removeSuffix(toString(cp.value))
                    restOfInput shouldBe toString(cp.value)
                }
            }
        }
    }
    "Repeat(max) rule matches if child rule matches exactly max times" {
        checkAll(legalCodePoints(), Arb.int(2..10)) { cp, max ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = repeat(cp.value.toRule(), max = max)
            }).apply {
                val repeated = toString(cp.value).repeat(max)
                run(repeated).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe repeated
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Repeat(max) rule matches if child rule matches less than max times" {
        checkAll(legalCodePoints(), Arb.int(2..10)) { cp, max ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = repeat(cp.value.toRule(), max = max)
            }).apply {
                val repeated = toString(cp.value).repeat(max - 1)
                run(repeated).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe repeated
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Repeat(max) rule matches if child rule matches more than max times" {
        checkAll(legalCodePoints(), Arb.int(2..10)) { cp, max ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = repeat(cp.value.toRule(), max = max)
            }).apply {
                val repeated = toString(cp.value).repeat(max + 1)
                run(repeated).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe repeated.removeSuffix(toString(cp.value))
                    restOfInput shouldBe toString(cp.value)
                }
            }
        }
    }
    "Repeat(min) rule matches if child rule matches exactly min times" {
        checkAll(legalCodePoints(), Arb.int(2..10)) { cp, min ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = repeat(cp.value.toRule(), min = min)
            }).apply {
                val repeated = toString(cp.value).repeat(min)
                run(repeated).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe repeated
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Repeat(min) rule does not match if child rule matches less than min times" {
        checkAll(legalCodePoints(), Arb.int(2..10)) { cp, min ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = repeat(cp.value.toRule(), min = min)
            }).apply {
                val repeated = toString(cp.value).repeat(min - 1)
                run(repeated).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe repeated
                }
            }
        }
    }
    "Repeat(min) rule matches if child rule matches more than min times" {
        checkAll(legalCodePoints(), Arb.int(2..10)) { cp, min ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = repeat(cp.value.toRule(), min = min)
            }).apply {
                val repeated = toString(cp.value).repeat(min + 1)
                run(repeated).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe repeated
                    restOfInput shouldBe ""
                }
            }
        }
    }
})

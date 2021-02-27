package com.mpe85.grampa.util

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.checkAll

class UnboundedRangeTests : StringSpec({
    "Unbounded range with lower limit" {
        checkAll(Arb.positiveInts()) { int ->
            range(int).apply {
                min shouldBe int
                max shouldBe null
            }
            min(int).apply {
                min shouldBe int
                max shouldBe null
            }
            range(int) shouldBe min(int)
        }
    }
    "Unbounded range with upper limit" {
        checkAll(Arb.positiveInts()) { int ->
            range(max = int).apply {
                min shouldBe 0
                max shouldBe int
            }
            max(int).apply {
                min shouldBe 0
                max shouldBe int
            }
            range(max = int) shouldBe max(int)
        }
    }
    "Unbounded range with lower and upper limit" {
        checkAll(Arb.positiveInts(), Arb.positiveInts()) { int1, int2 ->
            range(int1, int2).apply {
                min shouldBe int1
                max shouldBe int2
            }
        }
    }
})

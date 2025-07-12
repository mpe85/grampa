package com.github.mpe85.grampa.context

import com.github.mpe85.grampa.input.StringInputBuffer
import com.github.mpe85.grampa.stack.LinkedListRestorableStack
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.negativeInt
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.mockk

class ContextTests :
    StringSpec({
        "Advancing index with positive delta succeeds when rest of input is long enough" {
            checkAll(Arb.string(10), Arb.int(0..10)) { str, int ->
                Context<String>(
                        ContextState(
                            StringInputBuffer(str),
                            0,
                            mockk(),
                            0,
                            LinkedListRestorableStack(),
                            mockk(),
                        )
                    )
                    .apply { advanceIndex(int) shouldBe true }
            }
        }
        "Advancing index with positive delta fails when rest of input is not long enough" {
            checkAll(Arb.string(10), Arb.int(11..20)) { str, int ->
                Context<String>(
                        ContextState(
                            StringInputBuffer(str),
                            0,
                            mockk(),
                            0,
                            LinkedListRestorableStack(),
                            mockk(),
                        )
                    )
                    .apply { advanceIndex(int) shouldBe false }
            }
        }
        "Advancing index with negative delta throws IllegalArgumentException" {
            checkAll(Arb.negativeInt()) { int ->
                Context<String>(
                        ContextState(mockk(), 0, mockk(), 0, LinkedListRestorableStack(), mockk())
                    )
                    .apply { shouldThrow<IllegalArgumentException> { advanceIndex(int) } }
            }
        }
    })

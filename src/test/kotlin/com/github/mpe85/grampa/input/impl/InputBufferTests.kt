package com.github.mpe85.grampa.input.impl

import com.ibm.icu.impl.StringSegment
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class InputBufferTests : StringSpec({
    val buffers = listOf(
        StringBufferInputBuffer(StringBuffer("foobar")),
        StringBuilderInputBuffer(StringBuilder("foobar")),
        StringSegmentInputBuffer(StringSegment("foobar", false)),
        StringInputBuffer("foobar")
    )
    "Get character" {
        buffers.forAll { buffer ->
            buffer.getChar(0) shouldBe 'f'
            buffer.getChar(1) shouldBe 'o'
            buffer.getChar(2) shouldBe 'o'
            buffer.getChar(3) shouldBe 'b'
            buffer.getChar(4) shouldBe 'a'
            buffer.getChar(5) shouldBe 'r'
            shouldThrow<IndexOutOfBoundsException> {
                buffer.getChar(-1)
            }
            shouldThrow<IndexOutOfBoundsException> {
                buffer.getChar(6)
            }
        }
    }
    "Get code point" {
        buffers.forAll { buffer ->
            buffer.getCodePoint(0) shouldBe 'f'.toInt()
            buffer.getCodePoint(1) shouldBe 'o'.toInt()
            buffer.getCodePoint(2) shouldBe 'o'.toInt()
            buffer.getCodePoint(3) shouldBe 'b'.toInt()
            buffer.getCodePoint(4) shouldBe 'a'.toInt()
            buffer.getCodePoint(5) shouldBe 'r'.toInt()
            shouldThrow<IndexOutOfBoundsException> {
                try {
                    buffer.getCodePoint(-1)
                } catch (ex: Exception) {
                    println(ex)
                    throw ex
                }

            }
            shouldThrow<IndexOutOfBoundsException> {
                buffer.getCodePoint(6)
            }
        }
    }
    "Get length" {
        buffers.forAll { buffer ->
            buffer.length shouldBe 6
        }
    }
    "Sub sequence" {
        buffers.forAll { buffer ->
            buffer.subSequence(0, 6) shouldBe "foobar"
            buffer.subSequence(0, 1) shouldBe "f"
            buffer.subSequence(1, 3) shouldBe "oo"
            buffer.subSequence(5, 5) shouldBe ""
            shouldThrow<IndexOutOfBoundsException> {
                buffer.subSequence(-2, 1)
            }
            shouldThrow<IndexOutOfBoundsException> {
                buffer.subSequence(0, 7)
            }
            shouldThrow<IndexOutOfBoundsException> {
                buffer.subSequence(5, 3)
            }
        }
    }
    "Get position" {
        buffers.forAll { buffer ->
            (0..5).forEach { idx ->
                buffer.getPosition(idx).apply {
                    line shouldBe 1
                    column shouldBe idx + 1
                }
            }
            shouldThrow<IndexOutOfBoundsException> {
                buffer.getPosition(-1)
            }
            shouldThrow<IndexOutOfBoundsException> {
                buffer.getPosition(6)
            }
        }
    }
})

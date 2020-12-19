package com.mpe85.grampa.input.impl

import com.ibm.icu.impl.StringSegment
import com.mpe85.grampa.input.InputBuffer
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(PER_CLASS)
class InputBufferTest {

  @ParameterizedTest
  @MethodSource("provideInputBuffers")
  fun test_ctor_valid(ib: InputBuffer) {
    assertDoesNotThrow { ib }
  }

  @ParameterizedTest
  @MethodSource("provideInputBuffers")
  fun test_getChar_valid(ib: InputBuffer) {
    assertEquals('f', ib.getChar(0))
    assertEquals('o', ib.getChar(1))
    assertEquals('o', ib.getChar(2))
    assertEquals('b', ib.getChar(3))
    assertEquals('a', ib.getChar(4))
    assertEquals('r', ib.getChar(5))
  }

  @ParameterizedTest
  @MethodSource("provideInputBuffers")
  fun test_getChar_invalid_indexOutOfRange(ib: InputBuffer) {
    assertThrows(IllegalArgumentException::class.java) { ib.getChar(-1) }
    assertThrows(IllegalArgumentException::class.java) { ib.getChar(6) }
  }

  @ParameterizedTest
  @MethodSource("provideInputBuffers")
  fun test_getCodePoint_valid(ib: InputBuffer) {
    assertEquals('f'.toInt(), ib.getCodePoint(0))
    assertEquals('o'.toInt(), ib.getCodePoint(1))
    assertEquals('o'.toInt(), ib.getCodePoint(2))
    assertEquals('b'.toInt(), ib.getCodePoint(3))
    assertEquals('a'.toInt(), ib.getCodePoint(4))
    assertEquals('r'.toInt(), ib.getCodePoint(5))
  }

  @ParameterizedTest
  @MethodSource("provideInputBuffers")
  fun test_getCodePoint_invalid_indexOutOfRange(ib: InputBuffer) {
    assertThrows(IllegalArgumentException::class.java) { ib.getCodePoint(-1) }
    assertThrows(IllegalArgumentException::class.java) { ib.getCodePoint(6) }
  }

  @ParameterizedTest
  @MethodSource("provideInputBuffers")
  fun test_getLength_valid(ib: InputBuffer) {
    assertEquals(6, ib.length)
  }

  @ParameterizedTest
  @MethodSource("provideInputBuffers")
  fun test_subSequence_valid(ib: InputBuffer) {
    assertEquals("foobar", ib.subSequence(0, 6))
    assertEquals("f", ib.subSequence(0, 1))
    assertEquals("oo", ib.subSequence(1, 3))
    assertEquals("", ib.subSequence(5, 5))
  }

  @ParameterizedTest
  @MethodSource("provideInputBuffers")
  fun test_subSequence_invalid_indexOutOfRange(ib: InputBuffer) {
    assertThrows(IllegalArgumentException::class.java) { ib.subSequence(-2, 1) }
    assertThrows(IllegalArgumentException::class.java) { ib.subSequence(0, 7) }
    assertThrows(IllegalArgumentException::class.java) { ib.subSequence(5, 3) }
  }

  companion object {
    @SuppressFBWarnings(
      value = ["UPM_UNCALLED_PRIVATE_METHOD"],
      justification = "It's actually used as a factory method for parameterized tests."
    )
    @JvmStatic
    private fun provideInputBuffers(): Stream<InputBuffer> {
      return Stream.of(
        StringBufferInputBuffer(StringBuffer("foobar")),
        StringBuilderInputBuffer(StringBuilder("foobar")),
        StringSegmentInputBuffer(StringSegment("foobar", false)),
        StringInputBuffer("foobar")
      )
    }
  }

}

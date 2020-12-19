package com.mpe85.grampa.input.impl

import com.mpe85.grampa.input.InputPosition
import com.mpe85.grampa.input.LineCounter
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

@TestInstance(PER_CLASS)
class CharSequenceLineCounterTest {

  @Test
  fun test_getLineCount_valid() {
    val lc: LineCounter = CharSequenceLineCounter("foo\n  \nbar\n  ")
    assertEquals(4, lc.lineCount)
  }

  @Test
  fun test_ctor_valid() {
    assertDoesNotThrow { CharSequenceLineCounter("foobar") }
  }

  @Test
  fun test_getPosition_valid_noLineBreakAtEnd() {
    val lc: LineCounter = CharSequenceLineCounter("foo\nbar")
    assertEquals(2, lc.lineCount)

    // 'f'
    var pos: InputPosition = lc.getPosition(0)
    assertEquals(1, pos.line)
    assertEquals(1, pos.column)

    // 'o'
    pos = lc.getPosition(1)
    assertEquals(1, pos.line)
    assertEquals(2, pos.column)

    // 'o'
    pos = lc.getPosition(2)
    assertEquals(1, pos.line)
    assertEquals(3, pos.column)

    // '\n'
    pos = lc.getPosition(3)
    assertEquals(1, pos.line)
    assertEquals(4, pos.column)

    // 'b'
    pos = lc.getPosition(4)
    assertEquals(2, pos.line)
    assertEquals(1, pos.column)

    // 'a'
    pos = lc.getPosition(5)
    assertEquals(2, pos.line)
    assertEquals(2, pos.column)

    // 'r'
    pos = lc.getPosition(6)
    assertEquals(2, pos.line)
    assertEquals(3, pos.column)
  }

  @Test
  fun test_getPosition_valid_blankLines() {
    val lc: LineCounter = CharSequenceLineCounter("\n\n\n")
    assertEquals(3, lc.lineCount)

    // 1st line break
    var pos: InputPosition = lc.getPosition(0)
    assertEquals(1, pos.line)
    assertEquals(1, pos.column)

    // 2nd line break
    pos = lc.getPosition(1)
    assertEquals(2, pos.line)
    assertEquals(1, pos.column)

    // 3rd line break
    pos = lc.getPosition(2)
    assertEquals(3, pos.line)
    assertEquals(1, pos.column)
  }

  @Test
  fun test_getPosition_invalid_indexOutOfRange() {
    val lc: LineCounter = CharSequenceLineCounter("foobar")
    assertEquals(1, lc.lineCount)
    assertThrows<IllegalArgumentException> { lc.getPosition(-4711) }
    assertThrows<IllegalArgumentException> { lc.getPosition(-1) }
    assertThrows<IllegalArgumentException> { lc.getPosition(6) }
    assertThrows<IllegalArgumentException> { lc.getPosition(666) }
    assertDoesNotThrow { lc.getPosition(0) }
    assertDoesNotThrow { lc.getPosition(5) }
  }
}

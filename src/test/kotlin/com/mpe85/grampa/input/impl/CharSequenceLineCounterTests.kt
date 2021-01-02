package com.mpe85.grampa.input.impl

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CharSequenceLineCounterTests : StringSpec({
  "Get line count" {
    CharSequenceLineCounter("").lineCount shouldBe 0
    CharSequenceLineCounter(" ").lineCount shouldBe 1
    CharSequenceLineCounter(" \n").lineCount shouldBe 1
    CharSequenceLineCounter("\n").lineCount shouldBe 1
    CharSequenceLineCounter("\n ").lineCount shouldBe 2
    CharSequenceLineCounter("foo\n  \nbar\n  ").lineCount shouldBe 4
  }
  "Get position" {
    CharSequenceLineCounter("foo\nbar").apply {
      lineCount shouldBe 2
      getPosition(0).apply {
        line shouldBe 1
        column shouldBe 1
      }
      getPosition(1).apply {
        line shouldBe 1
        column shouldBe 2
      }
      getPosition(2).apply {
        line shouldBe 1
        column shouldBe 3
      }
      getPosition(3).apply {
        line shouldBe 1
        column shouldBe 4
      }
      getPosition(4).apply {
        line shouldBe 2
        column shouldBe 1
      }
      getPosition(5).apply {
        line shouldBe 2
        column shouldBe 2
      }
      getPosition(6).apply {
        line shouldBe 2
        column shouldBe 3
      }
    }
    CharSequenceLineCounter("\n\n\n").apply {
      lineCount shouldBe 3
      getPosition(0).apply {
        line shouldBe 1
        column shouldBe 1
      }
      getPosition(1).apply {
        line shouldBe 2
        column shouldBe 1
      }
      getPosition(2).apply {
        line shouldBe 3
        column shouldBe 1
      }
    }
    CharSequenceLineCounter("foobar").apply {
      lineCount shouldBe 1
      shouldThrow<IndexOutOfBoundsException> {
        getPosition(-4711)
      }
      shouldThrow<IndexOutOfBoundsException> {
        getPosition(-1)
      }
      shouldThrow<IndexOutOfBoundsException> {
        getPosition(6)
      }
      shouldThrow<IndexOutOfBoundsException> {
        getPosition(666)
      }
      shouldNotThrowAny {
        getPosition(0)
        getPosition(5)
      }
    }
  }
})

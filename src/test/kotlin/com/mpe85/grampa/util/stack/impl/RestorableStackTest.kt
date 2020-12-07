package com.mpe85.grampa.util.stack.impl

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RestorableStackTest {

  @Test
  fun test_push_valid() {
    val stack1 = LinkedListRestorableStack<Number>()
    val stack2 = LinkedListRestorableStack<Number>()

    stack1.push(1)
    stack1.push(2)
    stack1.push(3)

    stack2.push(0, 3)
    stack2.push(1, 2)
    stack2.push(2, 1)

    Assertions.assertEquals(3, stack1.size)
    Assertions.assertEquals(3, stack2.size)
    Assertions.assertEquals(stack1, stack2)
  }

  @Test
  fun test_peek_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.push(2)
    stack.push(3)

    Assertions.assertEquals(3, stack.size)
    Assertions.assertEquals(3, stack.peek())
    Assertions.assertEquals(3, stack.peek(0))
    Assertions.assertEquals(2, stack.peek(1))
    Assertions.assertEquals(1, stack.peek(2))
    Assertions.assertEquals(3, stack.size)
  }

  @Test
  fun test_peekAs_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.push(2)
    stack.push(3.3f)

    Assertions.assertEquals(3, stack.size)
    Assertions.assertEquals(3.3f, stack.peekAs(Float::class.javaObjectType).toFloat())
    Assertions.assertEquals(1, stack.peekAs(2, Int::class.javaObjectType).toInt())
    Assertions.assertEquals(3, stack.size)
  }

  @Test
  fun test_pop_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.push(2)
    stack.push(3)

    Assertions.assertEquals(3, stack.size)
    Assertions.assertEquals(3, stack.pop())
    Assertions.assertEquals(2, stack.size)
    Assertions.assertEquals(1, stack.pop(1))
    Assertions.assertEquals(1, stack.size)
    Assertions.assertEquals(2, stack.pop(0))
    Assertions.assertEquals(0, stack.size)
  }

  @Test
  fun test_popAS_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.push(2.2)
    stack.push(3)

    Assertions.assertEquals(3, stack.size)
    Assertions.assertEquals(2.2, stack.popAs(1, Double::class.javaObjectType).toDouble())
    Assertions.assertEquals(2, stack.size)
    Assertions.assertEquals(3, stack.popAs(Int::class.javaObjectType).toInt())
  }

  @Test
  fun test_poke_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.push(2)
    stack.push(3)

    Assertions.assertEquals(3, stack.size)
    Assertions.assertEquals(3, stack.poke(4))
    Assertions.assertEquals(4, stack.peek())
    Assertions.assertEquals(3, stack.size)
    Assertions.assertEquals(2, stack.poke(1, 5))
    Assertions.assertEquals(5, stack.peek(1))
    Assertions.assertEquals(3, stack.size)
  }

  @Test
  fun test_dup_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.dup()
    stack.dup()

    Assertions.assertEquals(3, stack.size)
    Assertions.assertEquals(1, stack.peek())
    Assertions.assertEquals(1, stack.peek(1))
    Assertions.assertEquals(1, stack.peek(2))
  }

  @Test
  fun test_swap_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.push(2)
    stack.push(3)
    stack.swap()

    Assertions.assertEquals(3, stack.size)
    Assertions.assertEquals(2, stack.peek())
    Assertions.assertEquals(3, stack.peek(1))
    Assertions.assertEquals(1, stack.peek(2))
  }

  @Test
  fun test_restoreSnapshot_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.takeSnapshot()
    stack.push(2)
    stack.push(3)
    stack.takeSnapshot()
    stack.pop(1)

    Assertions.assertEquals(2, stack.size)
    Assertions.assertEquals(2, stack.snapshotCount)

    stack.restoreSnapshot()

    Assertions.assertEquals(3, stack.size)
    Assertions.assertEquals(3, stack.peek())
    Assertions.assertEquals(2, stack.peek(1))
    Assertions.assertEquals(1, stack.peek(2))
    Assertions.assertEquals(1, stack.snapshotCount)

    stack.restoreSnapshot()

    Assertions.assertEquals(1, stack.size)
    Assertions.assertEquals(1, stack.peek())
    Assertions.assertEquals(0, stack.snapshotCount)
  }

  @Test
  fun test_discardSnapshot_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.takeSnapshot()
    stack.push(2)
    stack.takeSnapshot()

    Assertions.assertEquals(2, stack.snapshotCount)

    stack.discardSnapshot()

    Assertions.assertEquals(1, stack.snapshotCount)

    stack.discardSnapshot()

    Assertions.assertEquals(0, stack.snapshotCount)
  }

  @Test
  fun test_clearAllSnapshots_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.takeSnapshot()
    stack.push(2)
    stack.takeSnapshot()

    Assertions.assertEquals(2, stack.snapshotCount)

    stack.clearAllSnapshots()

    Assertions.assertEquals(0, stack.snapshotCount)
  }
}
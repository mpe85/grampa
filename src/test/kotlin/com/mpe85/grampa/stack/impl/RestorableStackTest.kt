package com.mpe85.grampa.stack.impl

import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
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

    assertEquals(3, stack1.size)
    assertEquals(3, stack2.size)
    assertEquals(stack1, stack2)
  }

  @Test
  fun test_peek_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.push(2)
    stack.push(3)

    assertEquals(3, stack.size)
    assertEquals(3, stack.peek())
    assertEquals(3, stack.peek(0))
    assertEquals(2, stack.peek(1))
    assertEquals(1, stack.peek(2))
    assertEquals(3, stack.size)
  }

  @Test
  fun test_peekAs_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.push(2)
    stack.push(3.3f)

    assertEquals(3, stack.size)
    assertEquals(3.3f, stack.peek().toFloat())
    assertEquals(1, stack.peek(2).toInt())
    assertEquals(3, stack.size)
  }

  @Test
  fun test_pop_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.push(2)
    stack.push(3)

    assertEquals(3, stack.size)
    assertEquals(3, stack.pop())
    assertEquals(2, stack.size)
    assertEquals(1, stack.pop(1))
    assertEquals(1, stack.size)
    assertEquals(2, stack.pop(0))
    assertEquals(0, stack.size)
  }

  @Test
  fun test_popAS_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.push(2.2)
    stack.push(3)

    assertEquals(3, stack.size)
    assertEquals(2.2, stack.pop(1).toDouble())
    assertEquals(2, stack.size)
    assertEquals(3, stack.pop().toInt())
  }

  @Test
  fun test_poke_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.push(2)
    stack.push(3)

    assertEquals(3, stack.size)
    assertEquals(3, stack.poke(4))
    assertEquals(4, stack.peek())
    assertEquals(3, stack.size)
    assertEquals(2, stack.poke(1, 5))
    assertEquals(5, stack.peek(1))
    assertEquals(3, stack.size)
  }

  @Test
  fun test_dup_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.dup()
    stack.dup()

    assertEquals(3, stack.size)
    assertEquals(1, stack.peek())
    assertEquals(1, stack.peek(1))
    assertEquals(1, stack.peek(2))
  }

  @Test
  fun test_swap_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.push(2)
    stack.push(3)
    stack.swap()

    assertEquals(3, stack.size)
    assertEquals(2, stack.peek())
    assertEquals(3, stack.peek(1))
    assertEquals(1, stack.peek(2))
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

    assertEquals(2, stack.size)
    assertEquals(2, stack.snapshotCount)

    stack.restoreSnapshot()

    assertEquals(3, stack.size)
    assertEquals(3, stack.peek())
    assertEquals(2, stack.peek(1))
    assertEquals(1, stack.peek(2))
    assertEquals(1, stack.snapshotCount)

    stack.restoreSnapshot()

    assertEquals(1, stack.size)
    assertEquals(1, stack.peek())
    assertEquals(0, stack.snapshotCount)
  }

  @Test
  fun test_discardSnapshot_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.takeSnapshot()
    stack.push(2)
    stack.takeSnapshot()

    assertEquals(2, stack.snapshotCount)

    stack.discardSnapshot()

    assertEquals(1, stack.snapshotCount)

    stack.discardSnapshot()

    assertEquals(0, stack.snapshotCount)
  }

  @Test
  fun test_clearAllSnapshots_valid() {
    val stack = LinkedListRestorableStack<Number>()

    stack.push(1)
    stack.takeSnapshot()
    stack.push(2)
    stack.takeSnapshot()

    assertEquals(2, stack.snapshotCount)

    stack.clearAllSnapshots()

    assertEquals(0, stack.snapshotCount)
  }
}
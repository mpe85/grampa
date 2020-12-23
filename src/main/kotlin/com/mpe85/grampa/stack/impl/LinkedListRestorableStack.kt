package com.mpe85.grampa.stack.impl

import com.mpe85.grampa.stack.RestorableStack
import java.util.ArrayDeque
import java.util.Collections.swap
import java.util.Deque
import java.util.LinkedList

/**
 * A linked list implementation of a restorable stack.
 *
 * @author mpe85
 * @param[E] The type of the stack elements
 */
class LinkedListRestorableStack<E> : LinkedList<E>, RestorableStack<E> {

  companion object {
    private const val serialVersionUID = 3875323652049358971L
  }

  private val snapshots = ArrayDeque<Deque<E>>()

  /**
   * Construct an empty restorable stack.
   */
  constructor() : super()

  /**
   * Construct a restorable stack with initial elements.
   *
   * @param[c] The initial elements on the stack.
   */
  constructor(c: Collection<E>) : super(c)

  override fun push(down: Int, element: E) = add(checkIndex(down), element)

  override fun pop(down: Int) = removeAt(checkIndex(down))

  override fun <T : E> popAs(type: Class<T>): T = type.cast(pop())

  override fun <T : E> popAs(down: Int, type: Class<T>): T = type.cast(pop(checkIndex(down)))

  override fun peek(down: Int) = get(checkIndex(down))

  override fun <T : E> peekAs(type: Class<T>): T = type.cast(get(0))

  override fun <T : E> peekAs(down: Int, type: Class<T>): T = type.cast(get(checkIndex(down)))

  override fun poke(element: E) = set(0, element)

  override fun poke(down: Int, element: E) = set(down, element)

  override fun dup() {
    check(size != 0) { "Duplicating the top stack value is not possible when the stack is empty." }
    push(peek())
  }

  override fun swap() {
    check(size >= 2) { "Swapping the two top stack values is not possible when the stack contains lesser than two values." }
    swap(this, 0, 1)
  }

  override fun takeSnapshot() = snapshots.push(copy())

  override fun restoreSnapshot() {
    snapshots.pop().let {
      clear()
      addAll(it)
    }
  }

  override fun discardSnapshot() {
    snapshots.pop()
  }

  override fun removeSnapshot(restore: Boolean) = if (restore) restoreSnapshot() else discardSnapshot()

  override fun clearAllSnapshots() = snapshots.clear()

  override val snapshotCount get() = snapshots.size

  override fun copy() = LinkedListRestorableStack(this)

  private fun checkIndex(down: Int) = down.also {
    require(it in 0..size) { "A 'down' index must not be out of bounds." }
  }

}
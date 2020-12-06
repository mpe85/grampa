package com.mpe85.grampa.util.stack.impl

import com.google.common.base.Preconditions
import com.mpe85.grampa.util.stack.RestorableStack
import java.util.ArrayDeque
import java.util.Collections
import java.util.Deque
import java.util.LinkedList

/**
 * A linked list implementation of a restorable stack.
 *
 * @author mpe85
 *
 * @param E the type of the stack elements
 */
class LinkedListRestorableStack<E> : LinkedList<E>, RestorableStack<E> {

  companion object {
    private const val serialVersionUID = 3875323652049358971L
  }

  private val snapshots: Deque<Deque<E>> = ArrayDeque()

  /**
   * Default c'tor.
   */
  constructor() : super()

  /**
   * C'tor.
   *
   * @param c the initial elements of the stack.
   */
  constructor(c: Collection<E>) : super(c)

  override fun push(down: Int, element: E) = add(checkIndex(down), element)

  override fun pop(down: Int): E = removeAt(checkIndex(down))

  override fun <T : E> popAs(type: Class<T>): T = type.cast(pop())

  override fun <T : E> popAs(down: Int, type: Class<T>): T = type.cast(pop(checkIndex(down)))

  override fun peek(down: Int): E = get(checkIndex(down))

  override fun <T : E> peekAs(type: Class<T>): T = type.cast(get(0))

  override fun <T : E> peekAs(down: Int, type: Class<T>): T = type.cast(get(checkIndex(down)))

  override fun poke(element: E): E = set(0, element)

  override fun poke(down: Int, element: E): E = set(down, element)

  override fun dup() {
    check(size != 0) { "Duplicating the top stack value is not possible when the stack is empty." }
    push(peek())
  }

  override fun swap() {
    check(size >= 2) { "Swapping the two top stack values is not possible when the stack contains lesser than two values." }
    Collections.swap(this, 0, 1)
  }

  override fun takeSnapshot() = snapshots.push(copy())

  override fun restoreSnapshot() {
    val snapshot = snapshots.pop()
    clear()
    addAll(snapshot)
  }

  override fun discardSnapshot() {
    snapshots.pop()
  }

  override fun removeSnapshot(restore: Boolean) {
    if (restore) {
      restoreSnapshot()
    } else {
      discardSnapshot()
    }
  }

  override fun clearAllSnapshots() = snapshots.clear()

  override val snapshotCount: Int
    get() = snapshots.size

  override fun copy(): RestorableStack<E> = LinkedListRestorableStack(this)

  private fun checkIndex(down: Int) = Preconditions.checkPositionIndex(down, size, "A 'down' index must be in range.")

}
package com.mpe85.grampa.stack.impl

import com.mpe85.grampa.stack.RestorableStack
import java.util.ArrayDeque
import java.util.Deque

/**
 * A linked list implementation of a restorable stack.
 *
 * @author mpe85
 * @param[E] The type of the stack elements
 */
class LinkedListRestorableStack<E> : LinkedListStack<E>, RestorableStack<E> {

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

  override val snapshotCount get() = snapshots.size

  private val snapshots = ArrayDeque<Deque<E>>()

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

  override fun clearSnapshots() = snapshots.clear()

  override fun reset() {
    clearSnapshots()
    clear()
  }

  override fun copy() = LinkedListRestorableStack(this)

  companion object {
    private const val serialVersionUID = 3875323652049358971L
  }

}
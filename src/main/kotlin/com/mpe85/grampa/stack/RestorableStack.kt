package com.mpe85.grampa.stack

import java.util.Deque

/**
 * A stack data structure that allows taking and restoring snapshots.
 *
 * @author mpe85
 *
 * @param[E] The type of the stack elements
 */
interface RestorableStack<E> : Deque<E> {
  /**
   * Insert an element onto the stack at a given position.
   *
   * @param[down] The position where the element is inserted, counted downwards beginning at the top of the stack
   *              (i.e. the number of elements on the stack to skip).
   * @param[element] An element
   */
  fun push(down: Int, element: E)

  /**
   * Retrieve and remove an element from the stack at a given position.
   *
   * @param[down] The position where the element is popped, counted downwards beginning at the top of the stack
   *              (i.e. the number of elements on the stack to skip).
   * @return The popped element
   */
  fun pop(down: Int): E

  /**
   * Retrieve and remove an element from the stack, and cast it to the specified subtype.
   *
   * @param[T] A subtype of [E]
   * @param[type] The type to cast the element to
   * @return The popped element
   */
  fun <T : E> popAs(type: Class<T>): T

  /**
   * Retrieve and remove an element from the stack at a given position, and cast it to the specified subtype.
   *
   * @param[T] A subtype of [E]
   * @param[down] The position where the element is popped, counted downwards beginning at the top of the stack
   *              (i.e. the number of elements on the stack to skip).
   * @param[type] The type to cast the element to
   * @return The popped element
   */
  fun <T : E> popAs(down: Int, type: Class<T>): T

  /**
   * Retrieve an element from the stack at a given position without removing it.
   *
   * @param[down] The position where the element is peeked, counted downwards beginning at the top of the stack
   *              (i.e. the number of elements on the stack to skip).
   * @return The peeked element
   */
  fun peek(down: Int): E

  /**
   * Retrieve an element from the stack without removing it, and cast it to the specified subtype.
   *
   * @param[T] A subtype of [E]
   * @param[type] The type to cast the element to
   * @return The peeked element
   */
  fun <T : E> peekAs(type: Class<T>): T

  /**
   * Retrieve an element from the stack at a given position without removing it, and cast it to the specified subtype.
   *
   * @param[T] A subtype of [E]
   * @param[down] The position where the element is peeked, counted downwards beginning at the top of the stack
   *              (i.e. the number of elements on the stack to skip).
   * @param[type] The type to cast the element to
   * @return The peeked element
   */
  fun <T : E> peekAs(down: Int, type: Class<T>): T

  /**
   * Replace the element on top of the stack.
   *
   * @param[element] A replacement element
   * @return The replaced element
   */
  fun poke(element: E): E

  /**
   * Replace an element in the stack at a given position.
   *
   * @param[down] The position where the element is poked, counted downwards beginning at the top of the stack
   *              (i.e. the number of elements on the stack to skip).
   * @param[element] A replacement element
   * @return The replaced element
   */
  fun poke(down: Int, element: E): E

  /**
   * Duplicate the element on top of the stack, i.e. the top stack element gets pushed again.
   */
  fun dup()

  /**
   * Swap the two top stack elements on the stack.
   */
  fun swap()

  /**
   * Take a snapshot of the current stack state. May be called multiple times; all taken snapshots are stacked.
   */
  fun takeSnapshot()

  /**
   * Restore the stack state to the last taken snapshot.
   */
  fun restoreSnapshot()

  /**
   * Discard the last taken snapshot.
   */
  fun discardSnapshot()

  /**
   * Remove the last taken snapshot.
   *
   * @param[restore] true if the stack should be restored to the snapshot, false if the snapshot should be discarded.
   */
  fun removeSnapshot(restore: Boolean)

  /**
   * Remove all taken snapshots.
   */
  fun clearAllSnapshots()

  /**
   * Get the current number of snapshots.
   *
   * @return The number of snapshots
   */
  val snapshotCount: Int

  /**
   * Copy the stack. Only the references to the elements are copied, not the elements themselves.
   *
   * @return The stack copy
   */
  fun copy(): RestorableStack<E>

}

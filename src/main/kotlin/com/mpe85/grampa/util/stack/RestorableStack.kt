package com.mpe85.grampa.util.stack

import java.util.Deque

/**
 * A stack data structure that allows taking and restoring snapshots.
 *
 * @author mpe85
 *
 * @param E the type of the stack elements
 */
interface RestorableStack<E> : Deque<E> {
  /**
   * Inserts an element onto the stack at a given position.
   *
   * @param down the position where the element is inserted, counted downwards beginning at the top of the stack
   *             (i.e. the number of elements on the stack to skip).
   * @param element an element
   */
  fun push(down: Int, element: E)

  /**
   * Retries and removes an element from the stack at a given position.
   *
   * @param down the position where the element is popped, counted downwards beginning at the top of the stack
   *             (i.e. the number of elements on the stack to skip).
   * @return the popped element
   */
  fun pop(down: Int): E

  /**
   * Retries and removes an element from the stack and casts it to the specified sub type.
   *
   * @param T a sub type of E
   * @param type the type to cast the element to
   * @return the popped element
   */
  fun <T : E> popAs(type: Class<T>): T

  /**
   * Retries and removes an element from the stack at a given position and casts it to the specified sub type.
   *
   * @param T a sub type of E
   * @param down the position where the element is popped, counted downwards beginning at the top of the stack
   *             (i.e. the number of elements on the stack to skip).
   * @param type the type to cast the element to
   * @return the popped element
   */
  fun <T : E> popAs(down: Int, type: Class<T>): T

  /**
   * Retrieves, but does not remove an element from the stack at a given position.
   *
   * @param down the position where the element is peeked, counted downwards beginning at the top of the stack
   *             (i.e. the number of elements on the stack to skip).
   * @return the peeked element
   */
  fun peek(down: Int): E

  /**
   * Retrieves, but does not remove an element from the stack and casts it to the specified sub type.
   *
   * @param T a sub type of E
   * @param type the type to cast the element to
   * @return the peeked element
   */
  fun <T : E> peekAs(type: Class<T>): T

  /**
   * Retrieves, but does not remove an element from the stack at a given position and casts it to the specified sub
   * type.
   *
   * @param T a sub type of E
   * @param down the position where the element is peeked, counted downwards beginning at the top of the stack
   *             (i.e. the number of elements on the stack to skip).
   * @param type the type to cast the element to
   * @return the peeked element
   */
  fun <T : E> peekAs(down: Int, type: Class<T>): T

  /**
   * Replaces the element on top of the stack.
   *
   * @param element a replacement element
   * @return the replaced element
   */
  fun poke(element: E): E

  /**
   * Replaces an element in the stack at a given position.
   *
   * @param down the position where the element is poked, counted downwards beginning at the top of the stack
   *             (i.e. the number of elements on the stack to skip).
   * @param element a replacement element
   * @return the replaced element
   */
  fun poke(down: Int, element: E): E

  /**
   * Duplicates the element on top of the stack, i.e. the top stack element gets pushed again.
   */
  fun dup()

  /**
   * Swaps the two top stack elements on the stack.
   */
  fun swap()

  /**
   * Takes a snapshot of the current stack state. May be called multiple times.
   */
  fun takeSnapshot()

  /**
   * Restores the stack state to the last taken snapshot.
   */
  fun restoreSnapshot()

  /**
   * Discards the last taken snapshot.
   */
  fun discardSnapshot()

  /**
   * Removes the last taken snapshot.
   *
   * @param restore true if the stack should be restored to the snapshot, false if the snapshot should be discarded.
   */
  fun removeSnapshot(restore: Boolean)

  /**
   * Removes all taken snapshots.
   */
  fun clearAllSnapshots()

  /**
   * Gets the current number of snapshots.
   *
   * @return the number of snapshots
   */
  val snapshotCount: Int

  /**
   * Copies the stack. Only the references to the elements are copied, not the elements themselves.
   *
   * @return the stack copy
   */
  fun copy(): RestorableStack<E>
}
package com.mpe85.grampa.stack

import java.util.*

/**
 * A stack data structure based on [Deque].
 *
 * @author mpe85
 *
 * @param[E] The type of the stack elements
 */
interface Stack<E> : Deque<E> {

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
     * Retrieve an element from the stack at a given position without removing it.
     *
     * @param[down] The position where the element is peeked, counted downwards beginning at the top of the stack
     *              (i.e. the number of elements on the stack to skip).
     * @return The peeked element
     */
    fun peek(down: Int): E

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
     * Copy the stack. Only the references to the elements are copied, not the elements themselves.
     *
     * @return The stack copy
     */
    fun copy(): Stack<E>

}

package com.github.mpe85.grampa.stack

import java.util.Deque

/**
 * A stack data structure based on [Deque].
 *
 * @param[E] The type of the stack elements
 * @author mpe85
 */
public interface Stack<E> : Deque<E> {

    /**
     * Insert an element onto the stack at a given position.
     *
     * @param[down] The position where the element is inserted, counted downwards beginning at the
     *   top of the stack (i.e. the number of elements on the stack to skip).
     * @param[element] An element
     */
    public fun push(down: Int, element: E)

    /**
     * Retrieve and remove an element from the stack at a given position.
     *
     * @param[down] The position where the element is popped, counted downwards beginning at the top
     *   of the stack (i.e. the number of elements on the stack to skip).
     * @return The popped element
     */
    public fun pop(down: Int): E

    /**
     * Retrieve an element from the stack at a given position without removing it.
     *
     * @param[down] The position where the element is peeked, counted downwards beginning at the top
     *   of the stack (i.e. the number of elements on the stack to skip).
     * @return The peeked element
     */
    public fun peek(down: Int): E

    /**
     * Replace the element on top of the stack.
     *
     * @param[element] A replacement element
     * @return The replaced element
     */
    public fun poke(element: E): E

    /**
     * Replace an element in the stack at a given position.
     *
     * @param[down] The position where the element is poked, counted downwards beginning at the top
     *   of the stack (i.e. the number of elements on the stack to skip).
     * @param[element] A replacement element
     * @return The replaced element
     */
    public fun poke(down: Int, element: E): E

    /** Duplicate the element on top of the stack, i.e. the top stack element gets pushed again. */
    public fun dup()

    /** Swap the two top stack elements on the stack. */
    public fun swap()

    /**
     * Copy the stack. Only the references to the elements are copied, not the elements themselves.
     *
     * @return The stack copy
     */
    public fun copy(): Stack<E>
}

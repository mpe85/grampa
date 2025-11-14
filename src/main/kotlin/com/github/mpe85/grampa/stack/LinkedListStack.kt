package com.github.mpe85.grampa.stack

import java.util.Collections.swap
import java.util.LinkedList

/**
 * A linked list implementation of a stack.
 *
 * @param[E] The type of the stack elements
 * @author mpe85
 */
internal open class LinkedListStack<E> : LinkedList<E>, Stack<E> {

    /** Construct an empty stack. */
    constructor() : super()

    /**
     * Construct a stack with initial elements.
     *
     * @param[c] The initial elements on the stack.
     */
    constructor(c: Collection<E>) : super(c)

    override fun push(down: Int, element: E): Unit = add(down, element)

    override fun pop(down: Int): E = removeAt(down)

    override fun peek(): E = first()

    override fun peek(down: Int): E = get(down)

    override fun poke(element: E): E = set(0, element)

    override fun poke(down: Int, element: E): E = set(down, element)

    override fun dup(): Unit = push(peek())

    override fun swap(): Unit = swap(this, 0, 1)

    override fun copy(): LinkedListStack<E> = LinkedListStack(this)
}

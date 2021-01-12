package com.mpe85.grampa.stack.impl

import com.mpe85.grampa.stack.Stack
import java.util.*
import java.util.Collections.swap

/**
 * A linked list implementation of a restorable stack.
 *
 * @author mpe85
 * @param[E] The type of the stack elements
 */
open class LinkedListStack<E> : LinkedList<E>, Stack<E> {

    /**
     * Construct an empty stack.
     */
    constructor() : super()

    /**
     * Construct a stack with initial elements.
     *
     * @param[c] The initial elements on the stack.
     */
    constructor(c: Collection<E>) : super(c)

    override fun push(down: Int, element: E) = add(down, element)

    override fun pop(down: Int) = removeAt(down)

    override fun peek(): E = first

    override fun peek(down: Int) = get(down)

    override fun poke(element: E) = set(0, element)

    override fun poke(down: Int, element: E) = set(down, element)

    override fun dup() = push(first)

    override fun swap() = swap(this, 0, 1)

    override fun copy() = LinkedListStack(this)

    companion object {
        private const val serialVersionUID = -8417124164036326875L
    }

}

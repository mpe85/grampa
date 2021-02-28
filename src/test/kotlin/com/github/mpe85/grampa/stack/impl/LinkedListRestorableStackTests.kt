package com.github.mpe85.grampa.stack.impl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class LinkedListRestorableStackTests : StringSpec({
    "Push" {
        val stack1 = LinkedListRestorableStack<Number>().apply {
            push(1)
            push(2)
            push(3)
        }
        val stack2 = LinkedListRestorableStack<Number>().apply {
            push(0, 3)
            push(1, 2)
            push(2, 1)
        }
        stack1.size shouldBe 3
        stack2.size shouldBe 3
        stack1 shouldBe stack2
    }
    "Peek" {
        LinkedListRestorableStack<Number>().apply {
            push(1)
            push(2)
            push(3)
            size shouldBe 3
            peek() shouldBe 3
            peek(0) shouldBe 3
            peek(1) shouldBe 2
            peek(2) shouldBe 1
            size shouldBe 3
            shouldThrow<IndexOutOfBoundsException> { peek(3) }
        }
    }
    "Pop" {
        LinkedListRestorableStack<Number>().apply {
            push(1)
            push(2)
            push(3)
            size shouldBe 3
            pop() shouldBe 3
            size shouldBe 2
            pop(1) shouldBe 1
            size shouldBe 1
            pop(0) shouldBe 2
            size shouldBe 0
            shouldThrow<NoSuchElementException> { pop() }
        }
    }
    "Poke" {
        LinkedListRestorableStack<Number>().apply {
            push(1)
            push(2)
            push(3)
            size shouldBe 3
            poke(4) shouldBe 3
            peek() shouldBe 4
            size shouldBe 3
            poke(1, 5) shouldBe 2
            peek(1) shouldBe 5
            size shouldBe 3
        }
    }
    "Dup" {
        LinkedListRestorableStack<Number>().apply {
            shouldThrow<NoSuchElementException> { dup() }
            push(1)
            dup()
            dup()
            size shouldBe 3
            peek() shouldBe 1
            peek(1) shouldBe 1
            peek(2) shouldBe 1
        }
    }
    "Swap" {
        LinkedListRestorableStack<Number>().apply {
            shouldThrow<IndexOutOfBoundsException> { swap() }
            push(1)
            shouldThrow<IndexOutOfBoundsException> { swap() }
            push(2)
            push(3)
            swap()
            size shouldBe 3
            peek() shouldBe 2
            peek(1) shouldBe 3
            peek(2) shouldBe 1
        }
    }
    "Restore snapshot" {
        LinkedListRestorableStack<Number>().apply {
            shouldThrow<NoSuchElementException> { restoreSnapshot() }
            push(1)
            takeSnapshot()
            push(2)
            push(3)
            takeSnapshot()
            pop(1)
            size shouldBe 2
            snapshotCount shouldBe 2
            restoreSnapshot()
            size shouldBe 3
            peek() shouldBe 3
            peek(1) shouldBe 2
            peek(2) shouldBe 1
            snapshotCount shouldBe 1
            restoreSnapshot()
            size shouldBe 1
            peek() shouldBe 1
            snapshotCount shouldBe 0
            shouldThrow<NoSuchElementException> { restoreSnapshot() }
        }
    }
    "Discard snapshot" {
        LinkedListRestorableStack<Number>().apply {
            push(1)
            takeSnapshot()
            push(2)
            takeSnapshot()
            snapshotCount shouldBe 2
            discardSnapshot()
            snapshotCount shouldBe 1
            discardSnapshot()
            snapshotCount shouldBe 0
        }
    }
    "Clear snapshots" {
        LinkedListRestorableStack<Number>().apply {
            push(1)
            takeSnapshot()
            push(2)
            takeSnapshot()
            snapshotCount shouldBe 2
            clearSnapshots()
            snapshotCount shouldBe 0
        }
    }
})

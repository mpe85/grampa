package com.mpe85.grampa.stack

/**
 * A stack data structure that allows taking and restoring snapshots.
 *
 * @author mpe85
 *
 * @param[E] The type of the stack elements
 */
interface RestorableStack<E> : Stack<E> {

    /**
     * Get the current number of snapshots.
     *
     * @return The number of snapshots
     */
    val snapshotCount: Int

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
    fun clearSnapshots()

    /**
     * Reset the stack, i.e. clear all snapshots and all elements on the stack.
     */
    fun reset()

    override fun copy(): RestorableStack<E>
}

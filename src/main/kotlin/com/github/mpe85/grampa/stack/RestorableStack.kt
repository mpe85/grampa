package com.github.mpe85.grampa.stack

/**
 * A stack data structure that allows taking and restoring snapshots.
 *
 * @param[E] The type of the stack elements
 * @author mpe85
 */
public interface RestorableStack<E> : Stack<E> {

    /**
     * Get the current number of snapshots.
     *
     * @return The number of snapshots
     */
    public val snapshotCount: Int

    /**
     * Take a snapshot of the current stack state. May be called multiple times; all taken snapshots
     * are stacked.
     */
    public fun takeSnapshot()

    /** Restore the stack state to the last taken snapshot. */
    public fun restoreSnapshot()

    /** Discard the last taken snapshot. */
    public fun discardSnapshot()

    /**
     * Remove the last taken snapshot.
     *
     * @param[restore] true if the stack should be restored to the snapshot, false if the snapshot
     *   should be discarded.
     */
    public fun removeSnapshot(restore: Boolean)

    /** Remove all taken snapshots. */
    public fun clearSnapshots()

    /** Reset the stack, i.e. clear all snapshots and all elements on the stack. */
    public fun reset()

    override fun copy(): RestorableStack<E>
}

package com.mpe85.grampa.util.stack;

import java.util.Deque;

/**
 * A stack data structure that allows taking and restoring snapshots.
 * 
 * @author mpe85
 *
 * @param <E>
 *        the type of the stack elements
 */
public interface RestorableStack<E> extends Deque<E> {
	
	/**
	 * Inserts an element onto the stack at a given position.
	 * 
	 * @param down
	 *        the position where the element is inserted, counted downwards beginning at the top of the stack (i.e. the
	 *        number of elements on the stack to skip).
	 * @param element
	 *        an element
	 */
	void push(int down, E element);
	
	/**
	 * Retries and removes an element from the stack at a given position.
	 * 
	 * @param down
	 *        the position where the element is popped, counted downwards beginning at the top of the stack (i.e. the
	 *        number of elements on the stack to skip).
	 * @return the popped element
	 */
	E pop(int down);
	
	/**
	 * Retries and removes an element from the stack and casts it to the specified sub type.
	 * 
	 * @param <T>
	 *        a sub type of E
	 * @param type
	 *        the type to cast the element to
	 * @return the popped element
	 */
	<T extends E> T popAs(Class<T> type);
	
	/**
	 * Retries and removes an element from the stack at a given position and casts it to the specified sub type.
	 * 
	 * @param <T>
	 *        a sub type of E
	 * @param down
	 *        the position where the element is popped, counted downwards beginning at the top of the stack (i.e. the
	 *        number of elements on the stack to skip).
	 * @param type
	 *        the type to cast the element to
	 * @return the popped element
	 */
	<T extends E> T popAs(int down, Class<T> type);
	
	/**
	 * Retrieves, but does not remove an element from the stack at a given position.
	 * 
	 * @param down
	 *        the position where the element is peeked, counted downwards beginning at the top of the stack (i.e. the
	 *        number of elements on the stack to skip).
	 * @return the peeked element
	 */
	E peek(int down);
	
	/**
	 * Retrieves, but does not remove an element from the stack and casts it to the specified sub type.
	 * 
	 * @param <T>
	 *        a sub type of E
	 * @param type
	 *        the type to cast the element to
	 * @return the peeked element
	 */
	<T extends E> T peekAs(Class<T> type);
	
	/**
	 * Retrieves, but does not remove an element from the stack at a given position and casts it to the specified sub
	 * type.
	 * 
	 * @param <T>
	 *        a sub type of E
	 * @param down
	 *        the position where the element is peeked, counted downwards beginning at the top of the stack (i.e. the
	 *        number of elements on the stack to skip).
	 * @param type
	 *        the type to cast the element to
	 * @return the peeked element
	 */
	<T extends E> T peekAs(int down, Class<T> type);
	
	/**
	 * Replaces the element on top of the stack.
	 * 
	 * @param element
	 *        a replacement element
	 * @return the replaced element
	 */
	E poke(E element);
	
	/**
	 * Replaces an element in the stack at a given position.
	 * 
	 * @param down
	 *        the position where the element is poked, counted downwards beginning at the top of the stack (i.e. the
	 *        number of elements on the stack to skip).
	 * @param element
	 *        a replacement element
	 * @return the replaced element
	 */
	E poke(int down, E element);
	
	/**
	 * Duplicates the element on top of the stack, i.e. the top stack element gets pushed again.
	 * 
	 */
	void dup();
	
	/**
	 * Swaps the two top stack elements on the stack.
	 */
	void swap();
	
	/**
	 * Takes a snapshot of the current stack state. May be called multiple times.
	 */
	void takeSnapshot();
	
	/**
	 * Restores the stack state to the last taken snapshot.
	 */
	void restoreSnapshot();
	
	/**
	 * Discards the last taken snapshot.
	 */
	void discardSnapshot();
	
	/**
	 * Removes the last taken snapshot.
	 * 
	 * @param restore
	 *        true if the stack should be restored to the snapshot, false if the snapshot should be discarded.
	 */
	void removeSnapshot(boolean restore);
	
	/**
	 * Removes all taken snapshots.
	 */
	void clearAllSnapshots();
	
	/**
	 * Gets the current number of snapshots.
	 * 
	 * @return the number of snapshots
	 */
	int getSnapshotCount();
	
	/**
	 * Copies the stack. Only the references to the elements are copied, not the elements themselves.
	 * 
	 * @return the stack copy
	 */
	RestorableStack<E> copy();
	
}

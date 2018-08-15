package com.mpe85.grampa.util.stack;

import java.util.Deque;

public interface RestorableStack<E> extends Deque<E>, Cloneable {
	
	void push(int down, E element);
	
	E pop(int down);
	
	<T extends E> T popAs(Class<T> type);
	
	<T extends E> T popAs(int down, Class<T> type);
	
	E peek(int down);
	
	<T extends E> T peekAs(Class<T> type);
	
	<T extends E> T peekAs(int down, Class<T> type);
	
	E poke(E element);
	
	E poke(int down, E element);
	
	void dup();
	
	void swap();
	
	void takeSnapshot();
	
	void restoreSnapshot();
	
	void discardSnapshot();
	
	void removeSnapshot(boolean restore);
	
	void clearAllSnapshots();
	
	int getSnapshotCount();
	
	RestorableStack<E> copy();
	
}

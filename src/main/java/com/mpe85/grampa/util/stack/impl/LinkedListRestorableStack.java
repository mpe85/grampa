package com.mpe85.grampa.util.stack.impl;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.util.stack.RestorableStack;

public class LinkedListRestorableStack<E> extends LinkedList<E> implements RestorableStack<E> {
	
	@Override
	public void push(final int down, final E element) {
		add(checkIndex(down), element);
	}
	
	@Override
	public E pop(final int down) {
		return remove(checkIndex(down));
	}
	
	@Override
	public <T extends E> T popAs(final Class<T> type) {
		return type.cast(pop());
	}
	
	@Override
	public <T extends E> T popAs(final int down, final Class<T> type) {
		return type.cast(pop(checkIndex(down)));
	}
	
	@Override
	public E peek(final int down) {
		return get(checkIndex(down));
	}
	
	@Override
	public <T extends E> T peekAs(final Class<T> type) {
		return type.cast(get(0));
	}
	
	@Override
	public <T extends E> T peekAs(final int down, final Class<T> type) {
		return type.cast(get(checkIndex(down)));
	}
	
	@Override
	public E poke(final E element) {
		return set(0, element);
	}
	
	@Override
	public E poke(final int down, final E element) {
		return set(down, element);
	}
	
	@Override
	public void dup() {
		push(peek());
	}
	
	@Override
	public void swap() {
		Collections.swap(this, 0, 1);
	}
	
	@Override
	public void takeSnapshot() {
		@SuppressWarnings("unchecked") final LinkedList<E> clone = (LinkedList<E>) clone();
		snapshots.push(clone);
	}
	
	@Override
	public void restoreSnapshot() {
		final LinkedList<E> snapshot = snapshots.pop();
		clear();
		addAll(snapshot);
	}
	
	@Override
	public void discardSnapshot() {
		snapshots.pop();
	}
	
	@Override
	public void removeSnapshot(final boolean restore) {
		if (restore) {
			restoreSnapshot();
		}
		else {
			discardSnapshot();
		}
	}
	
	@Override
	public void clearAllSnapshots() {
		snapshots.clear();
	}
	
	@Override
	public int getSnapshotCount() {
		return snapshots.size();
	}
	
	@Override
	public Object clone() {
		return super.clone();
	}
	
	private int checkIndex(final int down) {
		return Preconditions.checkPositionIndex(down, size(), "A 'down' index must be in range.");
	}
	
	
	private static final long serialVersionUID = 3875323652049358971L;
	
	private final Deque<LinkedList<E>> snapshots = new ArrayDeque<>();
	
}

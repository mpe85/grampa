package com.mpe85.grampa.util.stack.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

import com.mpe85.grampa.util.stack.RestorableStack;

public class LinkedListRestorableStack<E> extends LinkedList<E> implements RestorableStack<E> {
	
	public LinkedListRestorableStack() {
	}
	
	public LinkedListRestorableStack(final Collection<? extends E> c) {
		super(c);
	}
	
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
		checkNotNull(type, "A 'type' must not be null.");
		return type.cast(pop());
	}
	
	@Override
	public <T extends E> T popAs(final int down, final Class<T> type) {
		checkNotNull(type, "A 'type' must not be null.");
		return type.cast(pop(checkIndex(down)));
	}
	
	@Override
	public E peek(final int down) {
		return get(checkIndex(down));
	}
	
	@Override
	public <T extends E> T peekAs(final Class<T> type) {
		checkNotNull(type, "A 'type' must not be null.");
		return type.cast(get(0));
	}
	
	@Override
	public <T extends E> T peekAs(final int down, final Class<T> type) {
		checkNotNull(type, "A 'type' must not be null.");
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
		snapshots.push(copy());
	}
	
	@Override
	public void restoreSnapshot() {
		final Deque<E> snapshot = snapshots.pop();
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
	public RestorableStack<E> copy() {
		return new LinkedListRestorableStack<>(this);
	}
	
	private int checkIndex(final int down) {
		return checkPositionIndex(down, size(), "A 'down' index must be in range.");
	}
	
	
	private static final long serialVersionUID = 3875323652049358971L;
	
	private final Deque<Deque<E>> snapshots = new ArrayDeque<>();
	
}

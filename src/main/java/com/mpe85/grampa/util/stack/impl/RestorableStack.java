package com.mpe85.grampa.util.stack.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.util.stack.IRestorableStack;

public class RestorableStack<E> extends LinkedList<E> implements IRestorableStack<E> {
	
	
	@Override
	public void push(final int down, final E element) {
		add(size() - checkIndex(down), element);
	}
	
	@Override
	public E pop(final int down) {
		return remove(size() - 1 - checkIndex(down));
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
		return get(size() - 1 - checkIndex(down));
	}
	
	@Override
	public <T extends E> T peekAs(final Class<T> type) {
		return type.cast(get(size() - 1));
	}
	
	@Override
	public <T extends E> T peekAs(final int down, final Class<T> type) {
		return type.cast(get(size() - 1 - checkIndex(down)));
	}
	
	@Override
	public E poke(final E element) {
		return set(size() - 1, element);
	}
	
	@Override
	public E poke(final int down, final E element) {
		return set(size() - 1 - down, element);
	}
	
	@Override
	public void dup() {
		push(peek());
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
	public void clearAllSnapshots() {
		snapshots.clear();
	}
	
	private int checkIndex(final int down) {
		return Preconditions.checkElementIndex(down, size(), "A 'down' index must be in range.");
	}
	
	
	private static final long serialVersionUID = 3875323652049358971L;
	
	private final Deque<LinkedList<E>> snapshots = new ArrayDeque<>();
	
}

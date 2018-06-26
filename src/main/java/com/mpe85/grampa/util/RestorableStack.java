package com.mpe85.grampa.util;

import java.util.Stack;

import com.google.common.base.Preconditions;

public class RestorableStack<E> extends Stack<E> {
	
	public void push(final int down, final E element) {
		add(size() - checkDown(down), element);
	}
	
	public E pop(final int down) {
		return remove(size() - 1 - checkDown(down));
	}
	
	public <T extends E> T popAs(final Class<T> type) {
		return type.cast(pop());
	}
	
	public <T extends E> T popAs(final int down, final Class<T> type) {
		return type.cast(pop(checkDown(down)));
	}
	
	public E peek(final int down) {
		return get(size() - 1 - checkDown(down));
	}
	
	public <T extends E> T peekAs(final Class<T> type) {
		return type.cast(get(size() - 1));
	}
	
	public <T extends E> T peekAs(final int down, final Class<T> type) {
		return type.cast(get(size() - 1 - checkDown(down)));
	}
	
	private int checkDown(final int down) {
		return Preconditions.checkElementIndex(down, size(), "A 'down' index must be in range.");
	}
	
	
	private static final long serialVersionUID = -6369975557468922146L;
	
	
}

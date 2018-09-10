package com.mpe85.grampa.util.stack.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.util.stack.RestorableStack;

public class RestorableStackTest {
	
	@Test
	public void test_push_valid() {
		final RestorableStack<Number> stack1 = new LinkedListRestorableStack<>();
		final RestorableStack<Number> stack2 = new LinkedListRestorableStack<>();
		
		stack1.push(1);
		stack1.push(2);
		stack1.push(3);
		
		stack2.push(0, 3);
		stack2.push(1, 2);
		stack2.push(2, 1);
		
		assertEquals(3, stack1.size());
		assertEquals(3, stack2.size());
		assertEquals(stack1, stack2);
	}
	
	@Test
	public void test_peek_valid() {
		final RestorableStack<Number> stack = new LinkedListRestorableStack<>();
		
		stack.push(1);
		stack.push(2);
		stack.push(3);
		
		assertEquals(3, stack.size());
		assertEquals(3, stack.peek());
		assertEquals(3, stack.peek(0));
		assertEquals(2, stack.peek(1));
		assertEquals(1, stack.peek(2));
		assertEquals(3, stack.size());
	}
	
	@Test
	public void test_peekAs_valid() {
		final RestorableStack<Number> stack = new LinkedListRestorableStack<>();
		
		stack.push(1);
		stack.push(2);
		stack.push(3.3f);
		
		assertEquals(3, stack.size());
		assertEquals(3.3f, stack.peekAs(Float.class).floatValue());
		assertEquals(1, stack.peekAs(2, Integer.class).intValue());
		assertEquals(3, stack.size());
	}
	
	@Test
	public void test_pop_valid() {
		final RestorableStack<Number> stack = new LinkedListRestorableStack<>();
		
		stack.push(1);
		stack.push(2);
		stack.push(3);
		
		assertEquals(3, stack.size());
		assertEquals(3, stack.pop());
		assertEquals(2, stack.size());
		assertEquals(1, stack.pop(1));
		assertEquals(1, stack.size());
		assertEquals(2, stack.pop(0));
		assertEquals(0, stack.size());
	}
	
	@Test
	public void test_popAS_valid() {
		final RestorableStack<Number> stack = new LinkedListRestorableStack<>();
		
		stack.push(1);
		stack.push(2.2d);
		stack.push(3);
		
		assertEquals(3, stack.size());
		assertEquals(2.2d, stack.popAs(1, Double.class).doubleValue());
		assertEquals(2, stack.size());
		assertEquals(3, stack.popAs(Integer.class).intValue());
	}
	
	@Test
	public void test_poke_valid() {
		final RestorableStack<Number> stack = new LinkedListRestorableStack<>();
		
		stack.push(1);
		stack.push(2);
		stack.push(3);
		
		assertEquals(3, stack.size());
		assertEquals(3, stack.poke(4));
		assertEquals(4, stack.peek());
		assertEquals(3, stack.size());
		assertEquals(2, stack.poke(1, 5));
		assertEquals(5, stack.peek(1));
		assertEquals(3, stack.size());
	}
	
	@Test
	public void test_dup_valid() {
		final RestorableStack<Number> stack = new LinkedListRestorableStack<>();
		
		stack.push(1);
		stack.dup();
		stack.dup();
		
		assertEquals(3, stack.size());
		assertEquals(1, stack.peek());
		assertEquals(1, stack.peek(1));
		assertEquals(1, stack.peek(2));
	}
	
	@Test
	public void test_swap_valid() {
		final RestorableStack<Number> stack = new LinkedListRestorableStack<>();
		
		stack.push(1);
		stack.push(2);
		stack.push(3);
		stack.swap();
		
		assertEquals(3, stack.size());
		assertEquals(2, stack.peek());
		assertEquals(3, stack.peek(1));
		assertEquals(1, stack.peek(2));
	}
	
	@Test
	public void test_restoreSnapshot_valid() {
		final RestorableStack<Number> stack = new LinkedListRestorableStack<>();
		
		stack.push(1);
		stack.takeSnapshot();
		stack.push(2);
		stack.push(3);
		stack.takeSnapshot();
		
		stack.pop(1);
		
		assertEquals(2, stack.size());
		assertEquals(2, stack.getSnapshotCount());
		
		stack.restoreSnapshot();
		
		assertEquals(3, stack.size());
		assertEquals(3, stack.peek());
		assertEquals(2, stack.peek(1));
		assertEquals(1, stack.peek(2));
		assertEquals(1, stack.getSnapshotCount());
		
		stack.restoreSnapshot();
		
		assertEquals(1, stack.size());
		assertEquals(1, stack.peek());
		assertEquals(0, stack.getSnapshotCount());
	}
	
	@Test
	public void test_discardSnapshot_valid() {
		final RestorableStack<Number> stack = new LinkedListRestorableStack<>();
		
		stack.push(1);
		stack.takeSnapshot();
		stack.push(2);
		stack.takeSnapshot();
		
		assertEquals(2, stack.getSnapshotCount());
		
		stack.discardSnapshot();
		
		assertEquals(1, stack.getSnapshotCount());
		
		stack.discardSnapshot();
		
		assertEquals(0, stack.getSnapshotCount());
	}
	
	@Test
	public void test_clearAllSnapshots_valid() {
		final RestorableStack<Number> stack = new LinkedListRestorableStack<>();
		
		stack.push(1);
		stack.takeSnapshot();
		stack.push(2);
		stack.takeSnapshot();
		
		assertEquals(2, stack.getSnapshotCount());
		
		stack.clearAllSnapshots();
		
		assertEquals(0, stack.getSnapshotCount());
	}
	
}

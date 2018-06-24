package com.mpe85.grampa.input;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LineCounterTest {
	
	@Test
	public void test() {
		final LineCounter lc = new LineCounter("foo\nbar");
		InputPosition pos;
		
		assertEquals(2, lc.getLineCount());
		
		// 'f'
		pos = lc.getPosition(0);
		assertEquals(1, pos.getLine());
		assertEquals(1, pos.getColumn());
		
		// 'o'
		pos = lc.getPosition(1);
		assertEquals(1, pos.getLine());
		assertEquals(2, pos.getColumn());
		
		// 'o'
		pos = lc.getPosition(2);
		assertEquals(1, pos.getLine());
		assertEquals(3, pos.getColumn());
		
		// '\n'
		pos = lc.getPosition(3);
		assertEquals(1, pos.getLine());
		assertEquals(4, pos.getColumn());
		
		// 'b'
		pos = lc.getPosition(4);
		assertEquals(2, pos.getLine());
		assertEquals(1, pos.getColumn());
		
		// 'a'
		pos = lc.getPosition(5);
		assertEquals(2, pos.getLine());
		assertEquals(2, pos.getColumn());
		
		// 'r'
		pos = lc.getPosition(6);
		assertEquals(2, pos.getLine());
		assertEquals(3, pos.getColumn());
		
	}
	
}

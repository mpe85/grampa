package com.mpe85.grampa.input.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.input.ILineCounter;
import com.mpe85.grampa.input.InputPosition;

public class CharSequenceLineCounterTest {
	
	@Test
	public void test_getLineCount_valid() {
		final ILineCounter lc = new CharSequenceLineCounter("foo\n  \nbar\n  ");
		assertEquals(4, lc.getLineCount());
	}
	
	@Test
	public void test_ctor_valid() {
		assertDoesNotThrow(() -> new CharSequenceLineCounter("foobar"));
	}
	
	@Test
	public void test_ctor_invalid_nullInput() {
		assertThrows(NullPointerException.class, () -> new CharSequenceLineCounter(null));
	}
	
	@Test
	public void test_getPosition_valid_noLineBreakAtEnd() {
		final ILineCounter lc = new CharSequenceLineCounter("foo\nbar");
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
	
	@Test
	public void test_getPosition_valid_blankLines() {
		final ILineCounter lc = new CharSequenceLineCounter("\n\n\n");
		InputPosition pos;
		
		assertEquals(3, lc.getLineCount());
		
		// 1st line break
		pos = lc.getPosition(0);
		assertEquals(1, pos.getLine());
		assertEquals(1, pos.getColumn());
		
		// 2nd line break
		pos = lc.getPosition(1);
		assertEquals(2, pos.getLine());
		assertEquals(1, pos.getColumn());
		
		// 3rd line break
		pos = lc.getPosition(2);
		assertEquals(3, pos.getLine());
		assertEquals(1, pos.getColumn());
	}
	
	@Test
	public void test_getPosition_invalid_indexOutOfRange() {
		final ILineCounter lc = new CharSequenceLineCounter("foobar");
		
		assertEquals(1, lc.getLineCount());
		
		assertThrows(IndexOutOfBoundsException.class, () -> lc.getPosition(-4711));
		assertThrows(IndexOutOfBoundsException.class, () -> lc.getPosition(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> lc.getPosition(6));
		assertThrows(IndexOutOfBoundsException.class, () -> lc.getPosition(666));
		
		assertDoesNotThrow(() -> lc.getPosition(0));
		assertDoesNotThrow(() -> lc.getPosition(5));
	}
	
}
package com.mpe85.grampa.input.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.input.IInputBuffer;

public class StringInputBufferTest {
	
	@Test
	public void test_ctor_valid() {
		assertDoesNotThrow(() -> new StringInputBuffer("foobar"));
	}
	
	@Test
	public void test_ctor_invalid_nullCharSequence() {
		assertThrows(NullPointerException.class, () -> new StringInputBuffer(null));
	}
	
	@Test
	public void test_getChar_valid() {
		final IInputBuffer ib = new StringInputBuffer("foobar");
		
		assertEquals('f', ib.getChar(0));
		assertEquals('o', ib.getChar(1));
		assertEquals('o', ib.getChar(2));
		assertEquals('b', ib.getChar(3));
		assertEquals('a', ib.getChar(4));
		assertEquals('r', ib.getChar(5));
	}
	
	@Test
	public void test_getChar_invalid_indexOutOfRange() {
		final IInputBuffer ib = new StringInputBuffer("foobar");
		
		assertThrows(IndexOutOfBoundsException.class, () -> ib.getChar(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> ib.getChar(6));
	}
	
	@Test
	public void test_getCodePoint_valid() {
		final IInputBuffer ib = new StringInputBuffer("foobar");
		
		assertEquals('f', ib.getCodePoint(0));
		assertEquals('o', ib.getCodePoint(1));
		assertEquals('o', ib.getCodePoint(2));
		assertEquals('b', ib.getCodePoint(3));
		assertEquals('a', ib.getCodePoint(4));
		assertEquals('r', ib.getCodePoint(5));
	}
	
	@Test
	public void test_getCodePoint_invalid_indexOutOfRange() {
		final IInputBuffer ib = new StringInputBuffer("foobar");
		
		assertThrows(IndexOutOfBoundsException.class, () -> ib.getCodePoint(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> ib.getCodePoint(6));
	}
	
	@Test
	public void test_getLength_valid() {
		final IInputBuffer ib = new StringInputBuffer("foobar");
		
		assertEquals(6, ib.getLength());
	}
	
	@Test
	public void test_subSequence_valid() {
		final IInputBuffer ib = new StringInputBuffer("foobar");
		
		assertEquals("foobar", ib.subSequence(0, 6));
		assertEquals("f", ib.subSequence(0, 1));
		assertEquals("oo", ib.subSequence(1, 3));
		assertEquals("", ib.subSequence(5, 5));
	}
	
	@Test
	public void test_subSequence_invalid_indexOutOfRange() {
		final IInputBuffer ib = new StringInputBuffer("foobar");
		
		assertThrows(IndexOutOfBoundsException.class, () -> ib.subSequence(-2, 1));
		assertThrows(IndexOutOfBoundsException.class, () -> ib.subSequence(0, 7));
		assertThrows(IndexOutOfBoundsException.class, () -> ib.subSequence(5, 3));
	}
	
}

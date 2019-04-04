package com.mpe85.grampa.input.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.mpe85.grampa.input.InputBuffer;

public class InputBufferTest {
	
	@ParameterizedTest
	@MethodSource("provideInputBuffers")
	public void test_ctor_valid(final InputBuffer ib) {
		assertDoesNotThrow(() -> ib);
	}
	
	@ParameterizedTest
	@MethodSource("provideNullInputBuffers")
	public void test_ctor_invalid_nullCharSequence(final Supplier<InputBuffer> supplier) {
		assertThrows(NullPointerException.class, () -> supplier.get());
	}
	
	@ParameterizedTest
	@MethodSource("provideInputBuffers")
	public void test_getChar_valid(final InputBuffer ib) {
		assertEquals('f', ib.getChar(0));
		assertEquals('o', ib.getChar(1));
		assertEquals('o', ib.getChar(2));
		assertEquals('b', ib.getChar(3));
		assertEquals('a', ib.getChar(4));
		assertEquals('r', ib.getChar(5));
	}
	
	@ParameterizedTest
	@MethodSource("provideInputBuffers")
	public void test_getChar_invalid_indexOutOfRange(final InputBuffer ib) {
		assertThrows(IndexOutOfBoundsException.class, () -> ib.getChar(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> ib.getChar(6));
	}
	
	@ParameterizedTest
	@MethodSource("provideInputBuffers")
	public void test_getCodePoint_valid(final InputBuffer ib) {
		assertEquals('f', ib.getCodePoint(0));
		assertEquals('o', ib.getCodePoint(1));
		assertEquals('o', ib.getCodePoint(2));
		assertEquals('b', ib.getCodePoint(3));
		assertEquals('a', ib.getCodePoint(4));
		assertEquals('r', ib.getCodePoint(5));
	}
	
	@ParameterizedTest
	@MethodSource("provideInputBuffers")
	public void test_getCodePoint_invalid_indexOutOfRange(final InputBuffer ib) {
		assertThrows(IndexOutOfBoundsException.class, () -> ib.getCodePoint(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> ib.getCodePoint(6));
	}
	
	@ParameterizedTest
	@MethodSource("provideInputBuffers")
	public void test_getLength_valid(final InputBuffer ib) {
		assertEquals(6, ib.getLength());
	}
	
	@ParameterizedTest
	@MethodSource("provideInputBuffers")
	public void test_subSequence_valid(final InputBuffer ib) {
		assertEquals("foobar", ib.subSequence(0, 6));
		assertEquals("f", ib.subSequence(0, 1));
		assertEquals("oo", ib.subSequence(1, 3));
		assertEquals("", ib.subSequence(5, 5));
	}
	
	@ParameterizedTest
	@MethodSource("provideInputBuffers")
	public void test_subSequence_invalid_indexOutOfRange(final InputBuffer ib) {
		assertThrows(IndexOutOfBoundsException.class, () -> ib.subSequence(-2, 1));
		assertThrows(IndexOutOfBoundsException.class, () -> ib.subSequence(0, 7));
		assertThrows(IndexOutOfBoundsException.class, () -> ib.subSequence(5, 3));
	}
	
	private static Stream<InputBuffer> provideInputBuffers() {
		return Stream.of(
				new StringBufferInputBuffer(new StringBuffer("foobar")),
				new StringInputBuffer("foobar"));
	}
	
	private static Stream<Supplier<InputBuffer>> provideNullInputBuffers() {
		return Stream.of(
				() -> new StringBufferInputBuffer(null),
				() -> new StringInputBuffer(null));
	}
	
}

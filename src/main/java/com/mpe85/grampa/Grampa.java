package com.mpe85.grampa;

import static net.bytebuddy.matcher.ElementMatchers.returns;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.mpe85.grampa.exception.ParserCreateException;
import com.mpe85.grampa.intercept.RuleMethodInterceptor;
import com.mpe85.grampa.parser.Parser;
import com.mpe85.grampa.rule.Rule;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;

public final class Grampa {
	
	private Grampa() {
	}
	
	public static final <U extends Parser<T>, T> U createParser(final Class<U> parserClass) {
		try {
			return createParserSubClass(parserClass).newInstance();
		}
		catch (InstantiationException | IllegalAccessException ex) {
			throw new ParserCreateException("Failed to create new parser instance.", ex);
		}
	}
	
	public static final <U extends Parser<T>, T> ParserCtor<U> createParser(
			final Class<U> parserClass,
			final Class<?>... ctorParamTypes) {
		try {
			return new ParserCtor<>(createParserSubClass(parserClass).getConstructor(ctorParamTypes));
		}
		catch (NoSuchMethodException | SecurityException ex) {
			throw new ParserCreateException("Failed to find constructor matching given parameter types.", ex);
		}
	}
	
	private static final <U extends Parser<T>, T> Class<? extends U> createParserSubClass(final Class<U> parserClass) {
		return new ByteBuddy()
				.subclass(parserClass)
				.method(returns(Rule.class))
				.intercept(MethodDelegation
						.withDefaultConfiguration()
						.to(new RuleMethodInterceptor<>()))
				.make()
				.load(parserClass.getClassLoader())
				.getLoaded();
	}
	
	public static final class ParserCtor<T> {
		
		public ParserCtor(final Constructor<? extends T> ctor) {
			this.ctor = ctor;
		}
		
		public T withArgs(final Object... args) {
			try {
				return ctor.newInstance(args);
			}
			catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException ex) {
				throw new ParserCreateException("Failed to create new parser instance.", ex);
			}
		}
		
		private final Constructor<? extends T> ctor;
	}
	
}

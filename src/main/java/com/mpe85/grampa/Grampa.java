package com.mpe85.grampa;

import static net.bytebuddy.matcher.ElementMatchers.returns;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.google.common.reflect.Invokable;
import com.mpe85.grampa.exception.ParserCreateException;
import com.mpe85.grampa.intercept.RuleMethodInterceptor;
import com.mpe85.grampa.parser.Parser;
import com.mpe85.grampa.rule.Rule;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import one.util.streamex.StreamEx;

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
		validateParserClass(parserClass);
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
	
	private static final void validateParserClass(final Class<?> parserClass) {
		StreamEx.of(parserClass.getDeclaredMethods())
				.filter(m -> m.getReturnType() == Rule.class)
				.map(Invokable::from)
				.filter(i -> !i.isVarArgs() || !i.isAnnotationPresent(SafeVarargs.class))
				.forEach(Grampa::validateRuleMethod);
		if (parserClass.getSuperclass() != Object.class) {
			validateParserClass(parserClass.getSuperclass());
		}
	}
	
	private static final void validateRuleMethod(final Invokable<?, Object> invokable) {
		if (!invokable.isPublic() && !invokable.isProtected()) {
			throw new ParserCreateException("Rule methods must be public or protected.");
		}
		if (invokable.isFinal() && !isSafeVarArgsRuleMethod(invokable)) {
			throw new ParserCreateException("Rule methods must not be final.");
		}
		if (invokable.isStatic()) {
			throw new ParserCreateException("Rule methods must not be static.");
		}
	}
	
	private static final boolean isSafeVarArgsRuleMethod(final Invokable<?, Object> invokable) {
		return invokable.isVarArgs() && invokable.isAnnotationPresent(SafeVarargs.class);
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

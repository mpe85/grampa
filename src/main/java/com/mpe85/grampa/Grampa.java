package com.mpe85.grampa;

import static net.bytebuddy.implementation.MethodDelegation.withDefaultConfiguration;
import static net.bytebuddy.matcher.ElementMatchers.returns;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import com.google.common.reflect.Invokable;
import com.mpe85.grampa.exception.ParserCreateException;
import com.mpe85.grampa.intercept.RuleMethodInterceptor;
import com.mpe85.grampa.parser.Parser;
import com.mpe85.grampa.rule.Rule;

import net.bytebuddy.ByteBuddy;

/**
 * Main class containing static methods for parser creation
 * 
 * @author mpe85
 */
public final class Grampa {
	
	/**
	 * A parser c'tor (intermediate class for fluent API). This class wraps a {@link Constructor} and offers a var args
	 * method for calling that c'tor.
	 * 
	 * @author mpe85
	 *
	 * @param <T>
	 *            The type to which the c'tor belongs to
	 */
	public static final class ParserCtor<T> {
		
		private final Constructor<? extends T> ctor;
		
		/**
		 * Private C'tor. This class shall never be instantiated from outside
		 * 
		 * @param ctor
		 *            the wrapped {@link Constructor}
		 */
		private ParserCtor(final Constructor<? extends T> ctor) {
			this.ctor = ctor;
		}
		
		/**
		 * Call the wrapped c'tor using the given c'tor args. The arguments must match the c'tor parameter list.
		 * 
		 * @param args
		 *            c'tor args
		 * @return a new instance
		 */
		public T withArgs(final Object... args) {
			try {
				return ctor.newInstance(args);
			}
			catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException ex) {
				throw new ParserCreateException("Failed to create new parser instance.", ex);
			}
		}
		
	}
	
	/**
	 * Private C'tor. This class shall never be instantiated.
	 */
	private Grampa() {
	}
	
	/**
	 * Creates a new parser instance using the given parser class. The parser class must have a no-args c'tor which will
	 * be called by this method.
	 * 
	 * @param <U>
	 *            the type of the parser
	 * @param <T>
	 *            the type of the stack elements
	 * @param parserClass
	 *            a parser class
	 * @return a parser instance
	 */
	public static final <U extends Parser<T>, T> U createParser(final Class<U> parserClass) {
		try {
			return createParserSubClass(parserClass).getDeclaredConstructor().newInstance();
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException ex) {
			throw new ParserCreateException("Failed to create new parser instance.", ex);
		}
	}
	
	/**
	 * Creates a new parser instance using the given parser class and c'tor parameter types. The parser class must have
	 * a c'tor which matches the passed parameter types. The creation of the parser instance must be finalized by
	 * calling {@link ParserCtor#withArgs} on the returned {@link ParserCtor}.
	 * 
	 * @param <U>
	 *            the type of the parser
	 * @param <T>
	 *            the type of the stack elements
	 * @param parserClass
	 *            a parser class
	 * @param ctorParamTypes
	 *            the parameter types of the c'tor
	 * @return a parser c'tor
	 */
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
				.intercept(withDefaultConfiguration().to(new RuleMethodInterceptor<>()))
				.make()
				.load(parserClass.getClassLoader())
				.getLoaded();
	}
	
	private static final void validateParserClass(final Class<?> parserClass) {
		Stream.of(parserClass.getDeclaredMethods())
				.filter(m -> m.getReturnType() == Rule.class)
				.map(Invokable::from)
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
	
}

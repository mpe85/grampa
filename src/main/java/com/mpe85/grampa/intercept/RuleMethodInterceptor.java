package com.mpe85.grampa.intercept;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;
import com.mpe85.grampa.rule.impl.AbstractRule;
import com.mpe85.grampa.visitor.impl.ReferenceRuleReplaceVisitor;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * An interceptor for the rule methods of a parser. If a rule method gets called for the second time, the actual rule is
 * replaced by a reference rule in the first place. At the very ending of the parser creating process (i.e. the first
 * call of the root rule method) all reference rules are replaced by the 'real' rules. This is done to avoid endless
 * recursions caused by circular rule dependencies.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class RuleMethodInterceptor<T> {
	
	/**
	 * Intercepts rule methods.
	 * 
	 * @param method
	 *            a rule method
	 * @param zuper
	 *            the method body wrapped inside a callable
	 * @param args
	 *            the arguments with which the method was called
	 * @return a parser rule
	 * @throws Exception
	 *             may be thrown when the actual method is called via the callable
	 */
	@RuntimeType
	public Rule<T> intercept(
			@Origin final Method method,
			@SuperCall final Callable<Rule<T>> zuper,
			@AllArguments final Object... args)
			throws Exception {
		final int hash = Objects.hash(method.getName(), Objects.hash(args));
		
		if (!rules.containsKey(hash)) {
			rules.put(hash, null);
			final Rule<T> rule = zuper.call();
			rules.put(hash, rule);
			if (isRoot(method)) {
				rule.accept(new ReferenceRuleReplaceVisitor<>(rules));
			}
			return rule;
		}
		return new ReferenceRule<>(hash);
	}
	
	/**
	 * Checks if a rule method is the root rule method.
	 * 
	 * @param method
	 *            the method to check
	 * @return true if it is the root rule method, false otherwise
	 */
	private boolean isRoot(final Method method) {
		return Objects.equals(ROOT, method.getName()) && method.getParameterCount() == 0;
	}
	
	
	private static final String ROOT = "root";
	
	private final Map<Integer, Rule<T>> rules = new HashMap<>();
	
	/**
	 * A reference rule implementation that stores a reference to a another rule. Note that this is only used by
	 * {@link RuleMethodInterceptor}.
	 * 
	 * @author mpe85
	 *
	 * @param <T>
	 *            the type of the stack elements
	 */
	public static class ReferenceRule<T> extends AbstractRule<T> {
		
		/**
		 * Private C'tor. This class must never be instantiated from outside of {@link RuleMethodInterceptor}.
		 * 
		 * @param hashCode
		 *            the hash code of the referenced rule
		 */
		private ReferenceRule(final int hashCode) {
			this.hashCode = hashCode;
		}
		
		@Override
		public boolean match(final RuleContext<T> context) {
			return false;
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (obj != null && getClass() == obj.getClass()) {
				final ReferenceRule<?> other = (ReferenceRule<?>) obj;
				return Objects.equals(hashCode, other.hashCode);
			}
			return false;
		}
		
		@Override
		protected ToStringHelper toStringHelper() {
			return super.toStringHelper()
					.add("hashCode", hashCode);
		}
		
		
		private final int hashCode;
		
	}
	
}

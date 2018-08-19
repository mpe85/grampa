package com.mpe85.grampa.intercept;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.impl.ReferenceRule;
import com.mpe85.grampa.visitor.impl.ReferenceRuleReplaceVisitor;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * An interceptor for the rule methods of a parser. If a rule method gets called for the second time, the actual rule is
 * replaced by a reference rule, which is replaced by the real rule at the very ending of the call hierarchy (i.e. the
 * first call of the root rule method). This is done to avoid endless recursions caused by circular rule dependencies.
 * 
 * @author mpe85
 *
 * @param <T>
 *        the type of the stack elements
 */
public class RuleMethodInterceptor<T> {
	
	/**
	 * Intercepts rule methods.
	 * 
	 * @param method
	 *        a rule method
	 * @param zuper
	 *        the method body wrapped inside a callable
	 * @param args
	 *        the arguments with which the method was called
	 * @return a parser rule
	 * @throws Exception
	 *         may be thrown when the actual method is called via the callable
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
	 *        the method to check
	 * @return true if it is the root rule method, false otherwise
	 */
	private boolean isRoot(final Method method) {
		return ROOT.equals(method.getName()) && method.getParameterCount() == 0;
	}
	
	
	private static final String ROOT = "root";
	
	private final Map<Integer, Rule<T>> rules = new HashMap<>();
	
}

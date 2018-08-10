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

public class RuleMethodInterceptor<T> {
	
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
	
	private boolean isRoot(final Method method) {
		return ROOT.equals(method.getName()) && method.getParameterCount() == 0;
	}
	
	
	private static final String ROOT = "root";
	
	private final Map<Integer, Rule<T>> rules = new HashMap<>();
	
}

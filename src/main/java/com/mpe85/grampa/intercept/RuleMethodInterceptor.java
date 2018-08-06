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
	public Rule<T> root(
			@Origin final Method method,
			@SuperCall final Callable<Rule<T>> zuper)
			throws Exception {
		return intercept(method.getName(), method.getParameterCount() == 0, zuper);
	}
	
	@RuntimeType
	public Rule<T> other(
			@Origin final Method method,
			@SuperCall final Callable<Rule<T>> zuper,
			@AllArguments final Object... args)
			throws Exception {
		return intercept(method.getName(), false, zuper, args);
	}
	
	private Rule<T> intercept(
			final String ruleName,
			final boolean isRoot,
			final Callable<Rule<T>> zuper,
			final Object... args) throws Exception {
		final int hash = Objects.hash(ruleName, Objects.hash(args));
		
		if (!rules.containsKey(hash)) {
			rules.put(hash, null);
			final Rule<T> rule = zuper.call();
			rules.put(hash, rule);
			if (isRoot) {
				rule.accept(new ReferenceRuleReplaceVisitor<>(rules));
			}
			return rule;
		}
		return new ReferenceRule<>(hash);
	}
	
	
	private final Map<Integer, Rule<T>> rules = new HashMap<>();
	
}

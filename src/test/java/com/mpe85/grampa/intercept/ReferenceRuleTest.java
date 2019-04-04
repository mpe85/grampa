package com.mpe85.grampa.intercept;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Callable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mpe85.grampa.intercept.RuleMethodInterceptor.ReferenceRule;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;
import com.mpe85.grampa.rule.impl.EmptyRule;

@ExtendWith(MockitoExtension.class)
public class ReferenceRuleTest {
	
	static final class Foo implements Callable<Rule<Integer>> {
		public Rule<Integer> rule1() {
			return new EmptyRule<>();
		}
		
		public Rule<Integer> rule2() {
			return new EmptyRule<>();
		}
		
		@Override
		public Rule<Integer> call() throws Exception {
			return new EmptyRule<>();
		}
	}
	
	@Test
	public void equalsHashCodeToString() {
		final ReferenceRule<Integer> rule1 = createReferenceRule("rule1");
		final ReferenceRule<Integer> rule2 = createReferenceRule("rule1");
		final ReferenceRule<Integer> rule3 = createReferenceRule("rule2");
		
		assertTrue(rule1.equals(rule2));
		assertFalse(rule1.equals(rule3));
		assertFalse(rule1.equals(new Object()));
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
		assertNotEquals(rule1.hashCode(), rule3.hashCode());
		
		assertEquals(String.format("ReferenceRule{#children=0, hashCode=%d}", rule1.hashCode()), rule1.toString());
		assertEquals(String.format("ReferenceRule{#children=0, hashCode=%d}", rule2.hashCode()), rule2.toString());
		assertEquals(String.format("ReferenceRule{#children=0, hashCode=%d}", rule3.hashCode()), rule3.toString());
	}
	
	@Test
	public void match(@Mock final RuleContext<Integer> ctx) {
		final ReferenceRule<Integer> rule = createReferenceRule("rule1");
		
		assertFalse(rule.match(ctx));
	}
	
	private ReferenceRule<Integer> createReferenceRule(final String ruleMethod) {
		final RuleMethodInterceptor<Integer> interceptor = new RuleMethodInterceptor<>();
		
		final ThrowingSupplier<Rule<Integer>> supplier =
				() -> interceptor.intercept(Foo.class.getDeclaredMethod(ruleMethod), new Foo());
		
		assertDoesNotThrow(supplier);
		final Rule<Integer> rule = assertDoesNotThrow(supplier);
		
		assertTrue(rule instanceof ReferenceRule);
		
		return (ReferenceRule<Integer>) rule;
	}
	
}

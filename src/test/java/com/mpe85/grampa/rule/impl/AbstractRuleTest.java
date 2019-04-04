package com.mpe85.grampa.rule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mpe85.grampa.intercept.RuleMethodInterceptor.ReferenceRule;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;

@ExtendWith(MockitoExtension.class)
public class AbstractRuleTest {
	
	private class SomeRule extends AbstractRule<String> {
		public SomeRule() {
		}
		
		public SomeRule(final Rule<String> child) {
			super(child);
		}
		
		@Override
		public boolean match(final RuleContext<String> context) {
			return false;
		}
	}
	
	@Test
	public void equalsHashCodeToString() {
		final SomeRule rule1 = new SomeRule();
		final SomeRule rule2 = new SomeRule();
		
		assertTrue(rule1.equals(rule2));
		assertFalse(rule1.equals(new Object()));
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
		
		assertEquals("SomeRule{#children=0}", rule1.toString());
		assertEquals("SomeRule{#children=0}", rule2.toString());
	}
	
	@Test
	public void replaceReferenceRule_valid(@Mock final ReferenceRule<String> referenceRule) {
		final SomeRule rule = new SomeRule(referenceRule);
		
		assertNotNull(rule.replaceReferenceRule(0, new NeverRule<>()));
	}
	
	@Test
	public void replaceReferenceRule_invalid() {
		final SomeRule rule = new SomeRule(new EmptyRule<>());
		
		assertThrows(IllegalArgumentException.class, () -> rule.replaceReferenceRule(0, new NeverRule<>()));
	}
	
}
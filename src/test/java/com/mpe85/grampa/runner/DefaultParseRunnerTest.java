package com.mpe85.grampa.runner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.event.ParseEventListener;
import com.mpe85.grampa.event.PreMatchEvent;
import com.mpe85.grampa.parser.AbstractParser;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.impl.EmptyRule;

public class DefaultParseRunnerTest {
	
	private static final class IntegerTestListener extends ParseEventListener<Integer> {
		@Override
		public void beforeMatch(final PreMatchEvent<Integer> event) {
			throw new RuntimeException();
		}
	}
	
	@Test
	public void getRootRule() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return empty();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		assertTrue(runner.getRootRule() instanceof EmptyRule);
	}
	
	@Test
	public void registerListener() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return empty();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser(), (ex, ctx) -> {
			assertTrue(ex instanceof RuntimeException);
		});
		final ParseEventListener<Integer> listener = new IntegerTestListener();
		runner.registerListener(listener);
		runner.run("a");
		//assertDoesNotThrow(() -> runner.run("a"));
	}
	
	@Test
	public void unregisterListener() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return empty();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser(), (ex, ctx) -> {
			fail("Listener should not have been called.", ex);
		});
		final ParseEventListener<Integer> listener = new IntegerTestListener();
		runner.registerListener(listener);
		runner.unregisterListener(listener);
		runner.run("a");
	}
	
}

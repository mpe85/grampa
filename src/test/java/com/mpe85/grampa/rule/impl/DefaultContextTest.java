package com.mpe85.grampa.rule.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.eventbus.EventBus;
import com.mpe85.grampa.input.InputBuffer;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.util.stack.RestorableStack;

@ExtendWith(MockitoExtension.class)
public class DefaultContextTest {
	
	@Test
	public void advanceIndex_invalid(
			@Mock final InputBuffer ib,
			@Mock final Rule<String> rule,
			@Mock final RestorableStack<String> stack,
			@Mock final EventBus bus) {
		final DefaultContext<String> ctx = new DefaultContext<>(ib, 0, rule, 0, stack, bus);
		
		assertThrows(IllegalArgumentException.class, () -> ctx.advanceIndex(-1));
	}
	
}

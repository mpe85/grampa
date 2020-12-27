package com.mpe85.grampa.input.impl

import com.mpe85.grampa.input.InputBuffer
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.DefaultContext
import com.mpe85.grampa.rule.impl.DefaultContextState
import com.mpe85.grampa.stack.RestorableStack
import org.greenrobot.eventbus.EventBus
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class DefaultContextTest {
  @Test
  fun advanceIndex_invalid(
    @Mock ib: InputBuffer,
    @Mock rule: Rule<String>,
    @Mock stack: RestorableStack<String>,
    @Mock bus: EventBus
  ) {
    val ctx = DefaultContext(DefaultContextState(ib, 0, rule, 0, stack, bus))
    assertThrows(IllegalArgumentException::class.java) { ctx.advanceIndex(-1) }
  }
}

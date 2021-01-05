package com.mpe85.grampa.context.impl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk

class ContextTests : StringSpec({
  "Advance index" {
    Context<String>(ContextState(mockk(), 0, mockk(), 0, mockk(), mockk())).apply {
      shouldThrow<IllegalArgumentException> { advanceIndex(-1) }
    }
  }
})


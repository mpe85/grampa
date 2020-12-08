package com.mpe85.grampa.exception

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@TestInstance(PER_CLASS)
class ActionRunExceptionTest {

  @Test
  fun create() {
    val cause = RuntimeException()
    val ex1 = ActionRunException("someMessage")
    val ex2 = ActionRunException(cause)
    val ex3 = ActionRunException("someMessage", cause)

    assertEquals("someMessage", ex1.message)
    assertNull(ex1.cause)
    assertEquals(cause.toString(), ex2.message)
    assertEquals(cause, ex2.cause)
    assertEquals("someMessage", ex3.message)
    assertEquals(cause, ex3.cause)
  }

}
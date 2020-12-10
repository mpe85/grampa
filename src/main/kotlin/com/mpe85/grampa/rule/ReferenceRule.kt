package com.mpe85.grampa.rule

interface ReferenceRule<T> : Rule<T> {

  override fun hashCode(): Int

}
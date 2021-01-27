package com.mpe85.grampa

import com.ibm.icu.lang.UCharacter
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.codepoints
import io.kotest.property.arbitrary.filter

/**
 * Create an [Arb] of legal [Codepoint] according to [UCharacter.isLegal].
 *
 * @return An [Arb] of [Codepoint]s
 */
internal fun legalCodePoints() = Arb.codepoints().filter { UCharacter.isLegal(it.value) }

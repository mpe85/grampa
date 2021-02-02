package com.mpe85.grampa

import com.ibm.icu.lang.UCharacter
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.codepoints
import io.kotest.property.arbitrary.filter

/**
 * Create an [Arb] of legal [Codepoint] according to [UCharacter.isLegal].
 *
 * @return An [Arb] of [Codepoint]s
 */
internal fun legalCodePoints() = Arb.codepoints().filter { UCharacter.isLegal(it.value) }

/**
 * Create an [Arb] of lower case [Codepoint]s that differ from the [Codepoint] returned by [UCharacter.toUpperCase].
 *
 * @return An [Arb] of [Codepoint]s
 */
internal fun lowerCaseCodePoints() = legalCodePoints().filter {
    UCharacter.isLowerCase(it.value) && it.value != UCharacter.toUpperCase(it.value)
}

/**
 * Create an [Arb] of upper case [Codepoint]s that differ from the [Codepoint] returned by [UCharacter.toLowerCase].
 *
 * @return An [Arb] of [Codepoint]s
 */
internal fun upperCaseCodePoints() = legalCodePoints().filter {
    UCharacter.isUpperCase(it.value) && it.value != UCharacter.toLowerCase(it.value)
}

/**
 * Create an [Arb] of lower case [Char]s that differ from the [Char] returned by [Char.toUpperCase].
 *
 * @return An [Arb] of [Char]s
 */
internal fun lowerCaseChars() = Arb.char().filter {
    it.isLowerCase() && it != it.toUpperCase()
}

/**
 * Create an [Arb] of upper case [Char]s that differ from the [Char] returned by [Char.toLowerCase].
 *
 * @return An [Arb] of [Char]s
 */
internal fun upperCaseChars() = Arb.char().filter {
    it.isUpperCase() && it != it.toLowerCase()
}

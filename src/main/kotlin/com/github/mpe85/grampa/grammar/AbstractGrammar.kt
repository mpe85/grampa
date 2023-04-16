package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.context.RuleContext
import com.github.mpe85.grampa.rule.Action
import com.github.mpe85.grampa.rule.ActionRule
import com.github.mpe85.grampa.rule.CharPredicateRule
import com.github.mpe85.grampa.rule.ChoiceRule
import com.github.mpe85.grampa.rule.CodePointPredicateRule
import com.github.mpe85.grampa.rule.Command
import com.github.mpe85.grampa.rule.ConditionalRule
import com.github.mpe85.grampa.rule.EmptyRule
import com.github.mpe85.grampa.rule.EndOfInputRule
import com.github.mpe85.grampa.rule.IgnoreCaseCharRule
import com.github.mpe85.grampa.rule.IgnoreCaseCodePointRule
import com.github.mpe85.grampa.rule.IgnoreCaseTrieRule
import com.github.mpe85.grampa.rule.NeverRule
import com.github.mpe85.grampa.rule.RegexRule
import com.github.mpe85.grampa.rule.RepeatRule
import com.github.mpe85.grampa.rule.Rule
import com.github.mpe85.grampa.rule.SequenceRule
import com.github.mpe85.grampa.rule.StringRule
import com.github.mpe85.grampa.rule.TestNotRule
import com.github.mpe85.grampa.rule.TestRule
import com.github.mpe85.grampa.rule.TrieRule
import com.github.mpe85.grampa.rule.toAction
import com.github.mpe85.grampa.util.UnboundedRange
import com.github.mpe85.grampa.util.max
import com.ibm.icu.lang.UCharacter
import kotlin.streams.toList

/**
 * An abstract grammar that defines a bunch of useful grammar rules and actions.
 * A concrete grammar class should usually extend this class.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
@Suppress("Detekt.TooManyFunctions")
public abstract class AbstractGrammar<T> : Grammar<T> {

    /**
     * A rule that matches an empty string.
     * Or in other words, a rule that matches no input character and always succeeds.
     *
     * @return The created grammar rule
     */
    protected open fun empty(): Rule<T> = EmptyRule()

    /**
     * A rule that always fails.
     *
     * @return The created grammar rule
     */
    protected open fun never(): Rule<T> = NeverRule()

    /**
     * A rule that matches the end of the input.
     *
     * @return The created grammar rule
     */
    protected open fun eoi(): Rule<T> = EndOfInputRule()

    /**
     * A rule that matches any character.
     *
     * @return The created grammar rule
     */
    protected open fun anyChar(): Rule<T> = CharPredicateRule { true }

    /**
     * A rule that matches a specific character.
     *
     * @param[character] The character to match
     * @return The created grammar rule
     */
    protected open fun char(character: Char): Rule<T> = CharPredicateRule(character)

    /**
     * A rule that matches [this] character.
     *
     * @return The created grammar rule
     */
    protected open fun Char.toRule(): Rule<T> = char(this)

    /**
     * A rule that matches a specific character, ignoring the case of the character (case-insensitive).
     *
     * @param[character] The character to match
     * @return The created grammar rule
     */
    protected open fun ignoreCase(character: Char): Rule<T> = IgnoreCaseCharRule(character)

    /**
     * A rule that matches [this] character, ignoring its case (case-insensitive).
     *
     * @return The created grammar rule
     */
    protected open fun Char.toIgnoreCaseRule(): Rule<T> = ignoreCase(this)

    /**
     * A rule that matches a character within a range of characters.
     *
     * @param[lowerBound] The lower bound of the character range (inclusive)
     * @param[upperBound] The upper bound of the character range (inclusive)
     * @return The created grammar rule
     */
    protected open fun charRange(lowerBound: Char, upperBound: Char): Rule<T> {
        require(lowerBound <= upperBound) { "A 'lowerBound' must not be greater than an 'upperBound'." }
        return CharPredicateRule { it in lowerBound..upperBound }
    }

    /**
     * A rule that matches a character within [this] range of characters.
     *
     * @return The created grammar rule
     */
    protected open fun CharRange.toRule(): Rule<T> = charRange(first, last)

    /**
     * A rule that matches a character within a set of characters.
     *
     * @param[characters] A collection of characters
     * @return The created grammar rule
     */
    protected open fun anyOfChars(characters: Collection<Char>): Rule<T> = when {
        characters.isEmpty() -> never()
        characters.size == 1 -> characters.first().toRule()
        else -> CharPredicateRule { characters.sorted().binarySearch(it) >= 0 }
    }

    /**
     * A rule that matches a character within a set of characters.
     *
     * @param[characters] A variable number of characters
     * @return The created grammar rule
     */
    protected open fun anyOfChars(vararg characters: Char): Rule<T> = anyOfChars(characters.toList())

    /**
     * A rule that matches a character within a set of characters.
     *
     * @param[characters] A string containing the set of characters.
     * @return The created grammar rule
     */
    protected open fun anyOfChars(characters: String): Rule<T> = anyOfChars(characters.toCharArray().toList())

    /**
     * A rule that matches a character not in a set of characters.
     *
     * @param[characters] A collection of characters
     * @return The created grammar rule
     */
    protected open fun noneOfChars(characters: Collection<Char>): Rule<T> = when {
        characters.isEmpty() -> anyChar()
        else -> CharPredicateRule { characters.sorted().binarySearch(it) < 0 }
    }

    /**
     * A rule that matches a character not in a set of characters.
     *
     * @param[characters] A variable number of characters
     * @return The created grammar rule
     */
    protected open fun noneOfChars(vararg characters: Char): Rule<T> = noneOfChars(characters.toList())

    /**
     * A rule that matches a character not in a set of characters.
     *
     * @param[characters] A string containing the set of characters.
     * @return The created grammar rule
     */
    protected open fun noneOfChars(characters: String): Rule<T> = noneOfChars(characters.toCharArray().toList())

    /**
     * A rule that matches any code point.
     *
     * @return The created grammar rule
     */
    protected open fun anyCodePoint(): Rule<T> = CodePointPredicateRule(UCharacter::isLegal)

    /**
     * A rule that matches a specific code point.
     *
     * @param[codePoint] The code point to match
     * @return The created grammar rule
     */
    protected open fun codePoint(codePoint: Int): Rule<T> = CodePointPredicateRule(codePoint)

    /**
     * A rule that matches [this] code point.
     *
     * @return The created grammar rule
     */
    protected open fun Int.toRule(): Rule<T> = codePoint(this)

    /**
     * A rule that matches a specific code point, ignoring the case of the code point (case-insensitive).
     *
     * @param[codePoint] The code point to match
     * @return The created grammar rule
     */
    protected open fun ignoreCase(codePoint: Int): Rule<T> = IgnoreCaseCodePointRule(codePoint)

    /**
     * A rule that matches [this] code point, ignoring its case (case-insensitive).
     *
     * @return The created grammar rule
     */
    protected open fun Int.toIgnoreCaseRule(): Rule<T> = ignoreCase(this)

    /**
     * A rule that matches a code point within a range of code points.
     *
     * @param[lowerBound] The lower bound of the code point range (inclusive)
     * @param[upperBound] The upper bound of the code point range (inclusive)
     * @return The created grammar rule
     */
    protected open fun codePointRange(lowerBound: Int, upperBound: Int): Rule<T> {
        require(lowerBound <= upperBound) { "A 'lowerBound' must not be greater than an 'upperBound'." }
        return CodePointPredicateRule { it in lowerBound..upperBound }
    }

    /**
     * A rule that matches a code point within [this] range of code points.
     *
     * @return The created grammar rule
     */
    protected open fun IntRange.toRule(): Rule<T> = codePointRange(first, last)

    /**
     * A rule that matches a code point within a set of code points.
     *
     * @param[codePoints] A collection of code points.
     * @return The created grammar rule
     */
    protected open fun anyOfCodePoints(codePoints: Collection<Int>): Rule<T> = when {
        codePoints.isEmpty() -> never()
        codePoints.size == 1 -> codePoints.first().toRule()
        else -> CodePointPredicateRule { codePoints.sorted().binarySearch(it) >= 0 }
    }

    /**
     * A rule that matches a code point within a set of code points.
     *
     * @param[codePoints] A variable number of code points
     * @return The created grammar rule
     */
    protected open fun anyOfCodePoints(vararg codePoints: Int): Rule<T> = anyOfCodePoints(codePoints.toList())

    /**
     * A rule that matches a code point within a set of code points.
     *
     * @param[codePoints] A string containing the set of code points.
     * @return The created grammar rule
     */
    protected open fun anyOfCodePoints(codePoints: String): Rule<T> = anyOfCodePoints(codePoints.codePoints().toList())

    /**
     * A rule that matches a code point not in a set of code points.
     *
     * @param[codePoints] A collection of code points.
     * @return The created grammar rule
     */
    protected open fun noneOfCodePoints(codePoints: Collection<Int>): Rule<T> = when {
        codePoints.isEmpty() -> anyCodePoint()
        else -> CodePointPredicateRule { codePoints.sorted().binarySearch(it) < 0 }
    }

    /**
     * A rule that matches a code point not in a set of code points.
     *
     * @param[codePoints] A variable number of code points
     * @return The created grammar rule
     */
    protected open fun noneOfCodePoints(vararg codePoints: Int): Rule<T> = noneOfCodePoints(codePoints.toList())

    /**
     * A rule that matches a code point not in a set of code points.
     *
     * @param[codePoints] A string containing the set of code points.
     * @return The created grammar rule
     */
    protected open fun noneOfCodePoints(codePoints: String): Rule<T> =
        noneOfCodePoints(codePoints.codePoints().toList())

    /**
     * A rule that matches a specific string.
     *
     * @param[string] The string to match
     * @return The created grammar rule
     */
    protected open fun string(string: String): Rule<T> = when {
        string.isEmpty() -> empty()
        string.length == 1 -> string.first().toRule()
        string.codePoints().count() == 1L -> string.codePointAt(0).toRule()
        else -> StringRule(string)
    }

    /**
     * A rule that matches [this] string.
     *
     * @return The created grammar rule
     */
    protected open fun String.toRule(): Rule<T> = string(this)

    /**
     * A rule that matches a specific string, ignoring the case of its characters (case-insensitive).
     *
     * @param[string] The string to match
     * @return The created grammar rule
     */
    protected open fun ignoreCase(string: String): Rule<T> = when {
        string.isEmpty() -> empty()
        else -> IgnoreCaseTrieRule(string)
    }

    /**
     * A rule that matches [this] string, ignoring the case of its characters (case-insensitive).
     *
     * @return The created grammar rule
     */
    protected open fun String.toIgnoreCaseRule(): Rule<T> = ignoreCase(this)

    /**
     * A rule that matches a regular expression.
     *
     * @param[regex] A regular expression
     * @return The created grammar rule
     */
    protected open fun regex(regex: String): Rule<T> = RegexRule(regex)

    /**
     * A rule that matches [this] regular expression.
     *
     * @return The created grammar rule
     */
    protected open fun String.toRegexRule(): Rule<T> = regex(this)

    /**
     * A rule that matches a string within a set of strings.
     *
     * @param[strings] A collection of strings
     * @return The created grammar rule
     */
    protected open fun strings(strings: Collection<String>): Rule<T> = when {
        strings.isEmpty() -> never()
        strings.size == 1 -> strings.first().toRule()
        else -> TrieRule(strings)
    }

    /**
     * A rule that matches a string within a set of strings.
     *
     * @param[strings] A variable number of strings
     * @return The created grammar rule
     */
    protected open fun strings(vararg strings: String): Rule<T> = strings(strings.toList())

    /**
     * A rule that matches a string within a set of strings, ignoring the case of their characters (case-insensitive).
     *
     * @param[strings] A collection of strings
     * @return The created grammar rule
     */
    protected open fun ignoreCase(strings: Collection<String>): Rule<T> = when {
        strings.isEmpty() -> never()
        strings.size == 1 -> ignoreCase(strings.first())
        else -> IgnoreCaseTrieRule(strings)
    }

    /**
     * A rule that matches a string within a set of strings, ignoring the case of their characters (case-insensitive).
     *
     * @param[strings] A variable number of strings
     * @return The created grammar rule
     */
    protected open fun ignoreCase(vararg strings: String): Rule<T> = ignoreCase(strings.toList())

    /**
     * A rule that matches an ASCII character.
     *
     * @return The created grammar rule
     */
    protected open fun ascii(): Rule<T> = CharPredicateRule { it in Char.MIN_VALUE..Byte.MAX_VALUE.toInt().toChar() }

    /**
     * A rule that matches a characters of Unicode's Basic Multilingual Plane.
     *
     * @return The created grammar rule
     */
    protected open fun bmp(): Rule<T> = CodePointPredicateRule(UCharacter::isBMP)

    /**
     * A rule that matches a digit.
     *
     * @return The created grammar rule
     */
    protected open fun digit(): Rule<T> = CodePointPredicateRule(UCharacter::isDigit)

    /**
     * A rule that matches a character which is valid to be the first character of a Java identifier.
     *
     * @return The created grammar rule
     */
    protected open fun javaIdentifierStart(): Rule<T> = CodePointPredicateRule(Character::isJavaIdentifierStart)

    /**
     * A rule that matches a character which is valid to be part of a Java identifier, other than the first character.
     *
     * @return The created grammar rule
     */
    protected open fun javaIdentifierPart(): Rule<T> = CodePointPredicateRule(Character::isJavaIdentifierPart)

    /**
     * A rule that matches a letter.
     *
     * @return The created grammar rule
     */
    protected open fun letter(): Rule<T> = CodePointPredicateRule(UCharacter::isLetter)

    /**
     * A rule that matches a letter or a digit.
     *
     * @return The created grammar rule
     */
    protected open fun letterOrDigit(): Rule<T> = CodePointPredicateRule(UCharacter::isLetterOrDigit)

    /**
     * A rule that matches a printable character.
     *
     * @return The created grammar rule
     */
    protected open fun printable(): Rule<T> = CodePointPredicateRule(UCharacter::isPrintable)

    /**
     * A rule that matches a space character.
     *
     * @return The created grammar rule
     */
    protected open fun spaceChar(): Rule<T> = CodePointPredicateRule(UCharacter::isSpaceChar)

    /**
     * A rule that matches a whitespace character.
     *
     * @return The created grammar rule
     */
    protected open fun whitespace(): Rule<T> = CodePointPredicateRule(UCharacter::isWhitespace)

    /**
     * A rule that matches the carriage return character.
     *
     * @return The created grammar rule
     */
    protected open fun cr(): Rule<T> = CharPredicateRule('\r')

    /**
     * A rule that matches the line feed character.
     *
     * @return The created grammar rule
     */
    protected open fun lf(): Rule<T> = CharPredicateRule('\n')

    /**
     * A rule that matches the carriage return and line feed characters.
     *
     * @return The created grammar rule
     */
    protected open fun crlf(): Rule<T> = StringRule("\r\n")

    /**
     * A rule that matches a sequence of other rules.
     *
     * @param[rules] A variable number of rules
     * @return The created grammar rule
     */
    protected open fun sequence(vararg rules: Rule<T>): Rule<T> = sequence(rules.toList())

    /**
     * A rule that matches a sequence of other rules.
     *
     * @param[rules] A list of rules
     * @return The created grammar rule
     */
    protected open fun sequence(rules: List<Rule<T>>): Rule<T> = when {
        rules.isEmpty() -> empty()
        rules.size == 1 -> rules.first()
        else -> SequenceRule(rules)
    }

    /**
     * A sequence rule that matches [this] and another rule.
     *
     * @param[other] Another rule
     * @return The created grammar rule
     */
    protected open infix fun Rule<T>.and(other: Rule<T>): Rule<T> = sequence(listOf(this, other))

    /**
     * A sequence rule that matches [this] and another rule.
     *
     * @param[other] Another rule
     * @return The created grammar rule
     */
    protected open operator fun Rule<T>.plus(other: Rule<T>): Rule<T> = sequence(listOf(this, other))

    /**
     * A rule that matches the first successful rule in a list of other rules.
     *
     * @param[rules] A variable number of rules
     * @return The created grammar rule
     */
    protected open fun choice(vararg rules: Rule<T>): Rule<T> = choice(rules.toList())

    /**
     * A rule that matches the first successful rule in a list of other rules.
     *
     * @param[rules] A list of rules
     * @return The created grammar rule
     */
    protected open fun choice(rules: List<Rule<T>>): Rule<T> = when {
        rules.isEmpty() -> empty()
        rules.size == 1 -> rules.first()
        else -> ChoiceRule(rules)
    }

    /**
     * A choice rule that matches [this] rule if successful or another rule.
     *
     * @param[other] Another rule
     * @return The created grammar rule
     */
    protected open infix fun Rule<T>.or(other: Rule<T>): Rule<T> = choice(listOf(this, other))

    /**
     * A rule that matches its child rule optionally.
     * In other words, a rule that repeats its child rule zero or one time.
     *
     * @param[rule] The child rule to match optionally
     * @return The created grammar rule
     */
    protected open fun optional(rule: Rule<T>): Rule<T> = RepeatRule(rule, 0, 1)

    /**
     * A rule that matches its child rule zero or more times.
     *
     * @param[rule] The child rule to repeat
     * @return The created grammar rule
     */
    protected open fun zeroOrMore(rule: Rule<T>): Rule<T> = RepeatRule(rule, 0)

    /**
     * A rule that matches its child rule one or more times.
     *
     * @param[rule] The child rule to repeat
     * @return The created grammar rule
     */
    protected open fun oneOrMore(rule: Rule<T>): Rule<T> = RepeatRule(rule, 1)

    /**
     * Repeat a rule exactly [n] times.
     *
     * @param[rule] The child rule to repeat
     * @param[n] The number of repetitions
     * @return The created grammar rule
     */
    protected open fun repeat(rule: Rule<T>, n: Int): Rule<T> = RepeatRule(rule, n, n)

    /**
     * Repeat a rule between [min] and [max] times.
     *
     * @param[rule] The child rule to repeat
     * @param[min] The minimum number of repetitions
     * @param[max] The maximum number of repetitions, may be null (unbounded)
     * @return The created grammar rule
     */
    protected open fun repeat(rule: Rule<T>, min: Int = 0, max: Int? = null): Rule<T> = RepeatRule(rule, min, max)

    /**
     * Repeat a rule exactly [this] times.
     *
     * @param[rule] The rule to repeat
     * @return The created grammar rule
     */
    protected open operator fun Int.times(rule: Rule<T>): Rule<T> = repeat(rule, this)

    /**
     * Repeat a rule between [first] and [last] times.
     *
     * @param[rule] The rule to repeat
     * @return The created grammar rule
     */
    protected open operator fun IntRange.times(rule: Rule<T>): Rule<T> = repeat(rule, first, last)

    /**
     * Repeat a rule between [min] and [max] times, where [max] may be unbounded.
     *
     * @param[rule] The rule to repeat
     * @return The created grammar rule
     */
    protected open operator fun UnboundedRange.times(rule: Rule<T>): Rule<T> = repeat(rule, min, max)

    /**
     * A test rule that tests if its child rule matches.
     *
     * @param[rule] The child rule to test
     * @return The created grammar rule
     */
    protected open fun test(rule: Rule<T>): Rule<T> = TestRule(rule)

    /**
     * A test rule that tests if [this] rule matches.
     *
     * @return The created grammar rule
     */
    protected open fun Rule<T>.toTest(): Rule<T> = test(this)

    /**
     * A test rule that tests if its child rule does not match.
     *
     * @param[rule] The child rule to test
     * @return The created grammar rule
     */
    protected open fun testNot(rule: Rule<T>): Rule<T> = TestNotRule(rule)

    /**
     * A test rule that tests if [this] rule does not match.
     *
     * @return The created grammar rule
     */
    protected open fun Rule<T>.toTestNot(): Rule<T> = testNot(this)

    /**
     * A conditional rule that runs a child rule if a condition is true, otherwise it runs another child rule.
     *
     * @param[condition] The condition to check
     * @param[thenRule] The rule to run if the condition is true
     * @param[elseRule] The rule to run if the condition is false
     * @return The created grammar rule
     */
    protected open fun conditional(
        condition: (RuleContext<T>) -> Boolean,
        thenRule: Rule<T>,
        elseRule: Rule<T>,
    ): Rule<T> = ConditionalRule(condition, thenRule, elseRule)

    /**
     * A conditional rule that runs a child rule if a condition is true, otherwise it runs no rule.
     *
     * @param[condition] The condition to check
     * @param[thenRule] The rule to run if the condition is true
     * @return The created grammar rule
     */
    protected open fun conditional(condition: (RuleContext<T>) -> Boolean, thenRule: Rule<T>): Rule<T> =
        ConditionalRule(condition, thenRule)

    /**
     * A rule that runs an action.
     *
     * @param[action] The action to run
     * @return The created grammar rule
     */
    protected open fun action(action: Action<T>): Rule<T> = ActionRule(action::run)

    /**
     * A rule that runs an action. The action is skipped if the rule is run inside a test rule.
     *
     * @param[action] The skippable action to run
     * @return The created grammar rule
     */
    protected open fun skippableAction(action: Action<T>): Rule<T> = ActionRule(action::run, true)

    /**
     * A rule that runs [this] action function.
     *
     * @param[skippable] Whether the action should be skippable
     * @return The created grammar rule
     */
    protected open fun ((RuleContext<T>) -> Boolean).toActionRule(skippable: Boolean = false): Rule<T> =
        ActionRule(this, skippable)

    /**
     * A rule that runs [this] action.
     *
     * @param[skippable] Whether the action should be skippable
     * @return The created grammar rule
     */
    protected open fun Action<T>.toRule(skippable: Boolean = false): Rule<T> = ActionRule(::run, skippable)

    /**
     * A rule that executes a command.
     *
     * @param[command] The command to execute
     * @return The created grammar rule
     */
    protected open fun command(command: Command<T>): Rule<T> = action(command.toAction())

    /**
     * A rule that executes a command. The command is skipped if the rule is run inside a test rule.
     *
     * @param[command] The command to execute
     * @return The created grammar rule
     */
    protected open fun skippableCommand(command: Command<T>): Rule<T> = skippableAction(command.toAction())

    /**
     * A rule that executes [this] command function.
     *
     * @param[skippable] Whether the command should be skippable
     * @return The created grammar rule
     */
    protected open fun ((RuleContext<T>) -> Unit).toCommandRule(skippable: Boolean = false): Rule<T> =
        ActionRule(toAction(), skippable)

    /**
     * A rule that executes [this] command.
     *
     * @param[skippable] Whether the command should be skippable
     * @return The created grammar rule
     */
    protected open fun Command<T>.toRule(skippable: Boolean = false): Rule<T> = ActionRule(toAction()::run, skippable)

    /**
     * Post a static event to the parser's event bus.
     * Note that the event object is constructed at grammar create time (thus static).
     *
     * @param[event] The event to post
     * @return The created grammar rule
     */
    protected open fun post(event: Any): Rule<T> = skippableCommand { it.bus.post(event) }

    /**
     * Post a dynamic event to the parser's event bus.
     * Note that the event object is supplied by an event supplier at parser runtime
     * which has access to the parser context (thus dynamic).
     *
     * @param[supplier] An event supplier that is called when the rule is run
     * @return The created grammar rule
     */
    protected open fun post(supplier: (RuleContext<T>) -> Any): Rule<T> = skippableCommand { it.bus.post(supplier(it)) }

    /**
     * Pop the top level element from the stack.
     *
     * @return The created grammar rule
     */
    protected open fun pop(): Rule<T> = command { it.stack.pop() }

    /**
     * Pop an element from the stack at a given position.
     *
     * @param[down] The number of elements on the stack to skip
     * @return The created grammar rule
     */
    protected open fun pop(down: Int): Rule<T> = command { it.stack.pop(down) }

    /**
     * Replace the element on top of the stack.
     * Note that the replacement value is constructed at grammar create time.
     *
     * @param[value] A replacement value
     * @return The created grammar rule
     */
    protected open fun poke(value: T): Rule<T> = command { it.stack.poke(value) }

    /**
     * Replace an element in the stack at a given position.
     * Note that the replacement value is constructed at grammar create time.
     *
     * @param[down] The number of elements on the stack to skip
     * @param[value] A replacement value
     * @return The created grammar rule
     */
    protected open fun poke(down: Int, value: T): Rule<T> = command { it.stack.poke(down, value) }

    /**
     * Replace an element in the stack.
     * Note that the replacement value is supplied by a value supplier at parser runtime
     * which has access to the parser context.
     *
     * @param[supplier] A replacement value supplier
     * @return The created grammar rule
     */
    protected open fun poke(supplier: (RuleContext<T>) -> T): Rule<T> = command { it.stack.poke(supplier(it)) }

    /**
     * Replace an element in the stack at a given position.
     * Note that the replacement value is supplied by a value supplier at parser runtime
     * which has access to the parser context.
     *
     * @param[down] The number of elements on the stack to skip
     * @param[supplier] A replacement value supplier
     * @return The created grammar rule
     */
    protected open fun poke(down: Int, supplier: (RuleContext<T>) -> T): Rule<T> =
        command { it.stack.poke(down, supplier(it)) }

    /**
     * Push a new element onto the stack. Note that the value is constructed at grammar create time.
     *
     * @param[value] The value to push
     * @return The created grammar rule
     */
    protected open fun push(value: T): Rule<T> = command { it.stack.push(value) }

    /**
     * Push a new element onto the stack at a given position.
     * Note that the value is constructed at grammar create time.
     *
     * @param[down] The number of elements on the stack to skip
     * @param[value] The value to push
     * @return The created grammar rule
     */
    protected open fun push(down: Int, value: T): Rule<T> = command { it.stack.push(down, value) }

    /**
     * Push a new element onto the stack.
     * Note that the value is supplied by a value supplier at parser runtime which has access to the parser context.
     *
     * @param[supplier] The value supplier that supplies the value to push
     * @return The created grammar rule
     */
    protected open fun push(supplier: (RuleContext<T>) -> T): Rule<T> = command { it.stack.push(supplier(it)) }

    /**
     * Push a new element onto the stack at a given position.
     * Note that the value is supplied by a value supplier at parser runtime which has access to the parser context.
     *
     * @param[down] The number of elements on the stack to skip
     * @param[supplier] The value supplier that supplies the value to push
     * @return The created grammar rule
     */
    protected open fun push(down: Int, supplier: (RuleContext<T>) -> T): Rule<T> =
        command { it.stack.push(down, supplier(it)) }

    /**
     * Duplicate the top stack element.
     *
     * @return The created grammar rule
     */
    protected open fun dup(): Rule<T> = command { it.stack.dup() }

    /**
     * Swap the two top stack elements.
     *
     * @return The created grammar rule
     */
    protected open fun swap(): Rule<T> = command { it.stack.swap() }

    /**
     * Pop the top level element from the stack.
     * This function may be called by an action or command where the rule context is available.
     *
     * @param[context] The action context
     * @return The popped element
     */
    protected fun pop(context: RuleContext<T>): T = context.stack.pop()

    /**
     * Pop an element from the stack at a given position.
     * This function may be called by an action or command where the rule context is available.
     *
     * @param[down] The number of elements on the stack to skip
     * @param[context] The action context
     * @return The popped element
     */
    protected fun pop(down: Int, context: RuleContext<T>): T = context.stack.pop(down)

    /**
     * Replace the element on top of the stack.
     * This function may be called by an action or command where the rule context is available.
     *
     * @param[value] The replacement value
     * @return The replaced element
     */
    protected fun poke(context: RuleContext<T>, value: T): T = context.stack.poke(value)

    /**
     * Replace an element on the stack at a given position.
     * This function may be called by an action or command where the rule context is available.
     *
     * @param[down] The number of elements on the stack to skip
     * @param[value] The replacement value
     * @return The replaced element
     */
    protected fun poke(down: Int, context: RuleContext<T>, value: T): T = context.stack.poke(down, value)

    /**
     * Peek the top level element from the stack.
     * This function may be called by an action or command where the rule context is available.
     *
     * @param[context] The action context
     * @return The peeked element
     */
    protected fun peek(context: RuleContext<T>): T = context.stack.peek()

    /**
     * Peek an element from the stack at a given position.
     * This function may be called by an action or command where the rule is available.
     *
     * @param[down] The number of elements on the stack to skip
     * @param[context] The action context
     * @return The peeked element
     */
    protected fun peek(down: Int, context: RuleContext<T>): T = context.stack.peek(down)
}

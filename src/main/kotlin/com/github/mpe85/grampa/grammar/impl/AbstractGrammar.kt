package com.github.mpe85.grampa.grammar.impl

import com.github.mpe85.grampa.context.RuleContext
import com.github.mpe85.grampa.grammar.Grammar
import com.github.mpe85.grampa.rule.Action
import com.github.mpe85.grampa.rule.Command
import com.github.mpe85.grampa.rule.Rule
import com.github.mpe85.grampa.rule.impl.ActionRule
import com.github.mpe85.grampa.rule.impl.CharPredicateRule
import com.github.mpe85.grampa.rule.impl.CodePointPredicateRule
import com.github.mpe85.grampa.rule.impl.ConditionalRule
import com.github.mpe85.grampa.rule.impl.EmptyRule
import com.github.mpe85.grampa.rule.impl.EndOfInputRule
import com.github.mpe85.grampa.rule.impl.FirstOfRule
import com.github.mpe85.grampa.rule.impl.IgnoreCaseTrieRule
import com.github.mpe85.grampa.rule.impl.NeverRule
import com.github.mpe85.grampa.rule.impl.RegexRule
import com.github.mpe85.grampa.rule.impl.SequenceRule
import com.github.mpe85.grampa.rule.impl.StringRule
import com.github.mpe85.grampa.rule.impl.TestNotRule
import com.github.mpe85.grampa.rule.impl.TestRule
import com.github.mpe85.grampa.rule.impl.TrieRule
import com.github.mpe85.grampa.rule.impl.times
import com.github.mpe85.grampa.rule.impl.toIgnoreCaseRule
import com.github.mpe85.grampa.rule.impl.toRule
import com.github.mpe85.grampa.rule.toAction
import com.github.mpe85.grampa.util.max
import com.github.mpe85.grampa.util.min
import com.github.mpe85.grampa.util.range
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

    private val emptyRule by lazy { EmptyRule<T>() }
    private val neverRule by lazy { NeverRule<T>() }
    private val eoiRule by lazy { EndOfInputRule<T>() }
    private val anyCharRule by lazy { CharPredicateRule<T> { true } }
    private val anyCodePointRule by lazy { CodePointPredicateRule<T>(UCharacter::isLegal) }
    private val asciiRule by lazy { CharPredicateRule<T> { it in Char.MIN_VALUE..Byte.MAX_VALUE.toChar() } }
    private val bmpRule by lazy { CodePointPredicateRule<T>(UCharacter::isBMP) }
    private val digitRule by lazy { CodePointPredicateRule<T>(UCharacter::isDigit) }
    private val javaIdentStartRule by lazy { CodePointPredicateRule<T>(Character::isJavaIdentifierStart) }
    private val javaIdentPartRule by lazy { CodePointPredicateRule<T>(Character::isJavaIdentifierPart) }
    private val letterRule by lazy { CodePointPredicateRule<T>(UCharacter::isLetter) }
    private val letterOrDigitRule by lazy { CodePointPredicateRule<T>(UCharacter::isLetterOrDigit) }
    private val printableRule by lazy { CodePointPredicateRule<T>(UCharacter::isPrintable) }
    private val spaceCharRule by lazy { CodePointPredicateRule<T>(UCharacter::isSpaceChar) }
    private val whitespaceRule by lazy { CodePointPredicateRule<T>(UCharacter::isWhitespace) }
    private val crRule by lazy { CharPredicateRule<T>('\r') }
    private val lfRule by lazy { CharPredicateRule<T>('\n') }
    private val crlfRule by lazy { StringRule<T>("\r\n") }

    /**
     * A rule that matches an empty string.
     * Or in other words, a rule that matches no input character and always succeeds.
     *
     * @return A grammar rule
     */
    protected open fun empty(): Rule<T> = emptyRule

    /**
     * A rule that always fails.
     *
     * @return A grammar rule
     */
    protected open fun never(): Rule<T> = neverRule

    /**
     * A rule that matches the end of the input.
     *
     * @return A grammar rule
     */
    protected open fun eoi(): Rule<T> = eoiRule

    /**
     * A rule that matches any character.
     *
     * @return A grammar rule
     */
    protected open fun anyChar(): Rule<T> = anyCharRule

    /**
     * A rule that matches any code point.
     *
     * @return A grammar rule
     */
    protected open fun anyCodePoint(): Rule<T> = anyCodePointRule

    /**
     * A rule that matches a specific character.
     *
     * @param[character] The character to match
     * @return A grammar rule
     */
    protected open fun char(character: Char): Rule<T> = character.toRule()

    /**
     * A rule that matches a specific character, ignoring the case of the character (case-insensitive).
     *
     * @param[character] The character to match
     * @return A grammar rule
     */
    protected open fun ignoreCase(character: Char): Rule<T> = character.toIgnoreCaseRule()

    /**
     * A rule that matches a character within a range of characters.
     *
     * @param[lowerBound] The lower bound of the character range (inclusive)
     * @param[upperBound] The upper bound of the character range (inclusive)
     * @return A grammar rule
     */
    protected open fun charRange(lowerBound: Char, upperBound: Char): Rule<T> {
        Char.MIN_VALUE
        require(lowerBound <= upperBound) { "A 'lowerBound' must not be greater than an 'upperBound'." }
        return CharPredicateRule { it in lowerBound..upperBound }
    }

    /**
     * A rule that matches a character within a set of characters.
     *
     * @param[characters] A collection of characters
     * @return A grammar rule
     */
    protected open fun anyOfChars(characters: Collection<Char>): Rule<T> = when {
        characters.isEmpty() -> neverRule
        characters.size == 1 -> characters.first().toRule()
        else -> CharPredicateRule { characters.sorted().binarySearch(it) >= 0 }
    }

    /**
     * A rule that matches a character within a set of characters.
     *
     * @param[characters] A variable number of characters
     * @return A grammar rule
     */
    protected open fun anyOfChars(vararg characters: Char): Rule<T> = anyOfChars(characters.toList())

    /**
     * A rule that matches a character within a set of characters.
     *
     * @param[characters] A string containing the set of characters.
     * @return A grammar rule
     */
    protected open fun anyOfChars(characters: String): Rule<T> = anyOfChars(characters.toCharArray().toList())

    /**
     * A rule that matches a character not in a set of characters.
     *
     * @param[characters] A collection of characters
     * @return A grammar rule
     */
    protected open fun noneOfChars(characters: Collection<Char>): Rule<T> = when {
        characters.isEmpty() -> anyCharRule
        else -> CharPredicateRule { characters.sorted().binarySearch(it) < 0 }
    }

    /**
     * A rule that matches a character not in a set of characters.
     *
     * @param[characters] A variable number of characters
     * @return A grammar rule
     */
    protected open fun noneOfChars(vararg characters: Char): Rule<T> = noneOfChars(characters.toList())

    /**
     * A rule that matches a character not in a a set of characters.
     *
     * @param[characters] A string containing the set of characters.
     * @return A grammar rule
     */
    protected open fun noneOfChars(characters: String): Rule<T> = noneOfChars(characters.toCharArray().toList())

    /**
     * A rule that matches a specific code point.
     *
     * @param[codePoint] The code point to match
     * @return A grammar rule
     */
    protected open fun codePoint(codePoint: Int): Rule<T> = codePoint.toRule()

    /**
     * A rule that matches a specific code point, ignoring the case of the code point (case-insensitive).
     *
     * @param[codePoint] The code point to match
     * @return A grammar rule
     */
    protected open fun ignoreCase(codePoint: Int): Rule<T> = codePoint.toIgnoreCaseRule()

    /**
     * A rule that matches a code point within a range of code points.
     *
     * @param[lowerBound] The lower bound of the code point range (inclusive)
     * @param[upperBound] The upper bound of the code point range (inclusive)
     * @return A grammar rule
     */
    protected open fun codePointRange(lowerBound: Int, upperBound: Int): Rule<T> {
        require(lowerBound <= upperBound) { "A 'lowerBound' must not be greater than an 'upperBound'." }
        return CodePointPredicateRule { it in lowerBound..upperBound }
    }

    /**
     * A rule that matches a code point within a set of code points.
     *
     * @param[codePoints] A collection of code points.
     * @return A grammar rule
     */
    protected open fun anyOfCodePoints(codePoints: Collection<Int>): Rule<T> = when {
        codePoints.isEmpty() -> neverRule
        codePoints.size == 1 -> codePoints.first().toRule()
        else -> CodePointPredicateRule { codePoints.sorted().binarySearch(it) >= 0 }
    }

    /**
     * A rule that matches a code point within a set of code points.
     *
     * @param[codePoints] A variable number of code points
     * @return A grammar rule
     */
    protected open fun anyOfCodePoints(vararg codePoints: Int): Rule<T> = anyOfCodePoints(codePoints.toList())

    /**
     * A rule that matches a code point within a set of code points.
     *
     * @param[codePoints] A string containing the set of code points.
     * @return A grammar rule
     */
    protected open fun anyOfCodePoints(codePoints: String): Rule<T> = anyOfCodePoints(codePoints.codePoints().toList())

    /**
     * A rule that matches a code point not in a set of code points.
     *
     * @param[codePoints] A collection of code points.
     * @return A grammar rule
     */
    protected open fun noneOfCodePoints(codePoints: Collection<Int>): Rule<T> = when {
        codePoints.isEmpty() -> anyCodePointRule
        else -> CodePointPredicateRule { codePoints.sorted().binarySearch(it) < 0 }
    }

    /**
     * A rule that matches a code point not in a set of code points.
     *
     * @param[codePoints] A variable number of code points
     * @return A grammar rule
     */
    protected open fun noneOfCodePoints(vararg codePoints: Int): Rule<T> = noneOfCodePoints(codePoints.toList())

    /**
     * A rule that matches a code point not in a set of code points.
     *
     * @param[codePoints] A string containing the set of code points.
     * @return A grammar rule
     */
    protected open fun noneOfCodePoints(codePoints: String): Rule<T> =
        noneOfCodePoints(codePoints.codePoints().toList())

    /**
     * A rule that matches a specific string.
     *
     * @param[string] The string to match
     * @return A grammar rule
     */
    protected open fun string(string: String): Rule<T> = when {
        string.isEmpty() -> emptyRule
        string.length == 1 -> string.first().toRule()
        string.codePoints().count() == 1L -> string.codePointAt(0).toRule()
        else -> StringRule(string)
    }

    /**
     * A rule that matches a specific string, ignoring the case of its characters (case-insensitive).
     *
     * @param[string] The string to match
     * @return A grammar rule
     */
    protected open fun ignoreCase(string: String): Rule<T> = when {
        string.isEmpty() -> emptyRule
        else -> IgnoreCaseTrieRule(string)
    }

    /**
     * A rule that matches a regular expression.
     *
     * @param[regex] A regular expression
     * @return A grammar rule
     */
    protected open fun regex(regex: String): Rule<T> = RegexRule(regex)

    /**
     * A rule that matches a string within a set of strings.
     *
     * @param[strings] A collection of strings
     * @return A grammar rule
     */
    protected open fun strings(strings: Collection<String>): Rule<T> = when {
        strings.isEmpty() -> neverRule
        strings.size == 1 -> strings.first().toRule()
        else -> TrieRule(strings)
    }

    /**
     * A rule that matches a string within a set of strings.
     *
     * @param[strings] A variable number of strings
     * @return A grammar rule
     */
    protected open fun strings(vararg strings: String): Rule<T> = strings(strings.toList())

    /**
     * A rule that matches a string within a set of strings, ignoring the case of their characters (case-insensitive).
     *
     * @param[strings] A collection of strings
     * @return A grammar rule
     */
    protected open fun ignoreCase(strings: Collection<String>): Rule<T> = when {
        strings.isEmpty() -> neverRule
        strings.size == 1 -> ignoreCase(strings.first())
        else -> IgnoreCaseTrieRule(strings)
    }

    /**
     * A rule that matches a string within a set of strings, ignoring the case of their characters (case-insensitive).
     *
     * @param[strings] A variable number of strings
     * @return A grammar rule
     */
    protected open fun ignoreCase(vararg strings: String): Rule<T> = ignoreCase(strings.toList())

    /**
     * A rule that matches an ASCII character.
     *
     * @return A grammar rule
     */
    protected open fun ascii(): Rule<T> = asciiRule

    /**
     * A rule that matches a characters of Unicode's Basic Multilingual Plane.
     *
     * @return A grammar rule
     */
    protected open fun bmp(): Rule<T> = bmpRule

    /**
     * A rule that matches a digit.
     *
     * @return A grammar rule
     */
    protected open fun digit(): Rule<T> = digitRule

    /**
     * A rule that matches a character which is valid to be the first character of a Java identifier.
     *
     * @return A grammar rule
     */
    protected open fun javaIdentifierStart(): Rule<T> = javaIdentStartRule

    /**
     * A rule that matches a character which is valid to be part character of a Java identifier, other than the first
     * character.
     *
     * @return A grammar rule
     */
    protected open fun javaIdentifierPart(): Rule<T> = javaIdentPartRule

    /**
     * A rule that matches a letter.
     *
     * @return A grammar rule
     */
    protected open fun letter(): Rule<T> = letterRule

    /**
     * A rule that matches a letter or a digit.
     *
     * @return A grammar rule
     */
    protected open fun letterOrDigit(): Rule<T> = letterOrDigitRule

    /**
     * A rule that matches a printable character.
     *
     * @return A grammar rule
     */
    protected open fun printable(): Rule<T> = printableRule

    /**
     * A rule that matches a space character.
     *
     * @return A grammar rule
     */
    protected open fun spaceChar(): Rule<T> = spaceCharRule

    /**
     * A rule that matches a whitespace character.
     *
     * @return A grammar rule
     */
    protected open fun whitespace(): Rule<T> = whitespaceRule

    /**
     * A rule that matches the carriage return character.
     *
     * @return A grammar rule
     */
    protected open fun cr(): Rule<T> = crRule

    /**
     * A rule that matches the line feed character.
     *
     * @return A grammar rule
     */
    protected open fun lf(): Rule<T> = lfRule

    /**
     * A rule that matches the carriage return and line feed characters.
     *
     * @return A grammar rule
     */
    protected open fun crlf(): Rule<T> = crlfRule

    /**
     * A rule that matches a sequence of other rules.
     *
     * @param[rules] A variable number of rules
     * @return A grammar rule
     */
    protected open fun sequence(vararg rules: Rule<T>): Rule<T> = sequence(rules.toList())

    /**
     * A rule that matches a sequence of other rules.
     *
     * @param[rules] A list of rules
     * @return A grammar rule
     */
    protected open fun sequence(rules: List<Rule<T>>): Rule<T> = when {
        rules.isEmpty() -> emptyRule
        rules.size == 1 -> rules.first()
        else -> SequenceRule(rules)
    }

    /**
     * A rule that matches the first successful rule in a list of other rules.
     *
     * @param[rules] A variable number of rules
     * @return A grammar rule
     */
    protected open fun firstOf(vararg rules: Rule<T>): Rule<T> = firstOf(rules.toList())

    /**
     * A rule that matches the first successful rule in a list of other rules.
     *
     * @param[rules] A list of rules
     * @return A grammar rule
     */
    protected open fun firstOf(rules: List<Rule<T>>): Rule<T> = when {
        rules.isEmpty() -> emptyRule
        rules.size == 1 -> rules.first()
        else -> FirstOfRule(rules)
    }

    /**
     * A rule that matches its child rule optionally.
     * In other words, a rule that repeats its child rule zero or one time.
     *
     * @param[rule] The child rule to match optionally
     * @return A grammar rule
     */
    protected open fun optional(rule: Rule<T>): Rule<T> = max(1) * rule

    /**
     * A rule that matches its child rule zero or more times.
     *
     * @param[rule] The child rule to repeat
     * @return A grammar rule
     */
    protected open fun zeroOrMore(rule: Rule<T>): Rule<T> = min(0) * rule

    /**
     * A rule that matches its child rule one or more times.
     *
     * @param[rule] The child rule to repeat
     * @return A grammar rule
     */
    protected open fun oneOrMore(rule: Rule<T>): Rule<T> = min(1) * rule

    /**
     * Repeat a rule exactly n times.
     *
     * @param[rule] The child rule to repeat
     * @param[n] The number of repetitions
     * @return A grammar rule
     */
    protected open fun repeat(rule: Rule<T>, n: Int): Rule<T> = n * rule

    /**
     * Repeat a rule between min and max times.
     *
     * @param[rule] The child rule to repeat
     * @param[min] The minimum number of repetitions
     * @param[max] The maximum number of repetitions, may be null (unbounded)
     * @return A grammar rule
     */
    protected open fun repeat(rule: Rule<T>, min: Int = 0, max: Int? = null): Rule<T> = range(min, max) * rule

    /**
     * A test rule that tests if its child rule matches.
     *
     * @param[rule] The child rule to test
     * @return A grammar rule
     */
    protected open fun test(rule: Rule<T>): Rule<T> = TestRule(rule)

    /**
     * A test rule that tests if its child rule does not match.
     *
     * @param[rule] The child rule to test
     * @return A grammar rule
     */
    protected open fun testNot(rule: Rule<T>): Rule<T> = TestNotRule(rule)

    /**
     * A conditional rule that runs a child rule if a condition is true, otherwise it runs another child rule.
     *
     * @param[condition] A condition
     * @param[thenRule] The rule to run if the condition is true
     * @param[elseRule] The rule to run if the condition is false
     * @return A grammar rule
     */
    protected open fun conditional(
        condition: (RuleContext<T>) -> Boolean,
        thenRule: Rule<T>,
        elseRule: Rule<T>
    ): Rule<T> = ConditionalRule(condition, thenRule, elseRule)

    /**
     * A conditional rule that runs a child rule if a condition is true, otherwise it runs no rule.
     *
     * @param[condition] A condition
     * @param[thenRule] The rule to run if the condition is true
     * @return A grammar rule
     */
    protected open fun conditional(condition: (RuleContext<T>) -> Boolean, thenRule: Rule<T>): Rule<T> =
        ConditionalRule(condition, thenRule)

    /**
     * A rule that runs an action.
     *
     * @param[action] The action to run
     * @return A grammar rule
     */
    protected open fun action(action: Action<T>): Rule<T> = ActionRule(action::run)

    /**
     * A rule that executes a command.
     *
     * @param[command] The command to execute
     * @return A grammar rule
     */
    protected open fun command(command: Command<T>): Rule<T> = action(command.toAction())

    /**
     * A rule that runs an action. The action is skipped if the rule is run inside a test rule.
     *
     * @param[action] The skippable action to run
     * @return A grammar rule
     */
    protected open fun skippableAction(action: Action<T>): Rule<T> = ActionRule(action::run, true)

    /**
     * A rule that executes a command. The command is skipped if the rule is run inside a test rule.
     *
     * @param[command] The command to execute
     * @return A grammar rule
     */
    protected open fun skippableCommand(command: Command<T>): Rule<T> = skippableAction(command.toAction())

    /**
     * Post a static event to the parser's event bus.
     * Note that the event object is constructed at grammar create time (thus static).
     *
     * @param[event] The event to post
     * @return A grammar rule
     */
    protected open fun post(event: Any): Rule<T> = skippableCommand { it.bus.post(event) }

    /**
     * Post a dynamic event to the parser's event bus.
     * Note that the event object is supplied by an event supplier at parser runtime
     * which has access to the parser context (thus dynamic).
     *
     * @param[supplier] An event supplier that is called when the rule is run
     * @return A grammar rule
     */
    protected open fun post(supplier: (RuleContext<T>) -> Any): Rule<T> = skippableCommand { it.bus.post(supplier(it)) }

    /**
     * Pop the top level element from the stack.
     *
     * @return A grammar rule
     */
    protected open fun pop(): Rule<T> = command { it.stack.pop() }

    /**
     * Pop an element from the stack at a given position.
     *
     * @param[down] The number of elements on the stack to skip
     * @return A grammar rule
     */
    protected open fun pop(down: Int): Rule<T> = command { it.stack.pop(down) }

    /**
     * Replace the element on top of the stack.
     * Note that the replacement value is constructed at grammar create time.
     *
     * @param[value] A replacement value
     * @return A grammar rule
     */
    protected open fun poke(value: T): Rule<T> = command { it.stack.poke(value) }

    /**
     * Replace an element in the stack at a given position.
     * Note that the replacement value is constructed at grammar create time.
     *
     * @param[down] The number of elements on the stack to skip
     * @param[value] A replacement value
     * @return A grammar rule
     */
    protected open fun poke(down: Int, value: T): Rule<T> = command { it.stack.poke(down, value) }

    /**
     * Replace an element in the stack.
     * Note that the replacement value is supplied by a value supplier at parser runtime
     * which has access to the parser context.
     *
     * @param[supplier] A replacement value supplier
     * @return A grammar rule
     */
    protected open fun poke(supplier: (RuleContext<T>) -> T): Rule<T> = command { it.stack.poke(supplier(it)) }

    /**
     * Replace an element in the stack at a given position.
     * Note that the replacement value is supplied by a value supplier at parser runtime
     * which has access to the parser context.
     *
     * @param[down] The number of elements on the stack to skip
     * @param[supplier] A replacement value supplier
     * @return A grammar rule
     */
    protected open fun poke(down: Int, supplier: (RuleContext<T>) -> T): Rule<T> =
        command { it.stack.poke(down, supplier(it)) }

    /**
     * Push a new element onto stack. Note that the value is constructed at grammar create time.
     *
     * @param[value] A value
     * @return A grammar rule
     */
    protected open fun push(value: T): Rule<T> = command { it.stack.push(value) }

    /**
     * Push a new element onto stack.
     * Note that the value is supplied by a value supplier at parser runtime which has access to the parser context.
     *
     * @param[supplier] A value supplier
     * @return A grammar rule
     */
    protected open fun push(supplier: (RuleContext<T>) -> T): Rule<T> = command { it.stack.push(supplier(it)) }

    /**
     * Duplicate the top stack element.
     *
     * @return A grammar rule
     */
    protected open fun dup(): Rule<T> = command { it.stack.dup() }

    /**
     * Swap the two top stack elements.
     *
     * @return A grammar rule
     */
    protected open fun swap(): Rule<T> = command { it.stack.swap() }

    /**
     * Pop the top level element from the stack.
     * This function may be called by an action or command where the rule context is available.
     *
     * @param[context] An action context
     * @return The popped element
     */
    protected fun pop(context: RuleContext<T>): T = context.stack.pop()

    /**
     * Pop an element from the stack at a given position.
     * This function may be called by an action or command where the rule context is available.
     *
     * @param[down] The number of elements on the stack to skip
     * @param[context] An action context
     * @return The popped element
     */
    protected fun pop(down: Int, context: RuleContext<T>): T = context.stack.pop(down)

    /**
     * Replace the element on top of the stack.
     * This function may be called by an action or command where the rule context is available.
     *
     * @param[value] A replacement value
     * @return The replaced element
     */
    protected fun poke(context: RuleContext<T>, value: T): T = context.stack.poke(value)

    /**
     * Replace an element on the stack at a given position.
     * This function may be called by an action or command where the rule context is available.
     *
     * @param[down] The number of elements on the stack to skip
     * @param[value] A replacement value
     * @return The replaced element
     */
    protected fun poke(down: Int, context: RuleContext<T>, value: T): T = context.stack.poke(down, value)

    /**
     * Peek the top level element from the stack.
     * This function may be called by an action or command where the rule context is available.
     *
     * @param[context] An action context
     * @return The peeked element
     */
    protected fun peek(context: RuleContext<T>): T = context.stack.peek()

    /**
     * Peek an element from the stack at a given position.
     * This function may be called by an action or command where the rule is available.
     *
     * @param[down] The number of elements on the stack to skip
     * @param[context] An action context
     * @return The peeked element
     */
    protected fun peek(down: Int, context: RuleContext<T>): T = context.stack.peek(down)
}

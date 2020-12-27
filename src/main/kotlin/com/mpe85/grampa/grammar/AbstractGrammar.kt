package com.mpe85.grampa.grammar

import com.ibm.icu.lang.UCharacter
import com.mpe85.grampa.builder.RepeatRuleBuilder
import com.mpe85.grampa.rule.Action
import com.mpe85.grampa.rule.ActionContext
import com.mpe85.grampa.rule.Command
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.ActionRule
import com.mpe85.grampa.rule.impl.CharPredicateRule
import com.mpe85.grampa.rule.impl.CodePointPredicateRule
import com.mpe85.grampa.rule.impl.ConditionalRule
import com.mpe85.grampa.rule.impl.EmptyRule
import com.mpe85.grampa.rule.impl.EndOfInputRule
import com.mpe85.grampa.rule.impl.FirstOfRule
import com.mpe85.grampa.rule.impl.NeverRule
import com.mpe85.grampa.rule.impl.RegexRule
import com.mpe85.grampa.rule.impl.SequenceRule
import com.mpe85.grampa.rule.impl.StringRule
import com.mpe85.grampa.rule.impl.TestNotRule
import com.mpe85.grampa.rule.impl.TestRule
import com.mpe85.grampa.rule.impl.TrieRule
import com.mpe85.grampa.rule.toAction
import kotlin.streams.toList
import kotlin.text.Charsets.US_ASCII

/**
 * An abstract grammar that defines a bunch of useful grammar rules and actions.
 * A concrete grammar class should usually extend this class.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
abstract class AbstractGrammar<T> : Grammar<T> {

  private val asciiEncoder = US_ASCII.newEncoder()

  private val emptyRule = EmptyRule<T>()
  private val neverRule = NeverRule<T>()
  private val eoiRule = EndOfInputRule<T>()
  private val anyCharRule = CharPredicateRule<T> { true }
  private val anyCodePointRule = CodePointPredicateRule<T>(UCharacter::isLegal)
  private val asciiRule = CharPredicateRule<T>(asciiEncoder::canEncode)
  private val bmpRule = CodePointPredicateRule<T>(UCharacter::isBMP)
  private val digitRule = CodePointPredicateRule<T>(UCharacter::isDigit)
  private val javaIdentStartRule = CodePointPredicateRule<T>(Character::isJavaIdentifierStart)
  private val javaIdentPartRule = CodePointPredicateRule<T>(Character::isJavaIdentifierPart)
  private val letterRule = CodePointPredicateRule<T>(UCharacter::isLetter)
  private val letterOrDigitRule = CodePointPredicateRule<T>(UCharacter::isLetterOrDigit)
  private val printableRule = CodePointPredicateRule<T>(UCharacter::isPrintable)
  private val spaceCharRule = CodePointPredicateRule<T>(UCharacter::isSpaceChar)
  private val whitespaceRule = CodePointPredicateRule<T>(UCharacter::isWhitespace)
  private val crRule = CharPredicateRule<T>('\r')
  private val lfRule = CharPredicateRule<T>('\n')
  private val crlfRule = StringRule<T>("\r\n")

  /**
   * A rule that matches an empty string. Or in other words, a rule that matches no input character and always
   * succeeds.
   *
   * @return A grammar rule
   */
  protected open fun empty() = emptyRule

  /**
   * A rule that always fails.
   *
   * @return A grammar rule
   */
  protected open fun never() = neverRule

  /**
   * A rule that matches the end of the input.
   *
   * @return A grammar rule
   */
  protected open fun eoi() = eoiRule

  /**
   * A rule that matches any character.
   *
   * @return A grammar rule
   */
  protected open fun anyChar() = anyCharRule

  /**
   * A rule that matches any code point.
   *
   * @return A grammar rule
   */
  protected open fun anyCodePoint() = anyCodePointRule

  /**
   * A rule that matches a specific character.
   *
   * @param[character] The character to match
   * @return A grammar rule
   */
  protected open fun character(character: Char) = CharPredicateRule<T> { it == character }

  /**
   * A rule that matches a specific character, ignoring the case of the character (case-insensitive).
   *
   * @param[character] The character to match
   * @return A grammar rule
   */
  protected open fun ignoreCase(character: Char) =
    CharPredicateRule<T> { Character.toLowerCase(it) == Character.toLowerCase(character) }


  /**
   * A rule that matches a character within a range of characters.
   *
   * @param[lowerBound] The lower bound of the character range (inclusive)
   * @param[upperBound] The upper bound of the character range (inclusive)
   * @return A grammar rule
   */
  protected open fun charRange(lowerBound: Char, upperBound: Char): Rule<T> {
    require(lowerBound <= upperBound) { "A 'lowerBound' must not be greater than an 'upperBound'." }
    return CharPredicateRule { it in lowerBound..upperBound }
  }

  /**
   * A rule that matches a character within a set of characters.
   *
   * @param[characters] A collection of characters
   * @return A grammar rule
   */
  protected open fun anyOfChars(characters: Collection<Char>) = when {
    characters.isEmpty() -> neverRule
    characters.size == 1 -> character(characters.first())
    else -> CharPredicateRule { characters.sorted().binarySearch(it) >= 0 }
  }

  /**
   * A rule that matches a character within a set of characters.
   *
   * @param[characters] A variable number of characters
   * @return A grammar rule
   */
  protected open fun anyOfChars(vararg characters: Char) = anyOfChars(characters.toList())

  /**
   * A rule that matches a character within a set of characters.
   *
   * @param[characters] A string containing the set of characters.
   * @return A grammar rule
   */
  protected open fun anyOfChars(characters: String) = anyOfChars(characters.toCharArray().toList())

  /**
   * A rule that matches a character not in a set of characters.
   *
   * @param[characters] A collection of characters
   * @return A grammar rule
   */
  protected open fun noneOfChars(characters: Collection<Char>) = when {
    characters.isEmpty() -> anyCharRule
    else -> CharPredicateRule { characters.sorted().binarySearch(it) < 0 }
  }

  /**
   * A rule that matches a character not in a set of characters.
   *
   * @param[characters] A variable number of characters
   * @return A grammar rule
   */
  protected open fun noneOfChars(vararg characters: Char) = noneOfChars(characters.toList())

  /**
   * A rule that matches a character not in a a set of characters.
   *
   * @param[characters] A string containing the set of characters.
   * @return A grammar rule
   */
  protected open fun noneOfChars(characters: String) = noneOfChars(characters.toCharArray().toList())

  /**
   * A rule that matches a specific code point.
   *
   * @param[codePoint] The code point to match
   * @return A grammar rule
   */
  protected open fun codePoint(codePoint: Int) = CodePointPredicateRule<T> { it == codePoint }

  /**
   * A rule that matches a specific code point, ignoring the case of the code point (case-insensitive).
   *
   * @param[codePoint] The code point to match
   * @return A grammar rule
   */
  protected open fun ignoreCase(codePoint: Int) =
    CodePointPredicateRule<T> { UCharacter.toLowerCase(it) == UCharacter.toLowerCase(codePoint) }

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
  protected open fun anyOfCodePoints(codePoints: Collection<Int>) = when {
    codePoints.isEmpty() -> neverRule
    codePoints.size == 1 -> codePoint(codePoints.first())
    else -> CodePointPredicateRule { codePoints.sorted().binarySearch(it) >= 0 }
  }

  /**
   * A rule that matches a code point within a set of code points.
   *
   * @param[codePoints] A variable number of code points
   * @return A grammar rule
   */
  protected open fun anyOfCodePoints(vararg codePoints: Int) = anyOfCodePoints(codePoints.toList())

  /**
   * A rule that matches a code point within a set of code points.
   *
   * @param[codePoints] A string containing the set of code points.
   * @return A grammar rule
   */
  protected open fun anyOfCodePoints(codePoints: String) = anyOfCodePoints(codePoints.codePoints().toList())

  /**
   * A rule that matches a code point not in a set of code points.
   *
   * @param[codePoints] A collection of code points.
   * @return A grammar rule
   */
  protected open fun noneOfCodePoints(codePoints: Collection<Int>) = when {
    codePoints.isEmpty() -> anyCodePointRule
    else -> CodePointPredicateRule { codePoints.sorted().binarySearch(it) < 0 }
  }

  /**
   * A rule that matches a code point not in a set of code points.
   *
   * @param[codePoints] A variable number of code points
   * @return A grammar rule
   */
  protected open fun noneOfCodePoints(vararg codePoints: Int) = noneOfCodePoints(codePoints.toList())

  /**
   * A rule that matches a code point not in a set of code points.
   *
   * @param[codePoints] A string containing the set of code points.
   * @return A grammar rule
   */
  protected open fun noneOfCodePoints(codePoints: String) = noneOfCodePoints(codePoints.codePoints().toList())

  /**
   * A rule that matches a specific string.
   *
   * @param[string] The string to match
   * @return A grammar rule
   */
  protected open fun string(string: String) = when {
    string.isEmpty() -> emptyRule
    string.length == 1 -> character(string.first())
    else -> StringRule(string)
  }

  /**
   * A rule that matches a specific string, ignoring the case of its characters (case-insensitive).
   *
   * @param[string] The string to match
   * @return A grammar rule
   */
  protected open fun ignoreCase(string: String) = when {
    string.isEmpty() -> emptyRule
    string.length == 1 -> ignoreCase(string.first())
    else -> StringRule(string, true)
  }

  /**
   * A rule that matches a regular expression
   *
   * @param[regex] A regular expression
   * @return A grammar rule
   */
  protected open fun regex(regex: String) = RegexRule<T>(regex)

  /**
   * A rule that matches a string within a set of strings.
   *
   * @param[strings] A collection of strings
   * @return A grammar rule
   */
  protected open fun strings(strings: Collection<String>) = when {
    strings.isEmpty() -> neverRule
    strings.size == 1 -> string(strings.first())
    else -> TrieRule(strings)
  }

  /**
   * A rule that matches a string within a set of strings.
   *
   * @param[strings] A variable number of strings
   * @return A grammar rule
   */
  protected open fun strings(vararg strings: String) = strings(strings.toList())

  /**
   * A rule that matches a string within a set of strings, ignoring the case of their characters (case-insensitive).
   *
   * @param[strings] A collection of strings
   * @return A grammar rule
   */
  protected open fun ignoreCase(strings: Collection<String>) = when {
    strings.isEmpty() -> neverRule
    strings.size == 1 -> ignoreCase(strings.first())
    else -> TrieRule(strings, true)
  }

  /**
   * A rule that matches a string within a set of strings, ignoring the case of their characters (case-insensitive).
   *
   * @param[strings] A variable number of strings
   * @return A grammar rule
   */
  protected open fun ignoreCase(vararg strings: String) = ignoreCase(strings.toList())

  /**
   * A rule that matches an ASCII character.
   *
   * @return A grammar rule
   */
  protected open fun ascii() = asciiRule

  /**
   * A rule that matches a characters of Unicode's Basic Multilingual Plane.
   *
   * @return A grammar rule
   */
  protected open fun bmp() = bmpRule

  /**
   * A rule that matches a digit.
   *
   * @return A grammar rule
   */
  protected open fun digit() = digitRule

  /**
   * A rule that matches a character which is valid to be the first character of a java identifier.
   *
   * @return A grammar rule
   */
  protected open fun javaIdentifierStart() = javaIdentStartRule

  /**
   * A rule that matches a character which is valid to be part character of a java identifier, other than the first
   * character.
   *
   * @return A grammar rule
   */
  protected open fun javaIdentifierPart() = javaIdentPartRule

  /**
   * A rule that matches a letter.
   *
   * @return A grammar rule
   */
  protected open fun letter() = letterRule

  /**
   * A rule that matches a letter or a digit.
   *
   * @return A grammar rule
   */
  protected open fun letterOrDigit() = letterOrDigitRule

  /**
   * A rule that matches a printable character.
   *
   * @return A grammar rule
   */
  protected open fun printable() = printableRule

  /**
   * A rule that matches a space character.
   *
   * @return A grammar rule
   */
  protected open fun spaceChar() = spaceCharRule

  /**
   * A rule that matches a whitespace character.
   *
   * @return A grammar rule
   */
  protected open fun whitespace() = whitespaceRule

  /**
   * A rule that matches the carriage return character.
   *
   * @return A grammar rule
   */
  protected open fun cr() = crRule

  /**
   * A rule that matches the line feed character.
   *
   * @return A grammar rule
   */
  protected open fun lf() = lfRule

  /**
   * A rule that matches the carriage return and line feed characters.
   *
   * @return A grammar rule
   */
  protected open fun crlf() = crlfRule

  /**
   * A rule that matches a sequence of rules.
   *
   * @param[rules] A variable number of rules
   * @return A grammar rule
   */
  protected open fun sequence(vararg rules: Rule<T>) = sequence(rules.toList())

  /**
   * A rule that matches a sequence of rules.
   *
   * @param[rules] A list of rules
   * @return A grammar rule
   */
  protected open fun sequence(rules: List<Rule<T>>) = when {
    rules.isEmpty() -> emptyRule
    rules.size == 1 -> rules.first()
    else -> SequenceRule(rules)
  }

  /**
   * A rule that matches the first successful rule in a list of rules.
   *
   * @param[rules] A variable number of rules
   * @return A grammar rule
   */
  protected open fun firstOf(vararg rules: Rule<T>) = firstOf(rules.toList())

  /**
   * A rule that matches the first successful rule in a list of rules.
   *
   * @param[rules] A list of rules
   * @return A grammar rule
   */
  protected open fun firstOf(rules: List<Rule<T>>) = when {
    rules.isEmpty() -> emptyRule
    rules.size == 1 -> rules.first()
    else -> FirstOfRule(rules)
  }

  /**
   * A rule that matches its sub rule optionally. In other words, a rule that repeats its sub rule zero or one time.
   *
   * @param[rule] The sub rule to match optionally
   * @return A grammar rule
   */
  protected open fun optional(rule: Rule<T>) = repeat(rule).times(0, 1)

  /**
   * A rule that matches its sub rule zero or more times.
   *
   * @param[rule] The sub rule to repeat
   * @return A grammar rule
   */
  protected open fun zeroOrMore(rule: Rule<T>) = repeat(rule).min(0)

  /**
   * A rule that matches its sub rule one or more times.
   *
   * @param[rule] The sub rule to repeat
   * @return A grammar rule
   */
  protected open fun oneOrMore(rule: Rule<T>) = repeat(rule).min(1)

  /**
   * A rule builder for a repeat rule.
   *
   * @param[rule] The sub rule to repeat
   * @return a repeat rule builder
   */
  protected open fun repeat(rule: Rule<T>) = RepeatRuleBuilder(rule)

  /**
   * A predicate rule that tests if its sub rule matches.
   *
   * @param[rule] The sub rule to test
   * @return A grammar rule
   */
  protected open fun test(rule: Rule<T>) = TestRule(rule)

  /**
   * A predicate rule that tests if its sub rule does not match.
   *
   * @param[rule] The sub rule to test
   * @return A grammar rule
   */
  protected open fun testNot(rule: Rule<T>) = TestNotRule(rule)

  /**
   * A conditional rule that runs one rule if a condition is true, otherwise it runs another rule.
   *
   * @param[condition] A condition
   * @param[thenRule] The rule to run if the condition is true
   * @param[elseRule] The rule to run if the condition is false
   * @return A grammar rule
   */
  protected open fun conditional(condition: (ActionContext<T>) -> Boolean, thenRule: Rule<T>, elseRule: Rule<T>) =
    ConditionalRule(condition, thenRule, elseRule)

  /**
   * A conditional rule that runs a rule if a condition is true, otherwise it runs no rule.
   *
   * @param[condition] A condition
   * @param[thenRule] The rule to run if the condition is true
   * @return A grammar rule
   */
  protected open fun conditional(condition: (ActionContext<T>) -> Boolean, thenRule: Rule<T>) =
    ConditionalRule(condition, thenRule)

  /**
   * A rule that runs an action.
   *
   * @param[action] The action to run
   * @return A grammar rule
   */
  protected open fun action(action: Action<T>) = ActionRule(action::run)

  /**
   * A rule that executes a command.
   *
   * @param[command] The command to execute
   * @return A grammar rule
   */
  protected open fun command(command: Command<T>) = action(command.toAction())

  /**
   * A rule that runs an action. The action is skipped if the rule is run inside a predicate rule.
   *
   * @param[action] The skippable action to run
   * @return A grammar rule
   */
  protected open fun skippableAction(action: Action<T>) = ActionRule(action::run, true)

  /**
   * A rule that executes a command. The command is skipped if the rule is run inside a predicate rule.
   *
   * @param[command] The command to execute
   * @return A grammar rule
   */
  protected open fun skippableCommand(command: Command<T>) = skippableAction(command.toAction())

  /**
   * Post an event to the parser's event bus. Note that the event object is constructed at grammar create time.
   *
   * @param[event] The event to post
   * @return A grammar rule
   */
  protected open fun post(event: Any) = skippableCommand { ctx -> ctx.post(event) }

  /**
   * Post an event to the parser's event bus. Note that the event object is supplied by an event supplier at parser
   * runtime which has access to the parser context.
   *
   * @param[supplier] An event supplier that is called when the rule is run
   * @return A grammar rule
   */
  protected open fun post(supplier: (ActionContext<T>) -> Any) = skippableCommand { ctx -> ctx.post(supplier(ctx)) }

  /**
   * Pop the top level element from the stack.
   *
   * @return A grammar rule
   */
  protected open fun pop() = action { ctx ->
    ctx.stack.pop()
    true
  }

  /**
   * Pop an element from the stack at a given position.
   *
   * @param[down] The number of elements on the stack to skip
   * @return A grammar rule
   */
  protected open fun pop(down: Int) = action { ctx ->
    ctx.stack.pop(down)
    true
  }

  /**
   * Replace the element on top of the stack. Note that the value is constructed at grammar create time.
   *
   * @param[value] A replacement value
   * @return A grammar rule
   */
  protected open fun poke(value: T) = action { ctx ->
    ctx.stack.poke(value)
    true
  }

  /**
   * Replace an element in the stack at a given position. Note that the value is constructed at grammar create time.
   *
   * @param[down] The number of elements on the stack to skip
   * @param[value] A replacement value
   * @return A grammar rule
   */
  protected open fun poke(down: Int, value: T) = action { ctx ->
    ctx.stack.poke(down, value)
    true
  }

  /**
   * Replace an element in the stack. Note that the value is supplied by a value supplier at parser runtime which
   * has access to the parser context.
   *
   * @param[supplier] A replacement value supplier
   * @return A grammar rule
   */
  protected open fun poke(supplier: (ActionContext<T>) -> T) = action { ctx ->
    ctx.stack.poke(supplier(ctx))
    true
  }

  /**
   * Replace an element in the stack at a given position. Note that the value is supplied by a value supplier at
   * parser runtime which has access to the parser context.
   *
   * @param[down] The number of elements on the stack to skip
   * @param[supplier] A replacement value supplier
   * @return A grammar rule
   */
  protected open fun poke(down: Int, supplier: (ActionContext<T>) -> T) = action { ctx ->
    ctx.stack.poke(down, supplier(ctx))
    true
  }

  /**
   * Push a new element onto stack. Note that the value is constructed at grammar create time.
   *
   * @param[value] A value
   * @return A grammar rule
   */
  protected open fun push(value: T) = command { ctx -> ctx.stack.push(value) }

  /**
   * Push a new element onto stack. Note that the value is supplied by a value supplier at parser runtime which has
   * access to the parser context.
   *
   * @param[supplier] A value supplier
   * @return A grammar rule
   */
  protected open fun push(supplier: (ActionContext<T>) -> T) = command { ctx -> ctx.stack.push(supplier(ctx)) }

  /**
   * Duplicate the top stack element.
   *
   * @return A grammar rule
   */
  protected open fun dup() = command { ctx -> ctx.stack.dup() }

  /**
   * Swap the two top stack elements.
   *
   * @return A grammar rule
   */
  protected open fun swap() = command { ctx -> ctx.stack.swap() }

  /**
   * Pop the top level element from the stack. This method may be called by an action or command where the action
   * context is available.
   *
   * @param[context] An action context
   * @return The popped element
   */
  protected fun pop(context: ActionContext<T>): T = context.stack.pop()

  /**
   * Pop an element from the stack at a given position. This method may be called by an action or command where the
   * action
   *
   * @param[down] The number of elements on the stack to skip
   * @param[context] An action context
   * @return The popped element
   */
  protected fun pop(down: Int, context: ActionContext<T>) = context.stack.pop(down)

  /**
   * Peek the top level element from the stack.
   * This method may be called by an action or command where the action context is available.
   *
   * @param[context] An action context
   * @return The peeked element
   */
  protected fun peek(context: ActionContext<T>): T = context.stack.peek()

  /**
   * Peek an element from the stack at a given position.
   * This method may be called by an action or command where the action is available.
   *
   * @param[down] The number of elements on the stack to skip
   * @param[context] An action context
   * @return The peeked element
   */
  protected fun peek(down: Int, context: ActionContext<T>) = context.stack.peek(down)

}

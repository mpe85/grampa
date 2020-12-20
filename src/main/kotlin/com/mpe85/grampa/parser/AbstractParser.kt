package com.mpe85.grampa.parser

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
import java.util.Arrays.binarySearch
import java.util.Arrays.sort
import kotlin.text.Charsets.US_ASCII

/**
 * An abstract parser that defines a bunch of useful parser rules and actions. A concrete parser class should usually
 * extend this class.
 *
 * @author mpe85
 * @param T the type of the stack elements
 */
abstract class AbstractParser<T> : Parser<T> {

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
   * @return a rule
   */
  protected open fun empty() = emptyRule

  /**
   * A rule that always fails.
   *
   * @return a rule
   */
  protected open fun never() = neverRule

  /**
   * A rule that matches the end of the input.
   *
   * @return a rule
   */
  protected open fun eoi() = eoiRule

  /**
   * A rule that matches any character.
   *
   * @return a rule
   */
  protected open fun anyChar() = anyCharRule

  /**
   * A rule that matches any code point.
   *
   * @return a rule
   */
  protected open fun anyCodePoint() = anyCodePointRule

  /**
   * A rule that matches a specific character.
   *
   * @param character the character to match
   * @return a rule
   */
  protected open fun character(character: Char) = CharPredicateRule<T> { it == character }

  /**
   * A rule that matches a specific character, ignoring the case of the character (case-insensitive).
   *
   * @param character the character to match
   * @return a rule
   */
  protected open fun ignoreCase(character: Char) =
    CharPredicateRule<T> { Character.toLowerCase(it) == Character.toLowerCase(character) }


  /**
   * A rule that matches a character within a range of characters.
   *
   * @param lowerBound the lower bound of the character range (inclusive)
   * @param upperBound the upper bound of the character range (inclusive)
   * @return a rule
   */
  protected open fun charRange(lowerBound: Char, upperBound: Char): Rule<T> {
    require(lowerBound <= upperBound) { "A 'lowerBound' must not be greater than an 'upperBound'." }
    return CharPredicateRule { it in lowerBound..upperBound }
  }

  /**
   * A rule that matches a character within a set of characters.
   *
   * @param characters a variable number of characters
   * @return a rule
   */
  protected open fun anyOfChars(vararg characters: Char) = when {
    characters.isEmpty() -> neverRule
    characters.size == 1 -> character(characters.first())
    else -> {
      sort(characters)
      CharPredicateRule { binarySearch(characters, it) >= 0 }
    }
  }

  /**
   * A rule that matches a character within a set of characters.
   *
   * @param characters a set of characters
   * @return a rule
   */
  protected open fun anyOfChars(characters: Set<Char>) = anyOfChars(*characters.toCharArray())

  /**
   * A rule that matches a character within a set of characters.
   *
   * @param characters a string containing the set of characters.
   * @return a rule
   */
  protected open fun anyOfChars(characters: String) = anyOfChars(*characters.toCharArray())

  /**
   * A rule that matches a character not in a set of characters.
   *
   * @param characters a variable number of characters
   * @return a rule
   */
  protected open fun noneOfChars(vararg characters: Char) = when {
    characters.isEmpty() -> anyCharRule
    else -> {
      sort(characters)
      CharPredicateRule { binarySearch(characters, it) < 0 }
    }
  }

  /**
   * A rule that matches a character not in a set of characters.
   *
   * @param characters a set of characters
   * @return a rule
   */
  protected open fun noneOfChars(characters: Set<Char>) = noneOfChars(*characters.toCharArray())

  /**
   * A rule that matches a character not in a a set of characters.
   *
   * @param characters a string containing the set of characters.
   * @return a rule
   */
  protected open fun noneOfChars(characters: String) = noneOfChars(*characters.toCharArray())

  /**
   * A rule that matches a specific code point.
   *
   * @param codePoint the code point to match
   * @return a rule
   */
  protected open fun codePoint(codePoint: Int) = CodePointPredicateRule<T> { it == codePoint }

  /**
   * A rule that matches a specific code point, ignoring the case of the code point (case-insensitive).
   *
   * @param codePoint the code point to match
   * @return a rule
   */
  protected open fun ignoreCase(codePoint: Int) =
    CodePointPredicateRule<T> { UCharacter.toLowerCase(it) == UCharacter.toLowerCase(codePoint) }

  /**
   * A rule that matches a code point within a range of code points.
   *
   * @param lowerBound the lower bound of the code point range (inclusive)
   * @param upperBound the upper bound of the code point range (inclusive)
   * @return a rule
   */
  protected open fun codePointRange(lowerBound: Int, upperBound: Int): Rule<T> {
    require(lowerBound <= upperBound) { "A 'lowerBound' must not be greater than an 'upperBound'." }
    return CodePointPredicateRule { it in lowerBound..upperBound }
  }

  /**
   * A rule that matches a code point within a set of code points.
   *
   * @param codePoints a variable number of code points
   * @return a rule
   */
  protected open fun anyOfCodePoints(vararg codePoints: Int) = when {
    codePoints.isEmpty() -> neverRule
    codePoints.size == 1 -> codePoint(codePoints.first())
    else -> {
      sort(codePoints)
      CodePointPredicateRule { binarySearch(codePoints, it) >= 0 }
    }
  }

  /**
   * A rule that matches a code point within a set of code points.
   *
   * @param codePoints a set of code points.
   * @return a rule
   */
  protected open fun anyOfCodePoints(codePoints: Set<Int>) = anyOfCodePoints(*codePoints.toIntArray())

  /**
   * A rule that matches a code point within a set of code points.
   *
   * @param codePoints a string containing the set of code points.
   * @return a rule
   */
  protected open fun anyOfCodePoints(codePoints: String) = anyOfCodePoints(*codePoints.codePoints().toArray())

  /**
   * A rule that matches a code point not in a set of code points.
   *
   * @param codePoints a variable number of code points
   * @return a rule
   */
  protected open fun noneOfCodePoints(vararg codePoints: Int) = when {
    codePoints.isEmpty() -> anyCodePointRule
    else -> {
      sort(codePoints)
      CodePointPredicateRule { binarySearch(codePoints, it) < 0 }
    }
  }

  /**
   * A rule that matches a code point not in a set of code points.
   *
   * @param codePoints a set of code points.
   * @return a rule
   */
  protected open fun noneOfCodePoints(codePoints: Set<Int>) = noneOfCodePoints(*codePoints.toIntArray())

  /**
   * A rule that matches a code point not in a set of code points.
   *
   * @param codePoints a string containing the set of code points.
   * @return a rule
   */
  protected open fun noneOfCodePoints(codePoints: String) = noneOfCodePoints(*codePoints.codePoints().toArray())

  /**
   * A rule that matches a specific string.
   *
   * @param string the string to match
   * @return a rule
   */
  protected open fun string(string: String) = when {
    string.isEmpty() -> emptyRule
    string.length == 1 -> character(string.first())
    else -> StringRule(string)
  }

  /**
   * A rule that matches a specific string, ignoring the case of its characters (case-insensitive).
   *
   * @param string the string to match
   * @return a rule
   */
  protected open fun ignoreCase(string: String) = when {
    string.isEmpty() -> emptyRule
    string.length == 1 -> ignoreCase(string.first())
    else -> StringRule(string, true)
  }

  /**
   * A rule that matches a regular expression
   *
   * @param regex a regular expression
   * @return a rule
   */
  protected open fun regex(regex: String) = RegexRule<T>(regex)

  /**
   * A rule that matches a string within a set of strings.
   *
   * @param strings a variable number of strings
   * @return a rule
   */
  protected open fun strings(vararg strings: String) = strings(strings.toSet())

  /**
   * A rule that matches a string within a set of strings.
   *
   * @param strings a set of strings
   * @return a rule
   */
  protected open fun strings(strings: Set<String>) = when {
    strings.isEmpty() -> neverRule
    strings.size == 1 -> string(strings.first())
    else -> TrieRule(strings)
  }

  /**
   * A rule that matches a string within a set of strings, ignoring the case of their characters (case-insensitive).
   *
   * @param strings a variable number of strings
   * @return a rule
   */
  protected open fun ignoreCase(vararg strings: String) = ignoreCase(strings.toSet())

  /**
   * A rule that matches a string within a set of strings, ignoring the case of their characters (case-insensitive).
   *
   * @param strings a set of strings
   * @return a rule
   */
  protected open fun ignoreCase(strings: Set<String>) = when {
    strings.isEmpty() -> neverRule
    strings.size == 1 -> ignoreCase(strings.first())
    else -> TrieRule(strings, true)
  }

  /**
   * A rule that matches an ASCII character.
   *
   * @return a rule
   */
  protected open fun ascii() = asciiRule

  /**
   * A rule that matches a characters of Unicode's Basic Multilingual Plane.
   *
   * @return a rule
   */
  protected open fun bmp() = bmpRule

  /**
   * A rule that matches a digit.
   *
   * @return a rule
   */
  protected open fun digit() = digitRule

  /**
   * A rule that matches a character which is valid to be the first character of a java identifier.
   *
   * @return a rule
   */
  protected open fun javaIdentifierStart() = javaIdentStartRule

  /**
   * A rule that matches a character which is valid to be part character of a java identifier, other than the first
   * character.
   *
   * @return a rule
   */
  protected open fun javaIdentifierPart() = javaIdentPartRule

  /**
   * A rule that matches a letter.
   *
   * @return a rule
   */
  protected open fun letter() = letterRule

  /**
   * A rule that matches a letter or a digit.
   *
   * @return a rule
   */
  protected open fun letterOrDigit() = letterOrDigitRule

  /**
   * A rule that matches a printable character.
   *
   * @return a rule
   */
  protected open fun printable() = printableRule

  /**
   * A rule that matches a space character.
   *
   * @return a rule
   */
  protected open fun spaceChar() = spaceCharRule

  /**
   * A rule that matches a whitespace character.
   *
   * @return a rule
   */
  protected open fun whitespace() = whitespaceRule

  /**
   * A rule that matches the carriage return character.
   *
   * @return a rule
   */
  protected open fun cr() = crRule

  /**
   * A rule that matches the line feed character.
   *
   * @return a rule
   */
  protected open fun lf() = lfRule

  /**
   * A rule that matches the carriage return and line feed characters.
   *
   * @return a rule
   */
  protected open fun crlf() = crlfRule

  /**
   * A rule that matches a sequence of rules.
   *
   * @param rules a variable number of rules
   * @return a rule
   */
  @SafeVarargs
  protected open fun sequence(vararg rules: Rule<T>) = sequence(rules.toList())

  /**
   * A rule that matches a sequence of rules.
   *
   * @param rules a list of rules
   * @return a rule
   */
  protected open fun sequence(rules: List<Rule<T>>) = when {
    rules.isEmpty() -> emptyRule
    rules.size == 1 -> rules.first()
    else -> SequenceRule(rules)
  }

  /**
   * A rule that matches the first successful rule in a list of rules.
   *
   * @param rules a variable number of rules
   * @return a rule
   */
  @SafeVarargs
  protected open fun firstOf(vararg rules: Rule<T>) = firstOf(rules.toList())

  /**
   * A rule that matches the first successful rule in a list of rules.
   *
   * @param rules a list of rules
   * @return a rule
   */
  protected open fun firstOf(rules: List<Rule<T>>) = when {
    rules.isEmpty() -> emptyRule
    rules.size == 1 -> rules.first()
    else -> FirstOfRule(rules)
  }

  /**
   * A rule that matches its sub rule optionally. In other words, a rule that repeats its sub rule zero or one time.
   *
   * @param rule the sub rule to match optionally
   * @return a rule
   */
  protected open fun optional(rule: Rule<T>) = repeat(rule).times(0, 1)

  /**
   * A rule that matches its sub rule zero or more times.
   *
   * @param rule the sub rule to repeat
   * @return a rule
   */
  protected open fun zeroOrMore(rule: Rule<T>) = repeat(rule).min(0)

  /**
   * A rule that matches its sub rule one or more times.
   *
   * @param rule the sub rule to repeat
   * @return a rule
   */
  protected open fun oneOrMore(rule: Rule<T>) = repeat(rule).min(1)

  /**
   * A rule builder for a repeat rule.
   *
   * @param rule the sub rule to repeat
   * @return a repeat rule builder
   */
  protected open fun repeat(rule: Rule<T>) = RepeatRuleBuilder(rule)

  /**
   * A predicate rule that tests if its sub rule matches.
   *
   * @param rule the sub rule to test
   * @return a rule
   */
  protected open fun test(rule: Rule<T>) = TestRule(rule)

  /**
   * A predicate rule that tests if its sub rule does not match.
   *
   * @param rule the sub rule to test
   * @return a rule
   */
  protected open fun testNot(rule: Rule<T>) = TestNotRule(rule)

  /**
   * A conditional rule that runs one rule if a condition is true, otherwise it runs another rule.
   *
   * @param condition a condition
   * @param thenRule the rule to run if the condition is true
   * @param elseRule the rule to run if the condition is false
   * @return a rule
   */
  protected open fun conditional(condition: (ActionContext<T>) -> Boolean, thenRule: Rule<T>, elseRule: Rule<T>) =
    ConditionalRule(condition, thenRule, elseRule)

  /**
   * A conditional rule that runs a rule if a condition is true, otherwise it runs no rule.
   *
   * @param condition a condition
   * @param thenRule the rule to run if the condition is true
   * @return a rule
   */
  protected open fun conditional(condition: (ActionContext<T>) -> Boolean, thenRule: Rule<T>) =
    ConditionalRule(condition, thenRule)

  /**
   * A rule that runs an action.
   *
   * @param action the action to run
   * @return a rule
   */
  protected open fun action(action: Action<T>) = ActionRule(action)

  /**
   * A rule that executes a command.
   *
   * @param command the command to execute
   * @return a rule
   */
  protected open fun command(command: Command<T>) = action(command.toAction())

  /**
   * A rule that runs an action. The action is skipped if the rule is run inside a predicate rule.
   *
   * @param action the skippable action to run
   * @return a rule
   */
  protected open fun skippableAction(action: Action<T>) = ActionRule(action, true)

  /**
   * A rule that executes a command. The command is skipped if the rule is run inside a predicate rule.
   *
   * @param command the command to execute
   * @return a rule
   */
  protected open fun skippableCommand(command: Command<T>) = skippableAction(command.toAction())

  /**
   * Posts a event to the parser's event bus. Note that the event object is constructed at parser create time.
   *
   * @param event the event to post
   * @return a rule
   */
  protected open fun post(event: Any) = skippableCommand { ctx -> ctx.post(event) }

  /**
   * Posts a event to the parser's event bus. Note that the event object is supplied by an event supplier at parser
   * run time which has access to the parser context.
   *
   * @param supplier an event supplier that is called when the rule is run
   * @return a rule
   */
  protected open fun post(supplier: (ActionContext<T>) -> Any) = skippableCommand { ctx -> ctx.post(supplier(ctx)) }

  /**
   * Pops the top level element from the stack.
   *
   * @return a rule
   */
  protected open fun pop() = action { ctx ->
    ctx.stack.pop()
    true
  }

  /**
   * Pops an element from the stack at a given position.
   *
   * @param down number of elements on the stack to skip
   * @return a rule
   */
  protected open fun pop(down: Int) = action { ctx ->
    ctx.stack.pop(down)
    true
  }

  /**
   * Replaces the element on top of the stack. Note that the value is constructed at parser create time.
   *
   * @param value a replacement value
   * @return a rule
   */
  protected open fun poke(value: T) = action { ctx ->
    ctx.stack.poke(value)
    true
  }

  /**
   * Replaces an element in the stack at a given position. Note that the value is constructed at parser create time.
   *
   * @param down  number of elements on the stack to skip
   * @param value a replacement value
   * @return a rule
   */
  protected open fun poke(down: Int, value: T) = action { ctx ->
    ctx.stack.poke(down, value)
    true
  }

  /**
   * Replaces an element in the stack. Note that the value is supplied by a value supplier at parser run time which
   * has access to the parser context.
   *
   * @param supplier a replacement value supplier
   * @return a rule
   */
  protected open fun poke(supplier: (ActionContext<T>) -> T) = action { ctx ->
    ctx.stack.poke(supplier(ctx))
    true
  }

  /**
   * Replaces an element in the stack at a given position. Note that the value is supplied by a value supplier at
   * parser run time which has access to the parser context.
   *
   * @param down     number of elements on the stack to skip
   * @param supplier a replacement value supplier
   * @return a rule
   */
  protected open fun poke(down: Int, supplier: (ActionContext<T>) -> T) = action { ctx ->
    ctx.stack.poke(down, supplier(ctx))
    true
  }

  /**
   * Pushes a new element onto stack. Note that the value is constructed at parser create time.
   *
   * @param value a value
   * @return a rule
   */
  protected open fun push(value: T) = command { ctx -> ctx.stack.push(value) }

  /**
   * Pushes a new element onto stack. Note that the value is supplied by a value supplier at parser run time which has
   * access to the parser context.
   *
   * @param supplier a value supplier
   * @return a rule
   */
  protected open fun push(supplier: (ActionContext<T>) -> T) = command { ctx -> ctx.stack.push(supplier(ctx)) }

  /**
   * Duplicates the top stack element.
   *
   * @return a rule
   */
  protected open fun dup() = command { ctx -> ctx.stack.dup() }

  /**
   * Swaps the two top stack elements.
   *
   * @return a rule
   */
  protected open fun swap() = command { ctx -> ctx.stack.swap() }

  /**
   * Pops the top level element from the stack. This method may be called by an action or command where the action
   * context is available.
   *
   * @param context an action context
   * @return the popped element
   */
  protected fun pop(context: ActionContext<T>): T = context.stack.pop()

  /**
   * Pops an element from the stack at a given position. This method may be called by an action or command where the
   * action
   *
   * @param down number of elements on the stack to skip
   * @param context an action context
   * @return the popped element
   */
  protected fun pop(down: Int, context: ActionContext<T>) = context.stack.pop(down)

  /**
   * Pops and casts the top level element from the stack. This method may be called by an action or command where the
   * action context is available.
   *
   * @param U a sub type of T
   * @param clazz the the type to cast the element to
   * @param context an action context
   * @return the popped element
   */
  protected fun <U : T> popAs(clazz: Class<U>, context: ActionContext<T>) = context.stack.popAs(clazz)

  /**
   * Pops and casts an element from the stack at a given position. This method may be called by an action or command
   * where the action context is available.
   *
   * @param U a sub type of T
   * @param clazz the the type to cast the element to
   * @param down number of elements on the stack to skip
   * @param context an action context
   * @return the popped element
   */
  protected fun <U : T> popAs(clazz: Class<U>, down: Int, context: ActionContext<T>) = context.stack.popAs(down, clazz)

  /**
   * Peeks the top level element from the stack. This method may be called by an action or command where the action
   * context is available.
   *
   * @param context an action context
   * @return the peeked element
   */
  protected fun peek(context: ActionContext<T>): T = context.stack.peek()

  /**
   * Peeks an element from the stack at a given position. This method may be called by an action or command where the
   * action is available.
   *
   * @param down number of elements on the stack to skip
   * @param context an action context
   * @return the peeked element
   */
  protected fun peek(down: Int, context: ActionContext<T>) = context.stack.peek(down)

  /**
   * Peeks and casts the top level element from the stack. This method may be called by an action or command where the
   * action context is available.
   *
   * @param U a sub type of T
   * @param clazz the the type to cast the element to
   * @param context an action context
   * @return the peeked element
   */
  protected fun <U : T> peekAs(clazz: Class<U>, context: ActionContext<T>) = context.stack.peekAs(clazz)

  /**
   * Peeks and casts an element from the stack at a given position. This method may be called by an action or command
   * where the action context is available.
   *
   * @param U a sub type of T
   * @param clazz the the type to cast the element to
   * @param down number of elements on the stack to skip
   * @param context an action context
   * @return the peeked element
   */
  protected open fun <U : T> peekAs(clazz: Class<U>, down: Int, context: ActionContext<T>) =
    context.stack.peekAs(down, clazz)

}

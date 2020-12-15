package com.mpe85.grampa.parser

import com.google.common.base.CharMatcher
import com.google.common.base.Joiner
import com.google.common.base.Preconditions
import com.google.common.primitives.Ints
import com.ibm.icu.lang.UCharacter
import com.mpe85.grampa.builder.RepeatRuleBuilder
import com.mpe85.grampa.rule.Action
import com.mpe85.grampa.rule.ActionContext
import com.mpe85.grampa.rule.Command
import com.mpe85.grampa.rule.EventSupplier
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.ValueSupplier
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
import java.util.Arrays
import java.util.function.Predicate

/**
 * An abstract parser that defines a bunch of useful parser rules and actions. A concrete parser class should usually
 * extend this class.
 *
 * @author mpe85
 * @param T the type of the stack elements
 */
abstract class AbstractParser<T> : Parser<T> {

  companion object {
    private val JOINER = Joiner.on("")
  }

  private val EMPTY = EmptyRule<T>()
  private val NEVER = NeverRule<T>()
  private val EOI = EndOfInputRule<T>()
  private val ANY_CHAR = CharPredicateRule<T>(CharMatcher.any())
  private val ANY_CODEPOINT = CodePointPredicateRule<T> { ch -> UCharacter.isLegal(ch) }
  private val ASCII = CharPredicateRule<T>(CharMatcher.ascii())
  private val BMP = CodePointPredicateRule<T> { ch -> UCharacter.isBMP(ch) }
  private val DIGIT = CodePointPredicateRule<T> { ch -> UCharacter.isDigit(ch) }
  private val JAVA_IDENTIFIER_START =
    CodePointPredicateRule<T> { codePoint -> Character.isJavaIdentifierStart(codePoint) }
  private val JAVA_IDENTIFIER_PART =
    CodePointPredicateRule<T> { codePoint -> Character.isJavaIdentifierPart(codePoint) }
  private val LETTER = CodePointPredicateRule<T> { ch -> UCharacter.isLetter(ch) }
  private val LETTER_OR_DIGIT = CodePointPredicateRule<T> { ch -> UCharacter.isLetterOrDigit(ch) }
  private val PRINTABLE = CodePointPredicateRule<T> { ch -> UCharacter.isPrintable(ch) }
  private val SPACE_CHAR = CodePointPredicateRule<T> { ch -> UCharacter.isSpaceChar(ch) }
  private val WHITESPACE = CodePointPredicateRule<T> { ch -> UCharacter.isWhitespace(ch) }
  private val CR = CharPredicateRule<T>('\r')
  private val LF = CharPredicateRule<T>('\n')
  private val CRLF = StringRule<T>("\r\n")

  abstract override fun root(): Rule<T>

  /**
   * A rule that matches an empty string. Or in other words, a rule that matches no input character and always
   * succeeds.
   *
   * @return a rule
   */
  protected open fun empty(): Rule<T> {
    return EMPTY
  }

  /**
   * A rule that always fails.
   *
   * @return a rule
   */
  protected open fun never(): Rule<T> {
    return NEVER
  }

  /**
   * A rule that matches the end of the input.
   *
   * @return a rule
   */
  protected open fun eoi(): Rule<T> {
    return EOI
  }

  /**
   * A rule that matches any character.
   *
   * @return a rule
   */
  protected open fun anyChar(): Rule<T> {
    return ANY_CHAR
  }

  /**
   * A rule that matches any code point.
   *
   * @return a rule
   */
  protected open fun anyCodePoint(): Rule<T> {
    return ANY_CODEPOINT
  }

  /**
   * A rule that matches a specific character.
   *
   * @param character the character to match
   * @return a rule
   */
  protected open fun character(character: Char): Rule<T> {
    return CharPredicateRule(CharMatcher.`is`(character))
  }

  /**
   * A rule that matches a specific character, ignoring the case of the character (case-insensitive).
   *
   * @param character the character to match
   * @return a rule
   */
  protected open fun ignoreCase(character: Char): Rule<T> {
    return CharPredicateRule(
      CharMatcher.`is`(Character.toLowerCase(character))
        .or(CharMatcher.`is`(Character.toUpperCase(character)))
    )
  }

  /**
   * A rule that matches a character within a range of characters.
   *
   * @param lowerBound the lower bound of the character range (inclusive)
   * @param upperBound the upper bound of the character range (inclusive)
   * @return a rule
   */
  protected open fun charRange(lowerBound: Char, upperBound: Char): Rule<T> {
    return CharPredicateRule(CharMatcher.inRange(lowerBound, upperBound))
  }

  /**
   * A rule that matches a character within a set of characters.
   *
   * @param characters a variable number of characters
   * @return a rule
   */
  protected open fun anyOfChars(vararg characters: Char): Rule<T> {
    return anyOfChars(String(characters))
  }

  /**
   * A rule that matches a character within a set of characters.
   *
   * @param characters a set of characters
   * @return a rule
   */
  protected open fun anyOfChars(characters: Set<Char>): Rule<T> {
    return anyOfChars(JOINER.join(characters))
  }

  /**
   * A rule that matches a character within a set of characters.
   *
   * @param characters a string containing the set of characters.
   * @return a rule
   */
  protected open fun anyOfChars(characters: String): Rule<T> {
    if (characters.isEmpty()) {
      return NEVER
    } else if (characters.length == 1) {
      return character(characters[0])
    }
    return CharPredicateRule(CharMatcher.anyOf(characters))
  }

  /**
   * A rule that matches a character not in a set of characters.
   *
   * @param characters a variable number of characters
   * @return a rule
   */
  protected open fun noneOfChars(vararg characters: Char): Rule<T> {
    return noneOfChars(String(characters))
  }

  /**
   * A rule that matches a character not in a set of characters.
   *
   * @param characters a set of characters
   * @return a rule
   */
  protected open fun noneOfChars(characters: Set<Char>): Rule<T> {
    return noneOfChars(JOINER.join(characters))
  }

  /**
   * A rule that matches a character not in a a set of characters.
   *
   * @param characters a string containing the set of characters.
   * @return a rule
   */
  protected open fun noneOfChars(characters: String): Rule<T> {
    return if (characters.isEmpty()) {
      ANY_CHAR
    } else CharPredicateRule(CharMatcher.noneOf(characters))
  }

  /**
   * A rule that matches a specific code point.
   *
   * @param codePoint the code point to match
   * @return a rule
   */
  protected open fun codePoint(codePoint: Int): Rule<T> {
    return CodePointPredicateRule { cp -> cp == codePoint }
  }

  /**
   * A rule that matches a specific code point, ignoring the case of the code point (case-insensitive).
   *
   * @param codePoint the code point to match
   * @return a rule
   */
  protected open fun ignoreCase(codePoint: Int): Rule<T> {
    return CodePointPredicateRule { cp ->
      cp == UCharacter.toLowerCase(codePoint) || cp == UCharacter.toUpperCase(codePoint)
    }
  }

  /**
   * A rule that matches a code point within a range of code points.
   *
   * @param lowerBound the lower bound of the code point range (inclusive)
   * @param upperBound the upper bound of the code point range (inclusive)
   * @return a rule
   */
  protected open fun codePointRange(lowerBound: Int, upperBound: Int): Rule<T> {
    Preconditions.checkArgument(lowerBound <= upperBound, "A 'lowerBound' must not be greater than an 'upperBound'.")
    return CodePointPredicateRule { cp -> cp in lowerBound..upperBound }
  }

  /**
   * A rule that matches a code point within a set of code points.
   *
   * @param codePoints a variable number of code points
   * @return a rule
   */
  protected open fun anyOfCodePoints(vararg codePoints: Int): Rule<T> {
    if (codePoints.isEmpty()) {
      return NEVER
    } else if (codePoints.size == 1) {
      return codePoint(codePoints[0])
    }
    Arrays.sort(codePoints)
    return CodePointPredicateRule { cp -> Arrays.binarySearch(codePoints, cp) >= 0 }
  }

  /**
   * A rule that matches a code point within a set of code points.
   *
   * @param codePoints a string containing the set of code points.
   * @return a rule
   */
  protected open fun anyOfCodePoints(codePoints: String): Rule<T> {
    return anyOfCodePoints(*codePoints.codePoints().toArray())
  }

  /**
   * A rule that matches a code point within a set of code points.
   *
   * @param codePoints a set of code points.
   * @return a rule
   */
  protected open fun anyOfCodePoints(codePoints: Set<Int>): Rule<T> {
    return anyOfCodePoints(*Ints.toArray(codePoints))
  }

  /**
   * A rule that matches a code point not in a set of code points.
   *
   * @param codePoints a variable number of code points
   * @return a rule
   */
  protected open fun noneOfCodePoints(vararg codePoints: Int): Rule<T> {
    if (codePoints.isEmpty()) {
      return ANY_CODEPOINT
    }
    Arrays.sort(codePoints)
    return CodePointPredicateRule { cp -> Arrays.binarySearch(codePoints, cp) < 0 }
  }

  /**
   * A rule that matches a code point not in a set of code points.
   *
   * @param codePoints a string containing the set of code points.
   * @return a rule
   */
  protected open fun noneOfCodePoints(codePoints: String): Rule<T> {
    return noneOfCodePoints(*codePoints.codePoints().toArray())
  }

  /**
   * A rule that matches a code point not in a set of code points.
   *
   * @param codePoints a set of code points.
   * @return a rule
   */
  protected open fun noneOfCodePoints(codePoints: Set<Int>): Rule<T> {
    return noneOfCodePoints(*Ints.toArray(codePoints))
  }

  /**
   * A rule that matches a specific string.
   *
   * @param string the string to match
   * @return a rule
   */
  protected open fun string(string: String): Rule<T> {
    if (string.isEmpty()) {
      return EMPTY
    } else if (string.length == 1) {
      return character(string[0])
    }
    return StringRule(string)
  }

  /**
   * A rule that matches a specific string, ignoring the case of its characters (case-insensitive).
   *
   * @param string the string to match
   * @return a rule
   */
  protected open fun ignoreCase(string: String): Rule<T> {
    if (string.isEmpty()) {
      return EMPTY
    } else if (string.length == 1) {
      return ignoreCase(string[0])
    }
    return StringRule(string, true)
  }

  /**
   * A rule that matches a regular expression
   *
   * @param regex a regular expression
   * @return a rule
   */
  protected open fun regex(regex: String): Rule<T> {
    return RegexRule(regex)
  }

  /**
   * A rule that matches a string within a set of strings.
   *
   * @param strings a variable number of strings
   * @return a rule
   */
  protected open fun strings(vararg strings: String): Rule<T> {
    return strings(strings.toSet())
  }

  /**
   * A rule that matches a string within a set of strings.
   *
   * @param strings a set of strings
   * @return a rule
   */
  protected open fun strings(strings: Set<String>): Rule<T> {
    if (strings.isEmpty()) {
      return NEVER
    } else if (strings.size == 1) {
      return string(strings.iterator().next())
    }
    return TrieRule(strings)
  }

  /**
   * A rule that matches a string within a set of strings, ignoring the case of their characters (case-insensitive).
   *
   * @param strings a variable number of strings
   * @return a rule
   */
  protected open fun ignoreCase(vararg strings: String): Rule<T> {
    return ignoreCase(strings.toSet())
  }

  /**
   * A rule that matches a string within a set of strings, ignoring the case of their characters (case-insensitive).
   *
   * @param strings a set of strings
   * @return a rule
   */
  protected open fun ignoreCase(strings: Set<String>): Rule<T> {
    if (strings.isEmpty()) {
      return NEVER
    } else if (strings.size == 1) {
      return ignoreCase(strings.iterator().next())
    }
    return TrieRule(strings, true)
  }

  /**
   * A rule that matches an ASCII character.
   *
   * @return a rule
   */
  protected open fun ascii(): Rule<T> {
    return ASCII
  }

  /**
   * A rule that matches a characters of Unicode's Basic Multilingual Plane.
   *
   * @return a rule
   */
  protected open fun bmp(): Rule<T> {
    return BMP
  }

  /**
   * A rule that matches a digit.
   *
   * @return a rule
   */
  protected open fun digit(): Rule<T> {
    return DIGIT
  }

  /**
   * A rule that matches a character which is valid to be the first character of a java identifier.
   *
   * @return a rule
   */
  protected open fun javaIdentifierStart(): Rule<T> {
    return JAVA_IDENTIFIER_START
  }

  /**
   * A rule that matches a character which is valid to be part character of a java identifier, other than the first
   * character.
   *
   * @return a rule
   */
  protected open fun javaIdentifierPart(): Rule<T> {
    return JAVA_IDENTIFIER_PART
  }

  /**
   * A rule that matches a letter.
   *
   * @return a rule
   */
  protected open fun letter(): Rule<T> {
    return LETTER
  }

  /**
   * A rule that matches a letter or a digit.
   *
   * @return a rule
   */
  protected open fun letterOrDigit(): Rule<T> {
    return LETTER_OR_DIGIT
  }

  /**
   * A rule that matches a printable character.
   *
   * @return a rule
   */
  protected open fun printable(): Rule<T> {
    return PRINTABLE
  }

  /**
   * A rule that matches a space character.
   *
   * @return a rule
   */
  protected open fun spaceChar(): Rule<T> {
    return SPACE_CHAR
  }

  /**
   * A rule that matches a whitespace character.
   *
   * @return a rule
   */
  protected open fun whitespace(): Rule<T> {
    return WHITESPACE
  }

  /**
   * A rule that matches the carriage return character.
   *
   * @return a rule
   */
  protected open fun cr(): Rule<T> {
    return CR
  }

  /**
   * A rule that matches the line feed character.
   *
   * @return a rule
   */
  protected open fun lf(): Rule<T> {
    return LF
  }

  /**
   * A rule that matches the carriage return and line feed characters.
   *
   * @return a rule
   */
  protected open fun crlf(): Rule<T> {
    return CRLF
  }

  /**
   * A rule that matches a sequence of rules.
   *
   * @param rules a variable number of rules
   * @return a rule
   */
  @SafeVarargs
  protected open fun sequence(vararg rules: Rule<T>): Rule<T> {
    return sequence(rules.toList())
  }

  /**
   * A rule that matches a sequence of rules.
   *
   * @param rules a list of rules
   * @return a rule
   */
  protected open fun sequence(rules: List<Rule<T>>): Rule<T> {
    if (rules.isEmpty()) {
      return EMPTY
    } else if (rules.size == 1) {
      return rules[0]
    }
    return SequenceRule(rules)
  }

  /**
   * A rule that matches the first successful rule in a list of rules.
   *
   * @param rules a variable number of rules
   * @return a rule
   */
  @SafeVarargs
  protected open fun firstOf(vararg rules: Rule<T>): Rule<T> {
    return firstOf(rules.toList())
  }

  /**
   * A rule that matches the first successful rule in a list of rules.
   *
   * @param rules a list of rules
   * @return a rule
   */
  protected open fun firstOf(rules: List<Rule<T>>): Rule<T> {
    if (rules.isEmpty()) {
      return EMPTY
    } else if (rules.size == 1) {
      return rules[0]
    }
    return FirstOfRule(rules)
  }

  /**
   * A rule that matches its sub rule optionally. In other words, a rule that repeats its sub rule zero or one time.
   *
   * @param rule the sub rule to match optionally
   * @return a rule
   */
  protected open fun optional(rule: Rule<T>): Rule<T> {
    return repeat(rule).times(0, 1)
  }

  /**
   * A rule that matches its sub rule zero or more times.
   *
   * @param rule the sub rule to repeat
   * @return a rule
   */
  protected open fun zeroOrMore(rule: Rule<T>): Rule<T> {
    return repeat(rule).min(0)
  }

  /**
   * A rule that matches its sub rule one or more times.
   *
   * @param rule the sub rule to repeat
   * @return a rule
   */
  protected open fun oneOrMore(rule: Rule<T>): Rule<T> {
    return repeat(rule).min(1)
  }

  /**
   * A rule builder for a repeat rule.
   *
   * @param rule the sub rule to repeat
   * @return a repeat rule builder
   */
  protected open fun repeat(rule: Rule<T>): RepeatRuleBuilder<T> {
    return RepeatRuleBuilder(rule)
  }

  /**
   * A predicate rule that tests if its sub rule matches.
   *
   * @param rule the sub rule to test
   * @return a rule
   */
  protected open fun test(rule: Rule<T>): Rule<T> {
    return TestRule(rule)
  }

  /**
   * A predicate rule that tests if its sub rule does not match.
   *
   * @param rule the sub rule to test
   * @return a rule
   */
  protected open fun testNot(rule: Rule<T>): Rule<T> {
    return TestNotRule(rule)
  }

  /**
   * A conditional rule that runs one rule if a condition is true, otherwise it runs another rule.
   *
   * @param condition a condition
   * @param thenRule the rule to run if the condition is true
   * @param elseRule the rule to run if the condition is false
   * @return a rule
   */
  protected open fun conditional(
    condition: Predicate<ActionContext<T>>,
    thenRule: Rule<T>,
    elseRule: Rule<T>
  ): Rule<T> {
    return ConditionalRule(condition, thenRule, elseRule)
  }

  /**
   * A conditional rule that runs a rule if a condition is true, otherwise it runs no rule.
   *
   * @param condition a condition
   * @param thenRule the rule to run if the condition is true
   * @return a rule
   */
  protected open fun conditional(condition: Predicate<ActionContext<T>>, thenRule: Rule<T>): Rule<T> {
    return ConditionalRule(condition, thenRule)
  }

  /**
   * A rule that runs an action.
   *
   * @param action the action to run
   * @return a rule
   */
  protected open fun action(action: Action<T>): Rule<T> {
    return ActionRule(action)
  }

  /**
   * A rule that executes a command.
   *
   * @param command the command to execute
   * @return a rule
   */
  protected open fun command(command: Command<T>): Rule<T> {
    return action(command.toAction())
  }

  /**
   * A rule that runs an action. The action is skipped if the rule is run inside a predicate rule.
   *
   * @param action the skippable action to run
   * @return a rule
   */
  protected open fun skippableAction(action: Action<T>): Rule<T> {
    return ActionRule(action, true)
  }

  /**
   * A rule that executes a command. The command is skipped if the rule is run inside a predicate rule.
   *
   * @param command the command to execute
   * @return a rule
   */
  protected open fun skippableCommand(command: Command<T>): Rule<T> {
    return skippableAction(command.toAction())
  }

  /**
   * Posts a event to the parser's event bus. Note that the event object is constructed at parser create time.
   *
   * @param event the event to post
   * @return a rule
   */
  protected open fun post(event: Any): Rule<T> {
    return skippableCommand { ctx -> ctx.post(event) }
  }

  /**
   * Posts a event to the parser's event bus. Note that the event object is supplied by an event supplier at parser
   * run time which has access to the parser context.
   *
   * @param supplier an event supplier that is called when the rule is run
   * @return a rule
   */
  protected open fun post(supplier: EventSupplier<T>): Rule<T> {
    return skippableCommand { ctx -> ctx.post(supplier.supply(ctx)) }
  }

  /**
   * Pops the top level element from the stack.
   *
   * @return a rule
   */
  protected open fun pop(): Rule<T> {
    return action { ctx ->
      ctx.stack.pop()
      true
    }
  }

  /**
   * Pops an element from the stack at a given position.
   *
   * @param down number of elements on the stack to skip
   * @return a rule
   */
  protected open fun pop(down: Int): Rule<T> {
    return action { ctx ->
      ctx.stack.pop(down)
      true
    }
  }

  /**
   * Replaces the element on top of the stack. Note that the value is constructed at parser create time.
   *
   * @param value a replacement value
   * @return a rule
   */
  protected open fun poke(value: T): Rule<T> {
    return action { ctx ->
      ctx.stack.poke(value)
      true
    }
  }

  /**
   * Replaces an element in the stack at a given position. Note that the value is constructed at parser create time.
   *
   * @param down  number of elements on the stack to skip
   * @param value a replacement value
   * @return a rule
   */
  protected open fun poke(down: Int, value: T): Rule<T> {
    return action { ctx ->
      ctx.stack.poke(down, value)
      true
    }
  }

  /**
   * Replaces an element in the stack. Note that the value is supplied by a value supplier at parser run time which
   * has access to the parser context.
   *
   * @param supplier a replacement value supplier
   * @return a rule
   */
  protected open fun poke(supplier: ValueSupplier<T>): Rule<T> {
    return action { ctx ->
      ctx.stack.poke(supplier.supply(ctx))
      true
    }
  }

  /**
   * Replaces an element in the stack at a given position. Note that the value is supplied by a value supplier at
   * parser run time which has access to the parser context.
   *
   * @param down     number of elements on the stack to skip
   * @param supplier a replacement value supplier
   * @return a rule
   */
  protected open fun poke(down: Int, supplier: ValueSupplier<T>): Rule<T> {
    return action { ctx ->
      ctx.stack.poke(down, supplier.supply(ctx))
      true
    }
  }

  /**
   * Pushes a new element onto stack. Note that the value is constructed at parser create time.
   *
   * @param value a value
   * @return a rule
   */
  protected open fun push(value: T): Rule<T> {
    return command { ctx -> ctx.stack.push(value) }
  }

  /**
   * Pushes a new element onto stack. Note that the value is supplied by a value supplier at parser run time which has
   * access to the parser context.
   *
   * @param supplier a value supplier
   * @return a rule
   */
  protected open fun push(supplier: ValueSupplier<T>): Rule<T> {
    return command { ctx -> ctx.stack.push(supplier.supply(ctx)) }
  }

  /**
   * Duplicates the top stack element.
   *
   * @return a rule
   */
  protected open fun dup(): Rule<T> {
    return command { ctx -> ctx.stack.dup() }
  }

  /**
   * Swaps the two top stack elements.
   *
   * @return a rule
   */
  protected open fun swap(): Rule<T> {
    return command { ctx -> ctx.stack.swap() }
  }

  /**
   * Pops the top level element from the stack. This method may be called by an action or command where the action
   * context is available.
   *
   * @param context an action context
   * @return the popped element
   */
  protected open fun pop(context: ActionContext<T>): T {
    return context.stack.pop()
  }

  /**
   * Pops an element from the stack at a given position. This method may be called by an action or command where the
   * action
   *
   * @param down number of elements on the stack to skip
   * @param context an action context
   * @return the popped element
   */
  protected open fun pop(down: Int, context: ActionContext<T>): T {
    return context.stack.pop(down)
  }

  /**
   * Pops and casts the top level element from the stack. This method may be called by an action or command where the
   * action context is available.
   *
   * @param U a sub type of T
   * @param clazz the the type to cast the element to
   * @param context an action context
   * @return the popped element
   */
  protected open fun <U : T> popAs(clazz: Class<U>, context: ActionContext<T>): U {
    return context.stack.popAs(clazz)
  }

  /**
   * Pops and casts an element from the stack at a given position. This method may be called by an action or command
   * where the action context is available.
   *
   * @param U a sub type of T
   * @param clazz the the type to cast the element to
   * @param down number of elements on the stack to skip
   * @param context an action context
   * @return the popped element
  </U> */
  protected open fun <U : T> popAs(clazz: Class<U>, down: Int, context: ActionContext<T>): U {
    return context.stack.popAs(down, clazz)
  }

  /**
   * Peeks the top level element from the stack. This method may be called by an action or command where the action
   * context is available.
   *
   * @param context an action context
   * @return the peeked element
   */
  protected open fun peek(context: ActionContext<T>): T {
    return context.stack.peek()
  }

  /**
   * Peeks an element from the stack at a given position. This method may be called by an action or command where the
   * action is available.
   *
   * @param down number of elements on the stack to skip
   * @param context an action context
   * @return the peeked element
   */
  protected open fun peek(down: Int, context: ActionContext<T>): T {
    return context.stack.peek(down)
  }

  /**
   * Peeks and casts the top level element from the stack. This method may be called by an action or command where the
   * action context is available.
   *
   * @param U a sub type of T
   * @param clazz the the type to cast the element to
   * @param context an action context
   * @return the peeked element
   */
  protected open fun <U : T> peekAs(clazz: Class<U>, context: ActionContext<T>): U {
    return context.stack.peekAs(clazz)
  }

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
  protected open fun <U : T> peekAs(clazz: Class<U>, down: Int, context: ActionContext<T>): U {
    return context.stack.peekAs(down, clazz)
  }

}

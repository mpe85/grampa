package com.mpe85.grampa.parser;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;
import com.ibm.icu.lang.UCharacter;
import com.mpe85.grampa.builder.RepeatRuleBuilder;
import com.mpe85.grampa.rule.Action;
import com.mpe85.grampa.rule.ActionContext;
import com.mpe85.grampa.rule.Command;
import com.mpe85.grampa.rule.EventSupplier;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.ValueSupplier;
import com.mpe85.grampa.rule.impl.ActionRule;
import com.mpe85.grampa.rule.impl.CharPredicateRule;
import com.mpe85.grampa.rule.impl.CodePointPredicateRule;
import com.mpe85.grampa.rule.impl.EmptyRule;
import com.mpe85.grampa.rule.impl.EndOfInputRule;
import com.mpe85.grampa.rule.impl.FirstOfRule;
import com.mpe85.grampa.rule.impl.NeverRule;
import com.mpe85.grampa.rule.impl.RegexRule;
import com.mpe85.grampa.rule.impl.SequenceRule;
import com.mpe85.grampa.rule.impl.StringRule;
import com.mpe85.grampa.rule.impl.TestNotRule;
import com.mpe85.grampa.rule.impl.TestRule;
import com.mpe85.grampa.rule.impl.TrieRule;

import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

/**
 * An abstract parser that defines a bunch of useful parser rules and actions. A concrete parser class should usually
 * extend this class.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public abstract class AbstractParser<T> implements Parser<T> {
	
	@Override
	public abstract Rule<T> root();
	
	/**
	 * A rule that matches an empty string. Or in other words, a rule that matches nothing and always succeeds.
	 * 
	 * @return a rule
	 */
	protected Rule<T> empty() {
		return EMPTY;
	}
	
	/**
	 * A rule that always fails.
	 * 
	 * @return a rule
	 */
	protected Rule<T> never() {
		return NEVER;
	}
	
	/**
	 * A rule that matches the end of the input.
	 * 
	 * @return a rule
	 */
	protected Rule<T> eoi() {
		return EOI;
	}
	
	/**
	 * A rule that matches any character.
	 * 
	 * @return a rule
	 */
	protected Rule<T> anyChar() {
		return ANY_CHAR;
	}
	
	/**
	 * a rule that matches any code point.
	 * 
	 * @return a rule
	 */
	protected Rule<T> anyCodePoint() {
		return ANY_CODEPOINT;
	}
	
	/**
	 * A rule that matches a specific character.
	 * 
	 * @param character
	 *            the character to match
	 * @return a rule
	 */
	protected Rule<T> character(final char character) {
		return new CharPredicateRule<>(CharMatcher.is(character));
	}
	
	/**
	 * A rule that matches a specific character, ignoring the case of the character (case-insensitive).
	 * 
	 * @param character
	 *            the character to match
	 * @return a rule
	 */
	protected Rule<T> ignoreCase(final char character) {
		return new CharPredicateRule<>(
				CharMatcher.is(Character.toLowerCase(character))
						.or(CharMatcher.is(Character.toUpperCase(character))));
	}
	
	/**
	 * A rule that matches a character within a range of characters.
	 * 
	 * @param lowerBound
	 *            the lower bound of the character range (inclusive)
	 * @param upperBound
	 *            the upper bound of the character range (inclusive)
	 * @return a rule
	 */
	protected Rule<T> charRange(final char lowerBound, final char upperBound) {
		return new CharPredicateRule<>(CharMatcher.inRange(lowerBound, upperBound));
	}
	
	/**
	 * A rule that matches a character within a set of characters.
	 * 
	 * @param characters
	 *            a variable number of characters
	 * @return a rule
	 */
	protected Rule<T> anyOfChars(final char... characters) {
		return anyOfChars(String.valueOf(checkNotNull(characters, "A 'characters' array must not be null.")));
	}
	
	/**
	 * A rule that matches a character within a set of characters.
	 * 
	 * @param characters
	 *            a set of characters
	 * @return a rule
	 */
	protected Rule<T> anyOfChars(final Set<Character> characters) {
		checkNotNull(characters, "A set of 'characters' must not be null.");
		return anyOfChars(StreamEx.of(characters).joining());
	}
	
	/**
	 * A rule that matches a character within a set of characters.
	 * 
	 * @param characters
	 *            a string containing the set of characters.
	 * @return a rule
	 */
	protected Rule<T> anyOfChars(final String characters) {
		checkNotNull(characters, "A 'characters' string must not be null.");
		if (characters.length() == 0) {
			return NEVER;
		}
		else if (characters.length() == 1) {
			return character(characters.charAt(0));
		}
		return new CharPredicateRule<>(CharMatcher.anyOf(characters));
	}
	
	/**
	 * A rule that matches a character not in a set of characters.
	 * 
	 * @param characters
	 *            a variable number of characters
	 * @return a rule
	 */
	protected Rule<T> noneOfChars(final char... characters) {
		return noneOfChars(String.valueOf(checkNotNull(characters, "A 'characters' array must not be null.")));
	}
	
	/**
	 * A rule that matches a character not in a set of characters.
	 * 
	 * @param characters
	 *            a set of characters
	 * @return a rule
	 */
	protected Rule<T> noneOfChars(final Set<Character> characters) {
		checkNotNull(characters, "A set of 'characters' must not be null.");
		return noneOfChars(StreamEx.of(characters).joining());
	}
	
	/**
	 * A rule that matches a character not in a a set of characters.
	 * 
	 * @param characters
	 *            a string containing the set of characters.
	 * @return a rule
	 */
	protected Rule<T> noneOfChars(final String characters) {
		checkNotNull(characters, "A 'characters' string must not be null.");
		if (characters.length() == 0) {
			return ANY_CHAR;
		}
		return new CharPredicateRule<>(CharMatcher.noneOf(characters));
	}
	
	/**
	 * A rule that matches a specific code point.
	 * 
	 * @param codePoint
	 *            the code point to match
	 * @return a rule
	 */
	protected Rule<T> codePoint(final int codePoint) {
		return new CodePointPredicateRule<>(cp -> cp == codePoint);
	}
	
	/**
	 * A rule that matches a specific code point, ignoring the case of the code point (case-insensitive).
	 * 
	 * @param codePoint
	 *            the code point to match
	 * @return a rule
	 */
	protected Rule<T> ignoreCase(final int codePoint) {
		return new CodePointPredicateRule<>(
				cp -> cp == UCharacter.toLowerCase(codePoint) || cp == UCharacter.toUpperCase(codePoint));
	}
	
	/**
	 * A rule that matches a code point within a range of code points.
	 * 
	 * @param lowerBound
	 *            the lower bound of the code point range (inclusive)
	 * @param upperBound
	 *            the upper bound of the code point range (inclusive)
	 * @return a rule
	 */
	protected Rule<T> codePointRange(final int lowerBound, final int upperBound) {
		checkArgument(lowerBound <= upperBound, "A 'lowerBound' must not be greater than an 'upperBound'.");
		return new CodePointPredicateRule<>(cp -> cp >= lowerBound && cp <= upperBound);
	}
	
	/**
	 * A rule that matches a code point within a set of code points.
	 * 
	 * @param codePoints
	 *            a variable number of code points
	 * @return a rule
	 */
	protected Rule<T> anyOfCodePoints(final int... codePoints) {
		checkNotNull(codePoints, "A 'codePoints' array must not be null.");
		if (codePoints.length == 0) {
			return NEVER;
		}
		else if (codePoints.length == 1) {
			return codePoint(codePoints[0]);
		}
		Arrays.sort(codePoints);
		return new CodePointPredicateRule<>(cp -> Arrays.binarySearch(codePoints, cp) >= 0);
	}
	
	/**
	 * A rule that matches a code point within a set of code points.
	 * 
	 * @param codePoints
	 *            a string containing the set of code points.
	 * @return a rule
	 */
	protected Rule<T> anyOfCodePoints(final String codePoints) {
		checkNotNull(codePoints, "A 'codePoints' string must not be null.");
		return anyOfCodePoints(codePoints.codePoints().toArray());
	}
	
	/**
	 * A rule that matches a code point within a set of code points.
	 * 
	 * @param codePoints
	 *            a set of code points.
	 * @return a rule
	 */
	protected Rule<T> anyOfCodePoints(final Set<Integer> codePoints) {
		checkNotNull(codePoints, "A set of 'codePoints' must not be null.");
		return anyOfCodePoints(IntStreamEx.of(codePoints).toArray());
	}
	
	/**
	 * A rule that matches a code point not in a set of code points.
	 * 
	 * @param codePoints
	 *            a variable number of code points
	 * @return a rule
	 */
	protected Rule<T> noneOfCodePoints(final int... codePoints) {
		checkNotNull(codePoints, "A 'codePoints' array must not be null.");
		if (codePoints.length == 0) {
			return ANY_CODEPOINT;
		}
		Arrays.sort(codePoints);
		return new CodePointPredicateRule<>(cp -> Arrays.binarySearch(codePoints, cp) < 0);
	}
	
	/**
	 * A rule that matches a code point not in a set of code points.
	 * 
	 * @param codePoints
	 *            a string containing the set of code points.
	 * @return a rule
	 */
	protected Rule<T> noneOfCodePoints(final String codePoints) {
		checkNotNull(codePoints, "A 'codePoints' string must not be null.");
		return noneOfCodePoints(codePoints.codePoints().toArray());
	}
	
	/**
	 * A rule that matches a code point not in a set of code points.
	 * 
	 * @param codePoints
	 *            a set of code points.
	 * @return a rule
	 */
	protected Rule<T> noneOfCodePoints(final Set<Integer> codePoints) {
		checkNotNull(codePoints, "A set of 'codePoints' must not be null.");
		return noneOfCodePoints(IntStreamEx.of(codePoints).toArray());
	}
	
	/**
	 * A rule that matches a specific string.
	 * 
	 * @param string
	 *            the string to match
	 * @return a rule
	 */
	protected Rule<T> string(final String string) {
		checkNotNull(string, "A 'string' must not be null.");
		if (string.length() == 0) {
			return EMPTY;
		}
		else if (string.length() == 1) {
			return character(string.charAt(0));
		}
		return new StringRule<>(string);
	}
	
	/**
	 * A rule that matches a specific string, ignoring the case of its characters (case-insensitive).
	 * 
	 * @param string
	 *            the string to match
	 * @return a rule
	 */
	protected Rule<T> ignoreCase(final String string) {
		checkNotNull(string, "A 'string' must not be null.");
		if (string.length() == 0) {
			return EMPTY;
		}
		else if (string.length() == 1) {
			return ignoreCase(string.charAt(0));
		}
		return new StringRule<>(string, true);
		
	}
	
	/**
	 * A rule that matches a regular expression
	 * 
	 * @param regex
	 *            a regular expression
	 * @return a rule
	 */
	protected Rule<T> regex(final String regex) {
		return new RegexRule<>(checkNotNull(regex, "A 'regex' must not be null."));
	}
	
	/**
	 * A rule that matches a string within a set of strings.
	 * 
	 * @param strings
	 *            a variable number of strings
	 * @return a rule
	 */
	protected Rule<T> strings(final String... strings) {
		return strings(Sets.newHashSet(checkNotNull(strings, "A set of 'strings' must not be null.")));
	}
	
	/**
	 * A rule that matches a string within a set of strings.
	 * 
	 * @param strings
	 *            a set of strings
	 * @return a rule
	 */
	protected Rule<T> strings(final Set<String> strings) {
		checkNotNull(strings, "A set of 'strings' must not be null.");
		if (strings.size() == 0) {
			return NEVER;
		}
		else if (strings.size() == 1) {
			return string(strings.iterator().next());
		}
		return new TrieRule<>(strings);
	}
	
	/**
	 * A rule that matches a string within a set of strings, ignoring the case of their characters (case-insensitive).
	 * 
	 * @param strings
	 *            a variable number of strings
	 * @return a rule
	 */
	protected Rule<T> ignoreCase(final String... strings) {
		return ignoreCase(Sets.newHashSet(checkNotNull(strings, "A set of 'strings' must not be null.")));
	}
	
	/**
	 * A rule that matches a string within a set of strings, ignoring the case of their characters (case-insensitive).
	 * 
	 * @param strings
	 *            a set of strings
	 * @return a rule
	 */
	protected Rule<T> ignoreCase(final Set<String> strings) {
		checkNotNull(strings, "A set of 'strings' must not be null.");
		if (strings.size() == 0) {
			return NEVER;
		}
		else if (strings.size() == 1) {
			return ignoreCase(strings.iterator().next());
		}
		return new TrieRule<>(strings, true);
	}
	
	/**
	 * A rule that matches an ASCII character.
	 * 
	 * @return a rule
	 */
	protected Rule<T> ascii() {
		return ASCII;
	}
	
	/**
	 * A rule that matches a characters of Unicode's Basic Multilingual Plane.
	 * 
	 * @return a rule
	 */
	protected Rule<T> bmp() {
		return BMP;
	}
	
	/**
	 * A rule that matches a digit.
	 * 
	 * @return a rule
	 */
	protected Rule<T> digit() {
		return DIGIT;
	}
	
	/**
	 * A rule that matches a character which is valid to be the first character of a java identifier.
	 * 
	 * @return a rule
	 */
	protected Rule<T> javaIdentifierStart() {
		return JAVA_IDENTIFIER_START;
	}
	
	/**
	 * A rule that matches a character which is valid to be part character of a java identifier, other than the first
	 * character.
	 * 
	 * @return a rule
	 */
	protected Rule<T> javaIdentifierPart() {
		return JAVA_IDENTIFIER_PART;
	}
	
	/**
	 * Matches a letter.
	 * 
	 * @return a rule
	 */
	protected Rule<T> letter() {
		return LETTER;
	}
	
	/**
	 * Matches a letter or a digit.
	 * 
	 * @return a rule
	 */
	protected Rule<T> letterOrDigit() {
		return LETTER_OR_DIGIT;
	}
	
	/**
	 * Matches a printable character.
	 * 
	 * @return a rule
	 */
	protected Rule<T> printable() {
		return PRINTABLE;
	}
	
	/**
	 * Matches a space character.
	 * 
	 * @return a rule
	 */
	protected Rule<T> spaceChar() {
		return SPACE_CHAR;
	}
	
	/**
	 * Matches a whitespace character.
	 * 
	 * @return a rule
	 */
	protected Rule<T> whitespace() {
		return WHITESPACE;
	}
	
	/**
	 * Matches the carriage return character.
	 * 
	 * @return a rule
	 */
	protected Rule<T> cr() {
		return CR;
	}
	
	/**
	 * Matches the line feed character.
	 * 
	 * @return a rule
	 */
	protected Rule<T> lf() {
		return LF;
	}
	
	/**
	 * Matches the carriage return and line feed characters.
	 * 
	 * @return a rule
	 */
	protected Rule<T> crlf() {
		return CRLF;
	}
	
	/**
	 * A rule that matches a sequence of rules.
	 * 
	 * @param rules
	 *            a variable number of rules
	 * @return a rule
	 */
	@SafeVarargs
	protected final Rule<T> sequence(final Rule<T>... rules) {
		return sequence(Arrays.asList(checkNotNull(rules, "A 'rules' array must not be null.")));
	}
	
	/**
	 * A rule that matches a sequence of rules.
	 * 
	 * @param rules
	 *            a list of rules
	 * @return a rule
	 */
	protected Rule<T> sequence(final List<Rule<T>> rules) {
		checkNotNull(rules, "A 'rules' list must not be null.");
		if (rules.size() == 0) {
			return EMPTY;
		}
		else if (rules.size() == 1) {
			return rules.get(0);
		}
		return new SequenceRule<>(rules);
	}
	
	/**
	 * A rule that matches the first successful rule in a list of rules.
	 * 
	 * @param rules
	 *            a variable number of rules
	 * @return a rule
	 */
	@SafeVarargs
	protected final Rule<T> firstOf(final Rule<T>... rules) {
		return firstOf(Arrays.asList(checkNotNull(rules, "A 'rules' array must not be null.")));
	}
	
	/**
	 * A rule that matches the first successful rule in a list of rules.
	 * 
	 * @param rules
	 *            a list of rules
	 * @return a rule
	 */
	protected Rule<T> firstOf(final List<Rule<T>> rules) {
		checkNotNull(rules, "A 'rules' list must not be null.");
		if (rules.size() == 0) {
			return EMPTY;
		}
		else if (rules.size() == 1) {
			return rules.get(0);
		}
		return new FirstOfRule<>(rules);
	}
	
	/**
	 * A rule that matches its sub rule optionally. In other words, a rule that repeats its sub rule zero or one time.
	 * 
	 * @param rule
	 *            the sub rule to match optionally
	 * @return a rule
	 */
	protected Rule<T> optional(final Rule<T> rule) {
		return repeat(rule).times(0, 1);
	}
	
	/**
	 * A rule that matches its sub rule zero or more times.
	 * 
	 * @param rule
	 *            the sub rule to repeat
	 * @return a rule
	 */
	protected Rule<T> zeroOrMore(final Rule<T> rule) {
		return repeat(rule).min(0);
	}
	
	/**
	 * A rule that matches its sub rule one or more times.
	 * 
	 * @param rule
	 *            the sub rule to repeat
	 * @return a rule
	 */
	protected Rule<T> oneOrMore(final Rule<T> rule) {
		return repeat(rule).min(1);
	}
	
	/**
	 * A rule builder for a repeat rule.
	 * 
	 * @param rule
	 *            the sub rule to repeat
	 * @return a repeat rule builder
	 */
	protected RepeatRuleBuilder<T> repeat(final Rule<T> rule) {
		return new RepeatRuleBuilder<>(checkNotNull(rule, "A 'rule' must not be null."));
	}
	
	/**
	 * A predicate rule that tests if its sub rule matches.
	 * 
	 * @param rule
	 *            the sub rule to test
	 * @return a rule
	 */
	protected Rule<T> test(final Rule<T> rule) {
		return new TestRule<>(checkNotNull(rule, "A 'rule' must not be null."));
	}
	
	/**
	 * A predicate rule that tests if its sub rule does not match.
	 * 
	 * @param rule
	 *            the sub rule to test
	 * @return a rule
	 */
	protected Rule<T> testNot(final Rule<T> rule) {
		return new TestNotRule<>(checkNotNull(rule, "A 'rule' must not be null."));
	}
	
	/**
	 * A rule that runs an action.
	 * 
	 * @param action
	 *            the action to run
	 * @return a rule
	 */
	protected Rule<T> action(final Action<T> action) {
		return new ActionRule<>(checkNotNull(action, "An 'action' must not be null."));
	}
	
	/**
	 * A rule that executes a command.
	 * 
	 * @param command
	 *            the command to execute
	 * @return a rule
	 */
	protected Rule<T> command(final Command<T> command) {
		checkNotNull(command, "A 'command' must not be null.");
		return action(command.toAction());
	}
	
	/**
	 * A rule that runs an action. The action is skipped if the rule is run inside a predicate rule.
	 * 
	 * @param action
	 *            the skippable action to run
	 * @return a rule
	 */
	protected Rule<T> skippableAction(final Action<T> action) {
		return new ActionRule<>(checkNotNull(action, "An 'action' must not be null."), true);
	}
	
	/**
	 * A rule that executes a command. The command is skipped if the rule is run inside a predicate rule.
	 * 
	 * @param command
	 *            the command to execute
	 * @return a rule
	 */
	protected Rule<T> skippableCommand(final Command<T> command) {
		checkNotNull(command, "A 'command' must not be null.");
		return skippableAction(command.toAction());
	}
	
	/**
	 * Posts a event to the parser's event bus. Note that the event object is constructed at parser create time.
	 * 
	 * @param event
	 *            the event to post
	 * @return a rule
	 */
	protected Rule<T> post(final Object event) {
		checkNotNull(event, "An 'event' must not be null.");
		return skippableCommand(ctx -> ctx.post(event));
	}
	
	/**
	 * Posts a event to the parser's event bus. Note that the event object is supplied by an event supplier at parser
	 * run time which has access to the parser context.
	 * 
	 * @param supplier
	 *            an event supplier that is called when the rule is run
	 * @return a rule
	 */
	protected Rule<T> post(final EventSupplier<T> supplier) {
		checkNotNull(supplier, "A 'supplier' must not be null.");
		return skippableCommand(ctx -> ctx.post(supplier.supply(ctx)));
	}
	
	/**
	 * Pops the top level element from the stack.
	 * 
	 * @return a rule
	 */
	protected Rule<T> pop() {
		return action(ctx -> {
			ctx.getStack().pop();
			return true;
		});
	}
	
	/**
	 * Pops an element from the stack at a given position.
	 * 
	 * @param down
	 *            number of elements on the stack to skip
	 * @return a rule
	 */
	protected Rule<T> pop(final int down) {
		return action(ctx -> {
			ctx.getStack().pop(down);
			return true;
		});
	}
	
	/**
	 * Replaces the element on top of the stack. Note that the value is constructed at parser create time.
	 * 
	 * @param value
	 *            a replacement value
	 * @return a rule
	 */
	protected Rule<T> poke(final T value) {
		return action(ctx -> {
			ctx.getStack().poke(value);
			return true;
		});
	}
	
	/**
	 * Replaces an element in the stack at a given position. Note that the value is constructed at parser create time.
	 * 
	 * @param down
	 *            number of elements on the stack to skip
	 * @param value
	 *            a replacement value
	 * @return a rule
	 */
	protected Rule<T> poke(final int down, final T value) {
		return action(ctx -> {
			ctx.getStack().poke(down, value);
			return true;
		});
	}
	
	/**
	 * Replaces an element in the stack. Note that the value is supplied by a value supplier at parser run time which
	 * has access to the parser context.
	 * 
	 * @param supplier
	 *            a replacement value supplier
	 * @return a rule
	 */
	protected Rule<T> poke(final ValueSupplier<T> supplier) {
		checkNotNull(supplier, "A 'supplier' must not be null.");
		return action(ctx -> {
			ctx.getStack().poke(supplier.supply(ctx));
			return true;
		});
	}
	
	/**
	 * Replaces an element in the stack at a given position. Note that the value is supplied by a value supplier at
	 * parser run time which has access to the parser context.
	 * 
	 * @param down
	 *            number of elements on the stack to skip
	 * @param supplier
	 *            a replacement value supplier
	 * @return a rule
	 */
	protected Rule<T> poke(final int down, final ValueSupplier<T> supplier) {
		checkNotNull(supplier, "A 'supplier' must not be null.");
		return action(ctx -> {
			ctx.getStack().poke(down, supplier.supply(ctx));
			return true;
		});
	}
	
	/**
	 * Pushes a new element onto stack. Note that the value is constructed at parser create time.
	 * 
	 * @param value
	 *            a value
	 * @return a rule
	 */
	protected Rule<T> push(final T value) {
		return command(ctx -> ctx.getStack().push(value));
	}
	
	/**
	 * Pushes a new element onto stack. Note that the value is supplied by a value supplier at parser run time which has
	 * access to the parser context.
	 * 
	 * @param supplier
	 *            a value supplier
	 * @return a rule
	 */
	protected Rule<T> push(final ValueSupplier<T> supplier) {
		checkNotNull(supplier, "A 'supplier' must not be null.");
		return command(ctx -> ctx.getStack().push(supplier.supply(ctx)));
	}
	
	/**
	 * Duplicates the top stack element.
	 * 
	 * @return a rule
	 */
	protected Rule<T> dup() {
		return command(ctx -> ctx.getStack().dup());
	}
	
	/**
	 * Swaps the two top stack elements.
	 * 
	 * @return a rule
	 */
	protected Rule<T> swap() {
		return command(ctx -> ctx.getStack().swap());
	}
	
	/**
	 * Pops the top level element from the stack. This method may be called by an action or command where the action.
	 * context is available.
	 * 
	 * @param context
	 *            an action context
	 * @return the popped element
	 */
	protected final T pop(final ActionContext<T> context) {
		checkNotNull(context, "A 'context' must not be null.");
		return context.getStack().pop();
	}
	
	/**
	 * Pops an element from the stack at a given position. This method may be called by an action or command where the
	 * action
	 * 
	 * @param down
	 *            number of elements on the stack to skip
	 * @param context
	 *            an action context
	 * @return the popped element
	 */
	protected final T pop(final int down, final ActionContext<T> context) {
		checkNotNull(context, "A 'context' must not be null.");
		return context.getStack().pop(down);
	}
	
	/**
	 * Pops and casts the top level element from the stack. This method may be called by an action or command where the
	 * action. context is available.
	 * 
	 * @param <U>
	 *            a sub type of T
	 * @param clazz
	 *            the the type to cast the element to
	 * @param context
	 *            an action context
	 * @return the popped element
	 */
	protected final <U extends T> U popAs(final Class<U> clazz, final ActionContext<T> context) {
		checkNotNull(clazz, "A 'clazz' must not be null.");
		checkNotNull(context, "A 'context' must not be null.");
		return context.getStack().popAs(clazz);
	}
	
	/**
	 * Pops and casts an element from the stack at a given position. This method may be called by an action or command
	 * where the action. context is available.
	 * 
	 * @param <U>
	 *            a sub type of T
	 * @param clazz
	 *            the the type to cast the element to
	 * @param down
	 *            number of elements on the stack to skip
	 * @param context
	 *            an action context
	 * @return the popped element
	 */
	protected final <U extends T> U popAs(final Class<U> clazz, final int down, final ActionContext<T> context) {
		checkNotNull(clazz, "A 'clazz' must not be null.");
		checkNotNull(context, "A 'context' must not be null.");
		return context.getStack().popAs(down, clazz);
	}
	
	/**
	 * Peeks the top level element from the stack. This method may be called by an action or command where the action.
	 * context is available.
	 * 
	 * @param context
	 *            an action context
	 * @return the peeked element
	 */
	protected final T peek(final ActionContext<T> context) {
		checkNotNull(context, "A 'context' must not be null.");
		return context.getStack().peek();
	}
	
	/**
	 * Peeks an element from the stack at a given position. This method may be called by an action or command where the
	 * action
	 * 
	 * @param down
	 *            number of elements on the stack to skip
	 * @param context
	 *            an action context
	 * @return the peeked element
	 */
	protected final T peek(final int down, final ActionContext<T> context) {
		checkNotNull(context, "A 'context' must not be null.");
		return context.getStack().peek(down);
	}
	
	/**
	 * Peeks and casts the top level element from the stack. This method may be called by an action or command where the
	 * action. context is available.
	 * 
	 * @param <U>
	 *            a sub type of T
	 * @param clazz
	 *            the the type to cast the element to
	 * @param context
	 *            an action context
	 * @return the peeked element
	 */
	protected final <U extends T> U peekAs(final Class<U> clazz, final ActionContext<T> context) {
		checkNotNull(clazz, "A 'clazz' must not be null.");
		checkNotNull(context, "A 'context' must not be null.");
		return context.getStack().peekAs(clazz);
	}
	
	/**
	 * Peeks and casts an element from the stack at a given position. This method may be called by an action or command
	 * where the action. context is available.
	 * 
	 * @param <U>
	 *            a sub type of T
	 * @param clazz
	 *            the the type to cast the element to
	 * @param down
	 *            number of elements on the stack to skip
	 * @param context
	 *            an action context
	 * @return the peeked element
	 */
	protected final <U extends T> U peekAs(final Class<U> clazz, final int down, final ActionContext<T> context) {
		checkNotNull(clazz, "A 'clazz' must not be null.");
		checkNotNull(context, "A 'context' must not be null.");
		return context.getStack().peekAs(down, clazz);
	}
	
	
	protected final Rule<T> EMPTY = new EmptyRule<>();
	protected final Rule<T> NEVER = new NeverRule<>();
	protected final Rule<T> EOI = new EndOfInputRule<>();
	protected final Rule<T> ANY_CHAR = new CharPredicateRule<>(CharMatcher.any());
	protected final Rule<T> ANY_CODEPOINT = new CodePointPredicateRule<>(UCharacter::isLegal);
	protected final Rule<T> ASCII = new CharPredicateRule<>(CharMatcher.ascii());
	protected final Rule<T> BMP = new CodePointPredicateRule<>(UCharacter::isBMP);
	protected final Rule<T> DIGIT = new CodePointPredicateRule<>(UCharacter::isDigit);
	protected final Rule<T> JAVA_IDENTIFIER_START = new CodePointPredicateRule<>(Character::isJavaIdentifierStart);
	protected final Rule<T> JAVA_IDENTIFIER_PART = new CodePointPredicateRule<>(Character::isJavaIdentifierPart);
	protected final Rule<T> LETTER = new CodePointPredicateRule<>(UCharacter::isLetter);
	protected final Rule<T> LETTER_OR_DIGIT = new CodePointPredicateRule<>(UCharacter::isLetterOrDigit);
	protected final Rule<T> PRINTABLE = new CodePointPredicateRule<>(UCharacter::isPrintable);
	protected final Rule<T> SPACE_CHAR = new CodePointPredicateRule<>(UCharacter::isSpaceChar);
	protected final Rule<T> WHITESPACE = new CodePointPredicateRule<>(UCharacter::isWhitespace);
	protected final Rule<T> CR = new CharPredicateRule<>('\r');
	protected final Rule<T> LF = new CharPredicateRule<>('\n');
	protected final Rule<T> CRLF = new StringRule<>("\r\n");
	
}

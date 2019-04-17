package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.util.BytesTrie.Result;
import com.ibm.icu.util.CharsTrie;
import com.ibm.icu.util.CharsTrieBuilder;
import com.ibm.icu.util.StringTrieBuilder.Option;
import com.mpe85.grampa.rule.RuleContext;

/**
 * A trie (prefix tree) rule implementation that matches the input against a dictionary.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class TrieRule<T> extends AbstractRule<T> {
	
	private final CharsTrie trie;
	private final boolean ignoreCase;
	
	/**
	 * C'tor. Constructs a case-sensitive trie.
	 * 
	 * @param strings
	 *            a variable number of strings
	 */
	public TrieRule(final String... strings) {
		this(false, strings);
	}
	
	/**
	 * C'tor. Constructs a case-sensitive trie (case-sensitive or case-insensitive).
	 * 
	 * @param ignoreCase
	 *            if true, the case of the strings is ignored
	 * @param strings
	 *            a variable number of strings
	 */
	public TrieRule(final boolean ignoreCase, final String... strings) {
		this(Sets.newHashSet(strings), ignoreCase);
	}
	
	/**
	 * C'tor. Constructs a case-sensitive trie.
	 * 
	 * @param strings
	 *            a set of strings
	 */
	public TrieRule(final Set<String> strings) {
		this(strings, false);
	}
	
	/**
	 * C'tor. Constructs a case-sensitive trie (case-sensitive or case-insensitive).
	 * 
	 * @param ignoreCase
	 *            if true, the case of the strings is ignored
	 * @param strings
	 *            a set of strings
	 */
	public TrieRule(
			final Set<String> strings,
			final boolean ignoreCase) {
		final CharsTrieBuilder builder = new CharsTrieBuilder();
		strings.forEach(s -> {
			checkNotNull(s, "A 'string' must not be null.");
			builder.add(ignoreCase ? UCharacter.toLowerCase(s) : s, 0);
		});
		trie = builder.build(Option.FAST);
		this.ignoreCase = ignoreCase;
	}
	
	/**
	 * Returns all strings inside the trie.
	 * 
	 * @return a set of strings
	 */
	public Set<String> getStrings() {
		return Streams.stream(trie.iterator())
				.map(e -> e.chars.toString())
				.collect(Collectors.toSet());
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		int longestMatch = 0;
		final int[] codePoints = context.getRestOfInput().codePoints().toArray();
		for (int i = 0; i < codePoints.length; i++) {
			final Result result = trie.next(ignoreCase ? UCharacter.toLowerCase(codePoints[i]) : codePoints[i]);
			if (result == Result.FINAL_VALUE || result == Result.INTERMEDIATE_VALUE) {
				longestMatch = i + 1;
			}
			if (result == Result.FINAL_VALUE || result == Result.NO_MATCH) {
				break;
			}
		}
		trie.reset();
		return longestMatch > 0 && context.advanceIndex(longestMatch);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getStrings(), ignoreCase);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final TrieRule<?> other = (TrieRule<?>) obj;
			return super.equals(other)
					&& Objects.equals(getStrings(), other.getStrings())
					&& Objects.equals(ignoreCase, other.ignoreCase);
		}
		return false;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("strings", getStrings())
				.add("ignoreCase", ignoreCase);
	}
	
}

package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import java.util.Set;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.Sets;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.util.CharsTrie;
import com.ibm.icu.util.CharsTrieBuilder;
import com.ibm.icu.util.StringTrieBuilder.Option;
import com.mpe85.grampa.rule.RuleContext;

import one.util.streamex.StreamEx;

public class TrieRule<T> extends AbstractRule<T> {
	
	public TrieRule(final String... strings) {
		this(false, strings);
	}
	
	public TrieRule(final boolean ignoreCase, final String... strings) {
		this(Sets.newHashSet(strings), ignoreCase);
	}
	
	public TrieRule(final Set<String> strings) {
		this(strings, false);
	}
	
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
	
	public Set<String> getStrings() {
		return StreamEx.of(trie.iterator()).map(e -> e.chars).map(String.class::cast).toSet();
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		int longestMatch = 0;
		final int[] codePoints = context.getRestOfInput().codePoints().toArray();
		loop: for (int i = 0; i < codePoints.length; i++) {
			switch (trie.next(ignoreCase ? UCharacter.toLowerCase(codePoints[i]) : codePoints[i])) {
				case FINAL_VALUE:
					longestMatch = i + 1;
					break loop;
				case INTERMEDIATE_VALUE:
					longestMatch = i + 1;
					continue loop;
				case NO_MATCH:
					break loop;
				case NO_VALUE:
					continue loop;
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
	
	
	private final CharsTrie trie;
	private final boolean ignoreCase;
}

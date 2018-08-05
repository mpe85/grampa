package com.mpe85.grampa.rule.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.googlecode.concurrenttrees.radix.node.concrete.SmartArrayBasedNodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree;
import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.mpe85.grampa.rule.RuleContext;

public class TrieRule<T> extends AbstractRule<T> {
	
	public TrieRule(final String... strings) {
		this(Sets.newHashSet(strings));
	}
	
	public TrieRule(final Collection<String> strings) {
		this.strings.addAll(strings);
		strings.forEach(s -> trie.put(
				Preconditions.checkNotNull(s, "A 'string' must not be null."),
				VoidValue.SINGLETON));
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		final CharSequence longestKey = trie.getLongestKeyPrefixing(context.getRestOfInput());
		return longestKey != null && context.advanceIndex(longestKey.length());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), strings);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final TrieRule<?> other = (TrieRule<?>) obj;
			return super.equals(other)
					&& Objects.equals(strings, other.strings);
		}
		return false;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("strings", strings);
	}
	
	
	private final Set<String> strings = new HashSet<>();
	private final InvertedRadixTree<VoidValue> trie =
			new ConcurrentInvertedRadixTree<>(new SmartArrayBasedNodeFactory());
	
}

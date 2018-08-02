package com.mpe85.grampa.rule.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.googlecode.concurrenttrees.radix.node.concrete.SmartArrayBasedNodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree;
import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.mpe85.grampa.rule.RuleContext;

import one.util.streamex.StreamEx;

public class TrieRule<T> extends AbstractRule<T> {
	
	public TrieRule(final String... strings) {
		this(Sets.newHashSet(strings));
	}
	
	public TrieRule(final Collection<String> strings) {
		this.strings.addAll(strings);
		StreamEx.of(strings)
				.forEach(s -> trie.put(
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
	
	private final Set<String> strings = new HashSet<>();
	private final InvertedRadixTree<VoidValue> trie =
			new ConcurrentInvertedRadixTree<>(new SmartArrayBasedNodeFactory());
	
}

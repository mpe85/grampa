package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import java.util.Set;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.Sets;
import com.googlecode.concurrenttrees.radix.node.concrete.SmartArrayBasedNodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree;
import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.mpe85.grampa.rule.RuleContext;

public class StringsRule<T> extends AbstractRule<T> {
	
	public StringsRule(final String... strings) {
		this(Sets.newHashSet(strings));
	}
	
	public StringsRule(final Set<String> strings) {
		this.strings = strings;
		strings.forEach(s -> trie.put(
				checkNotNull(s, "A 'string' must not be null."),
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
			final StringsRule<?> other = (StringsRule<?>) obj;
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
	
	
	private final Set<String> strings;
	private final InvertedRadixTree<VoidValue> trie =
			new ConcurrentInvertedRadixTree<>(new SmartArrayBasedNodeFactory());
	
}

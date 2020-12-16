package com.mpe85.grampa.rule.impl;

import com.mpe85.grampa.rule.RuleContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CharPredicateRuleTest {

    @Test
    public void equalsHashCodeToString() {
        final Predicate<Character> pred = cp -> cp == 'a';

        final CharPredicateRule<String> rule1 = new CharPredicateRule<>(pred::test);
        final CharPredicateRule<String> rule2 = new CharPredicateRule<>(pred::test);
        final CharPredicateRule<String> rule3 = new CharPredicateRule<>('a');

        assertTrue(rule1.equals(rule2));
        assertFalse(rule1.equals(rule3));
        assertFalse(rule1.equals(new Object()));

        assertEquals(rule1.hashCode(), rule2.hashCode());
        assertNotEquals(rule1.hashCode(), rule3.hashCode());

        assertEquals("CharPredicateRule{#children=0}", rule1.toString());
        assertEquals("CharPredicateRule{#children=0}", rule2.toString());
        assertEquals("CharPredicateRule{#children=0}", rule3.toString());
    }

    @Test
    public void match(@Mock final RuleContext<String> ctx) {
        Mockito.when(ctx.isAtEndOfInput()).thenReturn(false);
        Mockito.when(ctx.getCurrentChar()).thenReturn('a');
        Mockito.when(ctx.advanceIndex(1)).thenReturn(true);

        final CharPredicateRule<String> rule1 = new CharPredicateRule<>('a');
        final CharPredicateRule<String> rule2 = new CharPredicateRule<>('b');

        assertTrue(rule1.match(ctx));
        assertFalse(rule2.match(ctx));
    }

}

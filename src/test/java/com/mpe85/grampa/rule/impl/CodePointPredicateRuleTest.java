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
public class CodePointPredicateRuleTest {

    @Test
    public void equalsHashCodeToString() {
        final Predicate<Integer> pred = cp -> cp == 'a';

        final CodePointPredicateRule<String> rule1 = new CodePointPredicateRule<>(pred::test);
        final CodePointPredicateRule<String> rule2 = new CodePointPredicateRule<>(pred::test);
        final CodePointPredicateRule<String> rule3 = new CodePointPredicateRule<>('a');

        assertTrue(rule1.equals(rule2));
        assertFalse(rule1.equals(rule3));
        assertFalse(rule1.equals(new Object()));

        assertEquals(rule1.hashCode(), rule2.hashCode());
        assertNotEquals(rule1.hashCode(), rule3.hashCode());

        assertEquals("CodePointPredicateRule{#children=0}", rule1.toString());
        assertEquals("CodePointPredicateRule{#children=0}", rule2.toString());
        assertEquals("CodePointPredicateRule{#children=0}", rule3.toString());
    }

    @Test
    public void match(@Mock final RuleContext<String> ctx) {
        Mockito.when(ctx.isAtEndOfInput()).thenReturn(false);
        Mockito.when(ctx.getCurrentCodePoint()).thenReturn((int) 'a');
        Mockito.when(ctx.advanceIndex(1)).thenReturn(true);

        final CodePointPredicateRule<String> rule1 = new CodePointPredicateRule<>('a');
        final CodePointPredicateRule<String> rule2 = new CodePointPredicateRule<>('b');

        assertTrue(rule1.match(ctx));
        assertFalse(rule2.match(ctx));
    }

}

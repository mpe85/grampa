package com.github.mpe85.grampa;

import com.github.mpe85.grampa.grammar.Grammar;
import com.github.mpe85.grampa.grammar.impl.AbstractGrammar;
import com.github.mpe85.grampa.parser.ParseResult;
import com.github.mpe85.grampa.parser.Parser;
import com.github.mpe85.grampa.rule.Rule;
import com.github.mpe85.grampa.rule.impl.StringRules;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JavaTests {

    public static class MyGrammar extends AbstractGrammar<String> {

        @NotNull
        @Override
        public Rule<String> root() {
            return StringRules.toRule("my");
        }
    }

    @Test
    public void createGrammar() {
        Grammar<String> grammar = Grampa.createGrammar(MyGrammar.class);
        Parser<String> parser = new Parser<>(grammar);
        ParseResult<String> result = parser.run("mygrammar");
        assertTrue(result.getMatched());
        assertFalse(result.getMatchedEntireInput());
        assertEquals("my", result.getMatchedInput());
        assertEquals("grammar", result.getRestOfInput());
    }

}

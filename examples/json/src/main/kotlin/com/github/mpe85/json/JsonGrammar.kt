package com.github.mpe85.json

import com.github.mpe85.grampa.grammar.AbstractGrammar
import com.github.mpe85.grampa.rule.Rule

open class JsonGrammar : AbstractGrammar<JsonElement>() {
    override fun start(): Rule<JsonElement> = sequence(jsonObject(), eoi())

    open fun jsonString(): Rule<JsonElement> = sequence(
        doubleQuote(),
        zeroOrMore(
            sequence(
                testNot(doubleQuote()),
                anyChar()
            )
        ),
        push { JsonString(checkNotNull(it.previousMatch?.toString())) },
        doubleQuote(),
        whitespaces()
    )

    open fun jsonNumber(): Rule<JsonElement> = sequence(
        sequence(
            sequence(
                optional(unaryMinus()),
                oneOrMore(digit()),
                optional(
                    sequence(
                        char('.'),
                        oneOrMore(digit())
                    )
                )
            ),
            push { JsonNumber(checkNotNull(it.previousMatch?.toString()?.toBigDecimal())) }
        ),
        whitespaces()
    )

    open fun jsonNull(): Rule<JsonElement> = sequence(
        string("null"),
        push(JsonNull),
        whitespaces()
    )

    open fun jsonBoolean(): Rule<JsonElement> = sequence(
        strings("true", "false"),
        push { JsonBoolean(checkNotNull(it.previousMatch?.toString()?.toBoolean())) },
        whitespaces()
    )

    open fun jsonElement(): Rule<JsonElement> = choice(
        jsonString(),
        jsonNumber(),
        jsonNull(),
        jsonBoolean(),
        jsonObject(),
        jsonArray()
    )

    open fun jsonObject(): Rule<JsonElement> = sequence(
        curlyBracketLeft(),
        sequence(
            jsonString(),
            colon(),
            jsonElement(),
            push { JsonObject(mutableMapOf((pop(1, it) as JsonString).value to pop(it))) }
        ),
        zeroOrMore(
            sequence(
                comma(),
                jsonString(),
                colon(),
                jsonElement(),
                command { (peek(2, it) as JsonObject).properties[(pop(1, it) as JsonString).value] = pop(it) }
            )
        ),
        curlyBracketRight()
    )

    open fun jsonArray(): Rule<JsonElement> = sequence(
        squareBracketLeft(),
        jsonElement(),
        push { JsonArray(mutableListOf(pop(it))) },
        zeroOrMore(
            sequence(
                comma(),
                jsonElement(),
                command { (peek(1, it) as JsonArray).elements.add(pop(it)) })
        ),
        squareBracketRight()
    )

    open fun whitespaces(): Rule<JsonElement> = zeroOrMore(whitespace())

    open fun doubleQuote() = char('"')
    open fun unaryMinus() = sequence(char('-'), whitespaces())
    open fun curlyBracketLeft() = sequence(char('{'), whitespaces())
    open fun curlyBracketRight() = sequence(char('}'), whitespaces())
    open fun squareBracketLeft() = sequence(char('['), whitespaces())
    open fun squareBracketRight() = sequence(char(']'), whitespaces())
    open fun colon() = sequence(char(':'), whitespaces())
    open fun comma() = sequence(char(','), whitespaces())
}

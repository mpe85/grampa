package com.github.mpe85.calculator

import com.github.mpe85.grampa.grammar.AbstractGrammar
import com.github.mpe85.grampa.rule.Rule

open class CalculatorGrammar : AbstractGrammar<Int>() {
    override fun start(): Rule<Int> = expression() + eoi()

    open fun expression(): Rule<Int> = term() + zeroOrMore(add() + term() + push { pop(it) + pop(it) })
    open fun term(): Rule<Int> = factor() + zeroOrMore(mul() + factor() + push { pop(it) * pop(it) })
    open fun factor(): Rule<Int> = lpar() + expression() + rpar() or integer()

    open fun add(): Rule<Int> = char('+') + whitespaces()
    open fun mul(): Rule<Int> = char('*') + whitespaces()
    open fun lpar(): Rule<Int> = char('(') + whitespaces()
    open fun rpar(): Rule<Int> = char(')') + whitespaces()
    open fun integer(): Rule<Int> =
        optional(char('-')) + oneOrMore(digit()) + push { it.previousMatch.toString().toInt() } + whitespaces()

    open fun whitespaces(): Rule<Int> = zeroOrMore(whitespace())
}

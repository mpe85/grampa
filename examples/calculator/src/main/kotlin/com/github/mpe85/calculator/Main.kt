package com.github.mpe85.calculator

import com.github.mpe85.grampa.createGrammar
import com.github.mpe85.grampa.parser.Parser

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val calcGrammar = CalculatorGrammar::class.createGrammar()
        val parser = Parser(calcGrammar)
        var result = parser.run("(1 + 2) * -3")
        println(result.stackTop)
        result = parser.run("1 + 2 * -3")
        println(result.stackTop)
        result = parser.run("1 + 2 * 3")
        println(result.stackTop)
    }
}

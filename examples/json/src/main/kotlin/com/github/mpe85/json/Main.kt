package com.github.mpe85.json

import com.github.mpe85.grampa.createGrammar
import com.github.mpe85.grampa.parser.Parser

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val calcGrammar = JsonGrammar::class.createGrammar()
        val parser = Parser(calcGrammar)
        val result = parser.run("{ \"foo\" : 42, \"bar\" : { \"active\" : true }, \"baz\" : [1,-2,3.67] }  ")
        println(result.matchedEntireInput)
        println(result.stackTop)

        result.stackTop?.accept(PrintVisitor())
    }
}

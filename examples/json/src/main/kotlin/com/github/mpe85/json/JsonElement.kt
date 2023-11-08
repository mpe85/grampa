package com.github.mpe85.json

sealed class JsonElement {
    abstract fun accept(visitor: JsonElementVisitor, depth: Int = 0)
}

data object JsonNull : JsonElement() {
    override fun accept(visitor: JsonElementVisitor, depth: Int) = visitor.visit(this, depth)
}

data class JsonString(val value: String) : JsonElement() {
    override fun accept(visitor: JsonElementVisitor, depth: Int) = visitor.visit(this, depth)
}

data class JsonNumber(val value: Number) : JsonElement() {
    override fun accept(visitor: JsonElementVisitor, depth: Int) = visitor.visit(this, depth)
}

data class JsonBoolean(val value: Boolean) : JsonElement() {
    override fun accept(visitor: JsonElementVisitor, depth: Int) = visitor.visit(this, depth)
}

data class JsonObject(val properties: MutableMap<String, JsonElement>) : JsonElement() {
    override fun accept(visitor: JsonElementVisitor, depth: Int) = visitor.visit(this, depth)
}

data class JsonArray(val elements: MutableList<JsonElement>) : JsonElement() {
    override fun accept(visitor: JsonElementVisitor, depth: Int) = visitor.visit(this, depth)
}

interface JsonElementVisitor {
    fun visit(jsonNull: JsonNull, depth: Int = 0)
    fun visit(string: JsonString, depth: Int = 0)
    fun visit(number: JsonNumber, depth: Int = 0)
    fun visit(boolean: JsonBoolean, depth: Int = 0)
    fun visit(obj: JsonObject, depth: Int = 0)
    fun visit(array: JsonArray, depth: Int = 0)
}

class PrintVisitor : JsonElementVisitor {
    override fun visit(jsonNull: JsonNull, depth: Int) = print("null")

    override fun visit(string: JsonString, depth: Int) = print("\"${string.value}\"")

    override fun visit(number: JsonNumber, depth: Int) = print(number.value)

    override fun visit(boolean: JsonBoolean, depth: Int) = print(boolean.value)

    override fun visit(obj: JsonObject, depth: Int) {
        println("{")
        obj.properties.asSequence().forEachIndexed { i, (key, value) ->
            if (i > 0) println(",")
            indent(depth + 1)
            print("\"$key\" : ")
            value.accept(this, depth + 1)
        }
        println()
        indent(depth)
        print("}")
    }

    override fun visit(array: JsonArray, depth: Int) {
        println("[")
        array.elements.asSequence().forEachIndexed { i, element ->
            if (i > 0) println(",")
            indent(depth + 1)
            element.accept(this, depth + 1)
        }
        println()
        indent(depth)
        print("]")
    }

    private fun indent(depth: Int) = print("\t".repeat(depth))
}

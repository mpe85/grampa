# Grampa

*A grammar parser library for Kotlin/JVM*

[![Latest Release](https://img.shields.io/github/release/mpe85/grampa/all.svg?label=Latest%20Release)](https://github.com/mpe85/grampa/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.mpe85/grampa.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.mpe85%22%20AND%20a:%22grampa%22)
[![Kotlin](https://img.shields.io/badge/kotlin-1.7-blue.svg?logo=kotlin&label=Kotlin)](http://kotlinlang.org)
[![Build](https://github.com/mpe85/grampa/actions/workflows/gradle.yml/badge.svg)](https://github.com/mpe85/grampa/actions/workflows/gradle.yml)
[![Coverage](https://codecov.io/gh/mpe85/grampa/branch/master/graph/badge.svg)](https://codecov.io/gh/mpe85/grampa)
[![License](https://img.shields.io/github/license/mpe85/grampa.svg?label=License)](https://github.com/mpe85/grampa/blob/master/LICENSE)

Grampa — short for **Gram**mar **pa**rser — is a library that allows you to define grammars completely in Kotlin source
code without any pre-processing phase (unlike other parser generators like ANTLR and JavaCC). Hence, there is no DSL to
be learned, the whole grammar definition is in one place (a Kotlin class) and can be changed and maintained very easily.
This library is inspired by [parboiled v1](https://github.com/sirthias/parboiled)
and [grappa](https://github.com/fge/grappa), but the focus is laid on simple and clean code without a lot of crazy and
complicated byte code manipulation.

## How to add to your build

Adding a dependency using Gradle (Groovy DSL):

```groovy
repositories {
    mavenCentral()
}
dependencies {
    implementation 'com.github.mpe85:grampa:1.1.0'
}
```

Adding a dependency using Gradle (Kotlin DSL):

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("com.github.mpe85:grampa:1.1.0")
}
```

Adding a dependency using Maven:

```xml

<dependency>
    <groupId>com.github.mpe85</groupId>
    <artifactId>grampa</artifactId>
    <version>1.1.0</version>
</dependency>
```

Adding a dependency using Ivy:

```xml

<dependency org="com.github.mpe85" name="grampa" rev="1.1.0"/>
```

## Getting started

Create a grammar class by extending the `AbstractGrammar` class:

```kotlin
open class LetterGrammar : AbstractGrammar<Unit>() {
    override fun start() = oneOrMore(letter()) + eoi()
}
```

Instantiate the grammar by using the `createGrammar` extension function:

```kotlin
val grammar = LetterGrammar::class.createGrammar()
```

Run the parser using the created grammar:

```kotlin
val parser: Parser<Unit> = Parser(grammar)
val result: ParseResult<Unit> = parser.run("abc")
```

See the [GitHub Wiki](https://github.com/mpe85/grampa/wiki) for a detailed documentation and examples.

## Other Kotlin targets

Since this library relies on byte code manipulation using [byte-buddy](https://bytebuddy.net) only the 'Kotlin/JVM'
target is supported. Other Kotlin targets (like 'Kotlin/JS' or one of the various native targets) are unsupported.

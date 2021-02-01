# Grampa: A grammar parser library for Kotlin

[![Latest release](https://img.shields.io/github/release/mpe85/grampa/all.svg)](https://github.com/mpe85/grampa/releases/latest)
[![Download](https://api.bintray.com/packages/mpe85/maven/grampa/images/download.svg)](https://bintray.com/mpe85/maven/grampa/_latestVersion)
[![Kotlin](https://img.shields.io/badge/kotlin-1.4-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Build](https://travis-ci.com/mpe85/grampa.svg?branch=master)](https://travis-ci.com/mpe85/grampa)
[![Coverage](https://codecov.io/gh/mpe85/grampa/branch/master/graph/badge.svg)](https://codecov.io/gh/mpe85/grampa)
[![License](https://img.shields.io/github/license/mpe85/grampa.svg)](https://github.com/mpe85/grampa/blob/master/LICENSE)

Grampa — short for **Gra**mmar **pa**rser — is a library that allows you to define grammars completely in Kotlin source
code without any pre-processing phase (unlike other parser generators like ANTLR and JavaCC). Hence there is no DSL to
be learned, the whole grammar definition is in one place (a Kotlin class) and can be changed and maintained very easily.
This library is inspired by [parboiled](https://github.com/sirthias/parboiled)
and [grappa](https://github.com/fge/grappa), but the focus is laid on simple and clean code without a lot of crazy and
complicated byte code manipulation.

## How to add to your build

Adding a dependency using Gradle (Groovy DSL):

```groovy
repositories {
    jcenter()
}
dependencies {
    implementation 'com.mpe85:grampa:0.9.2'
}
```

Adding a dependency using Gradle (Kotlin DSL):

```kotlin
repositories {
    jcenter()
}
dependencies {
    implementation("com.mpe85:grampa:0.9.2")
}
```

Adding a dependency using Maven:

```xml

<dependency>
    <groupId>com.mpe85</groupId>
    <artifactId>grampa</artifactId>
    <version>0.9.2</version>
    <type>pom</type>
</dependency>
```

Adding a dependency using Ivy:

```xml

<dependency org='com.mpe85' name='grampa' rev='0.9.2'>
    <artifact name='grampa' ext='pom'></artifact>
</dependency>
```

## Getting started

Create a grammar class by extending the `AbstractGrammar` class:

```kotlin
open class LetterGrammar : AbstractGrammar<Unit>() {
    override fun root() = oneOrMore(letter()) + eoi()
}
```

Instantiate the grammar by using the `createGrammar()` extension function:

```kotlin
val grammar = LetterGrammar::class.createGrammar()
```

Run the parser using the created grammar:

```kotlin
val parser: Parser<Unit> = Parser(grammar)
val result: ParseResult<Unit> = parser.run("abc")
```

# Grampa: A grammar parser library for Java

Grampa -- short for **Gra**mmar **pa**rser -- is a library that allows you to define grammars completely in Java source code without any pre-processing phase (unlike other parser generators like ANTLR and JavaCC).
Hence there is no DSL to be learned, the whole grammar definition is in one place (a Java class) and can be changed and maintained very easily.
This library is inspired by [parboiled](https://github.com/sirthias/parboiled) and [grappa](https://github.com/fge/grappa),
but the focus is laid on simple and clean code without a lot of crazy and complicated byte code manipulation.

## Versions

* first pre-release: **0.9.0**; requires **Java 8**

## How to add to your build

To add a dependency using Gradle:

```groovy
dependencies {
	implementation 'com.mpe85:grampa:0.9.0'
}
```

## Examples

Create a parser class by extending the AbstractParser class:

```java
public class LetterParser extends AbstractParser<Void> {
	@Override
	public Rule<Void> root() {
		return sequence(
				oneOrMore(letter()),
				eoi());
	}
}
```

Instantiate the parser by using the static createParser method:

```java
LetterParser parser = Grampa.createParser(LetterParser.class);
```

Run the parser using the DefaultParseRunner:

```java
DefaultParseRunner<Void> runner = new DefaultParseRunner<>(parser);
ParseResult<Void> result = runner.run("abc");
```

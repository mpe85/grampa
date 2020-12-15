package com.mpe85.grampa

import com.google.common.reflect.Invokable
import com.mpe85.grampa.exception.ParserCreateException
import com.mpe85.grampa.intercept.RuleMethodInterceptor
import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.rule.Rule
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.stream.Stream
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers

/**
 * Main class containing static methods for parser creation
 *
 * @author mpe85
 */
object Grampa {

  /**
   * Creates a new parser instance using the given parser class. The parser class must have a no-args c'tor which will
   * be called by this method.
   *
   * @param U the type of the parser
   * @param T the type of the stack elements
   * @param parserClass a parser class
   * @return a parser instance
   */
  @JvmStatic
  fun <U : Parser<T>, T> createParser(parserClass: Class<U>): U {
    return try {
      createParserSubClass(parserClass).getDeclaredConstructor().newInstance()
    } catch (ex: InstantiationException) {
      throw ParserCreateException("Failed to create new parser instance.", ex)
    } catch (ex: IllegalAccessException) {
      throw ParserCreateException("Failed to create new parser instance.", ex)
    } catch (ex: IllegalArgumentException) {
      throw ParserCreateException("Failed to create new parser instance.", ex)
    } catch (ex: InvocationTargetException) {
      throw ParserCreateException("Failed to create new parser instance.", ex)
    } catch (ex: NoSuchMethodException) {
      throw ParserCreateException("Failed to create new parser instance.", ex)
    } catch (ex: SecurityException) {
      throw ParserCreateException("Failed to create new parser instance.", ex)
    }
  }

  /**
   * Creates a new parser instance using the given parser class and c'tor parameter types. The parser class must have
   * a c'tor which matches the passed parameter types. The creation of the parser instance must be finalized by
   * calling [ParserCtor.withArgs] on the returned [ParserCtor].
   *
   * @param U the type of the parser
   * @param T the type of the stack elements
   * @param parserClass a parser class
   * @param ctorParamTypes the parameter types of the c'tor
   * @return a parser c'tor
   */
  @JvmStatic
  fun <U : Parser<T>, T> createParser(parserClass: Class<U>, vararg ctorParamTypes: Class<*>): ParserCtor<U> {
    return try {
      ParserCtor(createParserSubClass(parserClass).getConstructor(*ctorParamTypes))
    } catch (ex: NoSuchMethodException) {
      throw ParserCreateException("Failed to find constructor matching given parameter types.", ex)
    } catch (ex: SecurityException) {
      throw ParserCreateException("Failed to find constructor matching given parameter types.", ex)
    }
  }

  private fun <U : Parser<T>, T> createParserSubClass(parserClass: Class<U>): Class<out U> {
    validateParserClass(parserClass)
    return ByteBuddy()
      .subclass(parserClass)
      .method(ElementMatchers.returns(Rule::class.java))
      .intercept(MethodDelegation.withDefaultConfiguration().to(RuleMethodInterceptor<Any>()))
      .make()
      .load(parserClass.classLoader)
      .loaded
  }

  private fun validateParserClass(parserClass: Class<*>) {
    Stream.of(*parserClass.declaredMethods)
      .filter { m: Method -> m.returnType == Rule::class.java }
      .map { method -> Invokable.from(method) }
      .forEach { obj -> validateRuleMethod(obj) }
    if (parserClass.superclass != Any::class.java) {
      validateParserClass(parserClass.superclass)
    }
  }

  private fun validateRuleMethod(invokable: Invokable<*, Any>) {
    if (!invokable.isPublic && !invokable.isProtected) {
      throw ParserCreateException("Rule methods must be public or protected.")
    }
    if (invokable.isFinal && !isSafeVarArgsRuleMethod(invokable)) {
      throw ParserCreateException("Rule methods must not be final.")
    }
    if (invokable.isStatic) {
      throw ParserCreateException("Rule methods must not be static.")
    }
  }

  private fun isSafeVarArgsRuleMethod(invokable: Invokable<*, Any>): Boolean {
    return invokable.isVarArgs && invokable.isAnnotationPresent(SafeVarargs::class.java)
  }

  /**
   * A parser c'tor (intermediate class for fluent API). This class wraps a [Constructor] and offers a var args
   * method for calling that c'tor.
   *
   * @author mpe85
   *
   * @param T The type to which the c'tor belongs to
   * @param ctor the wrapped [Constructor]
   */
  class ParserCtor<T>(private val ctor: Constructor<out T>) {

    /**
     * Call the wrapped c'tor using the given c'tor args. The arguments must match the c'tor parameter list.
     *
     * @param args c'tor args
     * @return a new instance
     */
    fun withArgs(vararg args: Any): T {
      return try {
        ctor.newInstance(*args)
      } catch (ex: InstantiationException) {
        throw ParserCreateException("Failed to create new parser instance.", ex)
      } catch (ex: IllegalAccessException) {
        throw ParserCreateException("Failed to create new parser instance.", ex)
      } catch (ex: IllegalArgumentException) {
        throw ParserCreateException("Failed to create new parser instance.", ex)
      } catch (ex: InvocationTargetException) {
        throw ParserCreateException("Failed to create new parser instance.", ex)
      }
    }
  }
}
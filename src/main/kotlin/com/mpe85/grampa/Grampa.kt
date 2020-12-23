package com.mpe85.grampa

import com.mpe85.grampa.exception.ParserCreateException
import com.mpe85.grampa.intercept.RuleMethodInterceptor
import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.rule.Rule
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier.isFinal
import java.lang.reflect.Modifier.isProtected
import java.lang.reflect.Modifier.isPublic
import java.lang.reflect.Modifier.isStatic
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility.PROTECTED
import kotlin.reflect.KVisibility.PUBLIC
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.MethodDelegation.withDefaultConfiguration
import net.bytebuddy.matcher.ElementMatchers.returns

/**
 * Main class containing static methods for parser creation.
 *
 * @author mpe85
 */
object Grampa {

  /**
   * Create a new parser instance using the given parser class. The parser class must have a no-args c'tor which will
   * be called by this method.
   *
   * @param[U] The type of the parser
   * @param[T] The type of the stack elements
   * @param[parserClass] A parser class
   * @return A parser instance
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
   * Create a new parser instance using the given parser class and constructor parameter types.
   * The parser class must have a constructor which matches the passed parameter types.
   * The creation of the parser instance must be finalized by calling [ParserCtor.withArgs] on the returned [ParserCtor].
   *
   * @param[U] The type of the parser
   * @param[T] The type of the stack elements
   * @param[parserClass] A parser class
   * @param[ctorParamTypes] The parameter types of the constructor
   * @return A parser constructor
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
    parserClass.kotlin.validate()
    return ByteBuddy()
      .subclass(parserClass)
      .method(returns(Rule::class.java))
      .intercept(withDefaultConfiguration().to(RuleMethodInterceptor<Any>()))
      .make()
      .load(parserClass.classLoader)
      .loaded
  }

  private fun KClass<*>.validate() {
    declaredFunctions.asSequence().filter { it.returnType.jvmErasure.isSubclassOf(Rule::class) }.forEach {
      it.validate()
    }
    superclasses.forEach {
      it.validate()
    }
  }

  private fun KFunction<*>.validate() {
    this.javaMethod?.let { method ->
      if (!method.isPublic() && !method.isProtected()) {
        throw ParserCreateException("Rule methods must be public or protected.")
      }
      if (method.isFinal() && !method.isSafeVarArgsRuleMethod()) {
        throw ParserCreateException("Rule methods must not be final.")
      }
      if (method.isStatic()) {
        throw ParserCreateException("Rule methods must not be static.")
      }
    }
    if (!isPublic() && !isProtected()) {
      throw ParserCreateException("Rule functions must be public or protected.")
    }
    if (isFinal && !isSafeVarArgsRuleFunction()) {
      throw ParserCreateException("Rule functions must not be final.")
    }
  }

  private fun Method.isPublic() = isPublic(modifiers)
  private fun Method.isProtected() = isProtected(modifiers)
  private fun Method.isFinal() = isFinal(modifiers)
  private fun Method.isStatic() = isStatic(modifiers)
  private fun Method.isSafeVarArgsRuleMethod() = isVarArgs && isAnnotationPresent(SafeVarargs::class.java)

  private fun KFunction<*>.isPublic() = visibility == PUBLIC
  private fun KFunction<*>.isProtected() = visibility == PROTECTED
  private fun KFunction<*>.isSafeVarArgsRuleFunction() = parameters.any { it.isVararg } && hasAnnotation<SafeVarargs>()

  /**
   * A parser constructor (intermediate class for fluent API).
   * This class wraps a [Constructor] and offers a var args method for calling that constructor.
   *
   * @author mpe85
   *
   * @param[T] The type to which the constructor belongs to
   * @param[ctor] The wrapped [Constructor]
   */
  class ParserCtor<T>(private val ctor: Constructor<out T>) {

    /**
     * Call the wrapped constructor using the given constructor args.
     * The arguments must match the constructor parameter list.
     *
     * @param[args] Constructor arguments
     * @return A new instance
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
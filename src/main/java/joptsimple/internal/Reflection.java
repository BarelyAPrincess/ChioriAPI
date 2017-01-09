/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple.internal;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static joptsimple.internal.Classes.wrapperOf;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import joptsimple.ValueConverter;

/**
 * Helper methods for reflection.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public final class Reflection
{
	private static <V> ValueConverter<V> constructorConverter( Class<V> clazz )
	{
		try
		{
			return new ConstructorInvokingValueConverter<>( clazz.getConstructor( String.class ) );
		}
		catch ( NoSuchMethodException ignored )
		{
			return null;
		}
	}

	@SuppressWarnings( "unchecked" )
	public static <V> V convertWith( ValueConverter<V> converter, String raw )
	{
		return converter == null ? ( V ) raw : converter.convert( raw );
	}

	/**
	 * Finds an appropriate value converter for the given class.
	 *
	 * @param <V>
	 *             a constraint on the class object to introspect
	 * @param clazz
	 *             class to introspect on
	 * @return a converter method or constructor
	 */
	public static <V> ValueConverter<V> findConverter( Class<V> clazz )
	{
		Class<V> maybeWrapper = wrapperOf( clazz );

		ValueConverter<V> valueOf = valueOfConverter( maybeWrapper );
		if ( valueOf != null )
			return valueOf;

		ValueConverter<V> constructor = constructorConverter( maybeWrapper );
		if ( constructor != null )
			return constructor;

		throw new IllegalArgumentException( clazz + " is not a value type" );
	}

	/**
	 * Invokes the given constructor with the given arguments.
	 *
	 * @param <T>
	 *             constraint on the type of the objects yielded by the constructor
	 * @param constructor
	 *             constructor to invoke
	 * @param args
	 *             arguments to hand to the constructor
	 * @return the result of invoking the constructor
	 * @throws ReflectionException
	 *              in lieu of the gaggle of reflection-related exceptions
	 */
	public static <T> T instantiate( Constructor<T> constructor, Object... args )
	{
		try
		{
			return constructor.newInstance( args );
		}
		catch ( Exception ex )
		{
			throw reflectionException( ex );
		}
	}

	/**
	 * Invokes the given static method with the given arguments.
	 *
	 * @param method
	 *             method to invoke
	 * @param args
	 *             arguments to hand to the method
	 * @return the result of invoking the method
	 * @throws ReflectionException
	 *              in lieu of the gaggle of reflection-related exceptions
	 */
	public static Object invoke( Method method, Object... args )
	{
		try
		{
			return method.invoke( null, args );
		}
		catch ( Exception ex )
		{
			throw reflectionException( ex );
		}
	}

	private static boolean meetsConverterRequirements( Method method, Class<?> expectedReturnType )
	{
		int modifiers = method.getModifiers();
		return isPublic( modifiers ) && isStatic( modifiers ) && expectedReturnType.equals( method.getReturnType() );
	}

	private static RuntimeException reflectionException( Exception ex )
	{
		if ( ex instanceof IllegalArgumentException )
			return new ReflectionException( ex );
		if ( ex instanceof InvocationTargetException )
			return new ReflectionException( ex.getCause() );
		if ( ex instanceof RuntimeException )
			return ( RuntimeException ) ex;

		return new ReflectionException( ex );
	}

	private static <V> ValueConverter<V> valueOfConverter( Class<V> clazz )
	{
		try
		{
			Method valueOf = clazz.getMethod( "valueOf", String.class );
			if ( meetsConverterRequirements( valueOf, clazz ) )
				return new MethodInvokingValueConverter<>( valueOf, clazz );

			return null;
		}
		catch ( NoSuchMethodException ignored )
		{
			return null;
		}
	}

	private Reflection()
	{
		throw new UnsupportedOperationException();
	}
}

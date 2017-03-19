/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public final class Classes
{
	private static final Map<Class<?>, Class<?>> WRAPPERS = new HashMap<>( 13 );

	static
	{
		WRAPPERS.put( boolean.class, Boolean.class );
		WRAPPERS.put( byte.class, Byte.class );
		WRAPPERS.put( char.class, Character.class );
		WRAPPERS.put( double.class, Double.class );
		WRAPPERS.put( float.class, Float.class );
		WRAPPERS.put( int.class, Integer.class );
		WRAPPERS.put( long.class, Long.class );
		WRAPPERS.put( short.class, Short.class );
		WRAPPERS.put( void.class, Void.class );
	}

	/**
	 * Gives the "short version" of the given class name. Somewhat naive to inner classes.
	 *
	 * @param className
	 *             class name to chew on
	 * @return the short name of the class
	 */
	public static String shortNameOf( String className )
	{
		return className.substring( className.lastIndexOf( '.' ) + 1 );
	}

	/**
	 * Gives the primitive wrapper class for the given class. If the given class is not {@linkplain Class#isPrimitive() primitive}, returns the class itself.
	 *
	 * @param <T>
	 *             generic class type
	 * @param clazz
	 *             the class to check
	 * @return primitive wrapper type if {@code clazz} is primitive, otherwise {@code clazz}
	 */
	@SuppressWarnings( "unchecked" )
	public static <T> Class<T> wrapperOf( Class<T> clazz )
	{
		return clazz.isPrimitive() ? ( Class<T> ) WRAPPERS.get( clazz ) : clazz;
	}

	private Classes()
	{
		throw new UnsupportedOperationException();
	}
}

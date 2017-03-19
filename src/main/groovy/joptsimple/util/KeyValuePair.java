/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple.util;

import static joptsimple.internal.Strings.EMPTY;

/**
 * <p>
 * A simple string key/string value pair.
 * </p>
 *
 * <p>
 * This is useful as an argument type for options whose values take on the form {@code key=value}, such as JVM command line system properties.
 * </p>
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public final class KeyValuePair
{
	/**
	 * Parses a string assumed to be of the form {@code key=value} into its parts.
	 *
	 * @param asString
	 *             key-value string
	 * @return a key-value pair
	 * @throws NullPointerException
	 *              if {@code stringRepresentation} is {@code null}
	 */
	public static KeyValuePair valueOf( String asString )
	{
		int equalsIndex = asString.indexOf( '=' );
		if ( equalsIndex == -1 )
			return new KeyValuePair( asString, EMPTY );

		String aKey = asString.substring( 0, equalsIndex );
		String aValue = equalsIndex == asString.length() - 1 ? EMPTY : asString.substring( equalsIndex + 1 );

		return new KeyValuePair( aKey, aValue );
	}
	public final String key;

	public final String value;

	private KeyValuePair( String key, String value )
	{
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean equals( Object that )
	{
		if ( ! ( that instanceof KeyValuePair ) )
			return false;

		KeyValuePair other = ( KeyValuePair ) that;
		return key.equals( other.key ) && value.equals( other.value );
	}

	@Override
	public int hashCode()
	{
		return key.hashCode() ^ value.hashCode();
	}

	@Override
	public String toString()
	{
		return key + '=' + value;
	}
}

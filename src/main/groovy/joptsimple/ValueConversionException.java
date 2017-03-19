/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple;

/**
 * Thrown by {@link ValueConverter}s when problems occur in converting string values to other Java types.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class ValueConversionException extends RuntimeException
{
	private static final long serialVersionUID = -1L;

	/**
	 * Creates a new exception with the specified detail message.
	 *
	 * @param message
	 *             the detail message
	 */
	public ValueConversionException( String message )
	{
		this( message, null );
	}

	/**
	 * Creates a new exception with the specified detail message and cause.
	 *
	 * @param message
	 *             the detail message
	 * @param cause
	 *             the original exception
	 */
	public ValueConversionException( String message, Throwable cause )
	{
		super( message, cause );
	}
}

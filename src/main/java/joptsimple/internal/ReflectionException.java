/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple.internal;

/**
 * This unchecked exception wraps reflection-oriented exceptions.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class ReflectionException extends RuntimeException
{
	private static final long serialVersionUID = -2L;

	ReflectionException( Throwable cause )
	{
		super( cause );
	}
}

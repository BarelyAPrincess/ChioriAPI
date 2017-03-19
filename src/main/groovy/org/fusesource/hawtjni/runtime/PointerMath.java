/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.fusesource.hawtjni.runtime;

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class PointerMath
{
	private static final boolean bits32 = Library.getBitModel() == 32;

	final public static long add( long ptr, long n )
	{
		if ( bits32 )
			return ( int ) ( ptr + n );
		else
			return ptr + n;
	}
}

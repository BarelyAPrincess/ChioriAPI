/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.util;

public class Validation
{
	public static <T> T notNull( T var )
	{
		if ( var == null )
			throw new NullPointerException( "Variable can not be null!" );
		return var;
	}

	public static <T> T notNull( T var, String msg, Object... objs )
	{
		if ( var == null )
			throw new NullPointerException( String.format( msg, objs ) );
		return var;
	}
}

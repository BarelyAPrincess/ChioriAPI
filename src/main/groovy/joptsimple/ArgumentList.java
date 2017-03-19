/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple;

import static joptsimple.ParserRules.DOUBLE_HYPHEN;
import static joptsimple.ParserRules.HYPHEN_CHAR;

/**
 * <p>
 * Wrapper for an array of command line arguments.
 * </p>
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class ArgumentList
{
	private final String[] arguments;
	private int currentIndex;

	ArgumentList( String... arguments )
	{
		this.arguments = arguments.clone();
	}

	boolean hasMore()
	{
		return currentIndex < arguments.length;
	}

	String next()
	{
		return arguments[currentIndex++];
	}

	String peek()
	{
		return arguments[currentIndex];
	}

	void treatNextAsLongOption()
	{
		if ( HYPHEN_CHAR != arguments[currentIndex].charAt( 0 ) )
			arguments[currentIndex] = DOUBLE_HYPHEN + arguments[currentIndex];
	}
}

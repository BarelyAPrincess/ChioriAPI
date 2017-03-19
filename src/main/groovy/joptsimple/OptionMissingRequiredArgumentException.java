/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple;

import static java.util.Arrays.asList;

/**
 * Thrown when the option parser discovers options that require an argument, but are missing an argument.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class OptionMissingRequiredArgumentException extends OptionException
{
	private static final long serialVersionUID = -1L;

	OptionMissingRequiredArgumentException( OptionSpec<?> option )
	{
		super( asList( option ) );
	}

	@Override
	Object[] messageArguments()
	{
		return new Object[] {singleOptionString()};
	}
}

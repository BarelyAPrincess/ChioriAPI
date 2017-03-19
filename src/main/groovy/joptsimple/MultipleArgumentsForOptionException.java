/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple;

import static java.util.Collections.singleton;

/**
 * Thrown when asking an {@link OptionSet} for a single argument of an option when many have been specified.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class MultipleArgumentsForOptionException extends OptionException
{
	private static final long serialVersionUID = -1L;

	MultipleArgumentsForOptionException( OptionSpec<?> options )
	{
		super( singleton( options ) );
	}

	@Override
	Object[] messageArguments()
	{
		return new Object[] {singleOptionString()};
	}
}

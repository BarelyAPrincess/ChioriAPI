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
 * Thrown when a problem occurs converting an argument of an option from {@link String} to another type.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class OptionArgumentConversionException extends OptionException
{
	private static final long serialVersionUID = -1L;

	private final String argument;

	OptionArgumentConversionException( OptionSpec<?> options, String argument, Throwable cause )
	{
		super( singleton( options ), cause );

		this.argument = argument;
	}

	@Override
	Object[] messageArguments()
	{
		return new Object[] {argument, singleOptionString()};
	}
}

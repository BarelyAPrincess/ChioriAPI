/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple;

import static java.util.Collections.singletonList;

/**
 * Thrown when the option parser encounters an unrecognized option.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class UnrecognizedOptionException extends OptionException
{
	private static final long serialVersionUID = -1L;

	UnrecognizedOptionException( String option )
	{
		super( singletonList( option ) );
	}

	@Override
	Object[] messageArguments()
	{
		return new Object[] {singleOptionString()};
	}
}

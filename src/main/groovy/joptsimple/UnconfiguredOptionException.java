/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple;

import static java.util.Collections.singletonList;

import java.util.List;

/**
 * Thrown when an option parser refers to an option that is not in fact configured already on the parser.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class UnconfiguredOptionException extends OptionException
{
	private static final long serialVersionUID = -1L;

	UnconfiguredOptionException( List<String> options )
	{
		super( options );
	}

	UnconfiguredOptionException( String option )
	{
		this( singletonList( option ) );
	}

	@Override
	Object[] messageArguments()
	{
		return new Object[] {multipleOptionString()};
	}
}

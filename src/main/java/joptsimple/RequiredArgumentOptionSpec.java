/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple;

import java.util.List;

/**
 * Specification of an option that accepts a required argument.
 *
 * @param <V>
 *             represents the type of the arguments this option accepts
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class RequiredArgumentOptionSpec<V> extends ArgumentAcceptingOptionSpec<V>
{
	RequiredArgumentOptionSpec( List<String> options, String description )
	{
		super( options, true, description );
	}

	RequiredArgumentOptionSpec( String option )
	{
		super( option, true );
	}

	@Override
	protected void detectOptionArgument( OptionParser parser, ArgumentList arguments, OptionSet detectedOptions )
	{
		if ( !arguments.hasMore() )
			throw new OptionMissingRequiredArgumentException( this );

		addArguments( detectedOptions, arguments.next() );
	}
}

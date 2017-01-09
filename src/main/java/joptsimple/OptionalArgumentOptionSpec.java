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
 * Specification of an option that accepts an optional argument.
 *
 * @param <V>
 *             represents the type of the arguments this option accepts
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class OptionalArgumentOptionSpec<V> extends ArgumentAcceptingOptionSpec<V>
{
	OptionalArgumentOptionSpec( List<String> options, String description )
	{
		super( options, false, description );
	}

	OptionalArgumentOptionSpec( String option )
	{
		super( option, false );
	}

	@Override
	protected void detectOptionArgument( OptionParser parser, ArgumentList arguments, OptionSet detectedOptions )
	{
		if ( arguments.hasMore() )
		{
			String nextArgument = arguments.peek();

			if ( !parser.looksLikeAnOption( nextArgument ) && canConvertArgument( nextArgument ) )
				handleOptionArgument( parser, detectedOptions, arguments );
			else if ( isArgumentOfNumberType() && canConvertArgument( nextArgument ) )
				addArguments( detectedOptions, arguments.next() );
			else
				detectedOptions.add( this );
		}
		else
			detectedOptions.add( this );
	}

	private void handleOptionArgument( OptionParser parser, OptionSet detectedOptions, ArgumentList arguments )
	{
		if ( parser.posixlyCorrect() )
		{
			detectedOptions.add( this );
			parser.noMoreOptions();
		}
		else
			addArguments( detectedOptions, arguments.next() );
	}
}

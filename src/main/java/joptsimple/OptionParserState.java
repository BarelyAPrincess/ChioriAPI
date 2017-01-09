/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple;

import static joptsimple.ParserRules.isLongOptionToken;
import static joptsimple.ParserRules.isOptionTerminator;
import static joptsimple.ParserRules.isShortOptionToken;

/**
 * Abstraction of parser state; mostly serves to model how a parser behaves depending on whether end-of-options
 * has been detected.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
abstract class OptionParserState
{
	static OptionParserState noMoreOptions()
	{
		return new OptionParserState()
		{
			@Override
			protected void handleArgument( OptionParser parser, ArgumentList arguments, OptionSet detectedOptions )
			{
				parser.handleNonOptionArgument( arguments.next(), arguments, detectedOptions );
			}
		};
	}

	static OptionParserState moreOptions( final boolean posixlyCorrect )
	{
		return new OptionParserState()
		{
			@Override
			protected void handleArgument( OptionParser parser, ArgumentList arguments, OptionSet detectedOptions )
			{
				String candidate = arguments.next();
				try
				{
					if ( isOptionTerminator( candidate ) )
					{
						parser.noMoreOptions();
						return;
					}
					else if ( isLongOptionToken( candidate ) )
					{
						parser.handleLongOptionToken( candidate, arguments, detectedOptions );
						return;
					}
					else if ( isShortOptionToken( candidate ) )
					{
						parser.handleShortOptionToken( candidate, arguments, detectedOptions );
						return;
					}
				}
				catch ( UnrecognizedOptionException e )
				{
					if ( !parser.doesAllowsUnrecognizedOptions() )
						throw e;
				}

				if ( posixlyCorrect )
					parser.noMoreOptions();

				parser.handleNonOptionArgument( candidate, arguments, detectedOptions );
			}
		};
	}

	protected abstract void handleArgument( OptionParser parser, ArgumentList arguments, OptionSet detectedOptions );
}

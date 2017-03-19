/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple;

import static java.util.Collections.singletonList;
import static joptsimple.ParserRules.RESERVED_FOR_EXTENSIONS;

import java.util.Locale;

import joptsimple.internal.Messages;

/**
 * Represents the {@code "-W"} form of long option specification.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class AlternativeLongOptionSpec extends ArgumentAcceptingOptionSpec<String>
{
	AlternativeLongOptionSpec()
	{
		super( singletonList( RESERVED_FOR_EXTENSIONS ), true, Messages.message( Locale.getDefault(), "joptsimple.HelpFormatterMessages", AlternativeLongOptionSpec.class, "description" ) );

		describedAs( Messages.message( Locale.getDefault(), "joptsimple.HelpFormatterMessages", AlternativeLongOptionSpec.class, "arg.description" ) );
	}

	@Override
	protected void detectOptionArgument( OptionParser parser, ArgumentList arguments, OptionSet detectedOptions )
	{
		if ( !arguments.hasMore() )
			throw new OptionMissingRequiredArgumentException( this );

		arguments.treatNextAsLongOption();
	}
}

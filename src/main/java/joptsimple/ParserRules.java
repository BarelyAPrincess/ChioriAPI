/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple;

import static java.lang.Character.isLetterOrDigit;

import java.util.List;

/**
 * Can tell whether or not options are well-formed.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
final class ParserRules
{
	static final char HYPHEN_CHAR = '-';
	static final String HYPHEN = String.valueOf( HYPHEN_CHAR );
	static final String DOUBLE_HYPHEN = "--";
	static final String OPTION_TERMINATOR = DOUBLE_HYPHEN;
	static final String RESERVED_FOR_EXTENSIONS = "W";

	static void ensureLegalOption( String option )
	{
		if ( option.startsWith( HYPHEN ) )
			throw new IllegalOptionSpecificationException( String.valueOf( option ) );

		for ( int i = 0; i < option.length(); ++i )
			ensureLegalOptionCharacter( option.charAt( i ) );
	}

	private static void ensureLegalOptionCharacter( char option )
	{
		if ( ! ( isLetterOrDigit( option ) || isAllowedPunctuation( option ) ) )
			throw new IllegalOptionSpecificationException( String.valueOf( option ) );
	}

	static void ensureLegalOptions( List<String> options )
	{
		for ( String each : options )
			ensureLegalOption( each );
	}

	private static boolean isAllowedPunctuation( char option )
	{
		String allowedPunctuation = "?._" + HYPHEN_CHAR;
		return allowedPunctuation.indexOf( option ) != -1;
	}

	static boolean isLongOptionToken( String argument )
	{
		return argument.startsWith( DOUBLE_HYPHEN ) && !isOptionTerminator( argument );
	}

	static boolean isOptionTerminator( String argument )
	{
		return OPTION_TERMINATOR.equals( argument );
	}

	static boolean isShortOptionToken( String argument )
	{
		return argument.startsWith( HYPHEN ) && !HYPHEN.equals( argument ) && !isLongOptionToken( argument );
	}

	private ParserRules()
	{
		throw new UnsupportedOperationException();
	}
}

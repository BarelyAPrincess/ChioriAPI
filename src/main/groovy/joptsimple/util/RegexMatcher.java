/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple.util;

import static java.util.regex.Pattern.compile;
import static joptsimple.internal.Messages.message;

import java.util.Locale;
import java.util.regex.Pattern;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

/**
 * Ensures that values entirely match a regular expression.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class RegexMatcher implements ValueConverter<String>
{
	/**
	 * Gives a matcher that uses the given regular expression.
	 *
	 * @param pattern
	 *             the regular expression pattern
	 * @return the new converter
	 * @throws java.util.regex.PatternSyntaxException
	 *              if the expression's syntax is invalid
	 */
	public static ValueConverter<String> regex( String pattern )
	{
		return new RegexMatcher( pattern, 0 );
	}

	private final Pattern pattern;

	/**
	 * Creates a matcher that uses the given regular expression, modified by the given flags.
	 *
	 * @param pattern
	 *             the regular expression pattern
	 * @param flags
	 *             modifying regex flags
	 * @throws IllegalArgumentException
	 *              if bit values other than those corresponding to the defined match flags are
	 *              set in {@code flags}
	 * @throws java.util.regex.PatternSyntaxException
	 *              if the expression's syntax is invalid
	 */
	public RegexMatcher( String pattern, int flags )
	{
		this.pattern = compile( pattern, flags );
	}

	@Override
	public String convert( String value )
	{
		if ( !pattern.matcher( value ).matches() )
			raiseValueConversionFailure( value );

		return value;
	}

	private void raiseValueConversionFailure( String value )
	{
		String message = message( Locale.getDefault(), "joptsimple.ExceptionMessages", RegexMatcher.class, "message", value, pattern.pattern() );
		throw new ValueConversionException( message );
	}

	@Override
	public String valuePattern()
	{
		return pattern.pattern();
	}

	@Override
	public Class<String> valueType()
	{
		return String.class;
	}
}

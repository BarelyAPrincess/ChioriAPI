/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple.util;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import joptsimple.internal.Messages;

/**
 * Converts values to {@link Date}s using a {@link DateFormat} object.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class DateConverter implements ValueConverter<Date>
{
	/**
	 * Creates a converter that uses a {@link SimpleDateFormat} with the given date/time pattern. The date formatter
	 * created is not {@link SimpleDateFormat#setLenient(boolean) lenient}.
	 *
	 * @param pattern
	 *             expected date/time pattern
	 * @return the new converter
	 * @throws NullPointerException
	 *              if {@code pattern} is {@code null}
	 * @throws IllegalArgumentException
	 *              if {@code pattern} is invalid
	 */
	public static DateConverter datePattern( String pattern )
	{
		SimpleDateFormat formatter = new SimpleDateFormat( pattern );
		formatter.setLenient( false );

		return new DateConverter( formatter );
	}

	private final DateFormat formatter;

	/**
	 * Creates a converter that uses the given date formatter/parser.
	 *
	 * @param formatter
	 *             the formatter/parser to use
	 * @throws NullPointerException
	 *              if {@code formatter} is {@code null}
	 */
	public DateConverter( DateFormat formatter )
	{
		if ( formatter == null )
			throw new NullPointerException( "illegal null formatter" );

		this.formatter = formatter;
	}

	@Override
	public Date convert( String value )
	{
		ParsePosition position = new ParsePosition( 0 );

		Date date = formatter.parse( value, position );
		if ( position.getIndex() != value.length() )
			throw new ValueConversionException( message( value ) );

		return date;
	}

	private String message( String value )
	{
		String key;
		Object[] arguments;

		if ( formatter instanceof SimpleDateFormat )
		{
			key = "with.pattern.message";
			arguments = new Object[] {value, ( ( SimpleDateFormat ) formatter ).toPattern()};
		}
		else
		{
			key = "without.pattern.message";
			arguments = new Object[] {value};
		}

		return Messages.message( Locale.getDefault(), "joptsimple.ExceptionMessages", DateConverter.class, key, arguments );
	}

	@Override
	public String valuePattern()
	{
		return formatter instanceof SimpleDateFormat ? ( ( SimpleDateFormat ) formatter ).toPattern() : "";
	}

	@Override
	public Class<Date> valueType()
	{
		return Date.class;
	}
}

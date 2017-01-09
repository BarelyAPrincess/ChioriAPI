/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import joptsimple.internal.Messages;

/**
 * Converts values to {@link java.net.InetAddress} using {@link InetAddress#getByName(String) getByName}.
 *
 * @author <a href="mailto:r@ymund.de">Raymund F\u00FCl\u00F6p</a>
 */
public class InetAddressConverter implements ValueConverter<InetAddress>
{
	@Override
	public InetAddress convert( String value )
	{
		try
		{
			return InetAddress.getByName( value );
		}
		catch ( UnknownHostException e )
		{
			throw new ValueConversionException( message( value ) );
		}
	}

	private String message( String value )
	{
		return Messages.message( Locale.getDefault(), "joptsimple.ExceptionMessages", InetAddressConverter.class, "message", value );
	}

	@Override
	public String valuePattern()
	{
		return null;
	}

	@Override
	public Class<InetAddress> valueType()
	{
		return InetAddress.class;
	}
}

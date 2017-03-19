/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.fusesource.jansi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * An ANSI string which reports the size of rendered text correctly (ignoring any ANSI escapes).
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 1.1
 */
public class AnsiString implements CharSequence
{
	private final CharSequence encoded;

	private final CharSequence plain;

	public AnsiString( final CharSequence str )
	{
		assert str != null;
		this.encoded = str;
		this.plain = chew( str );
	}

	@Override
	public char charAt( final int index )
	{
		return getEncoded().charAt( index );
	}

	private CharSequence chew( final CharSequence str )
	{
		assert str != null;

		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		AnsiOutputStream out = new AnsiOutputStream( buff );

		try
		{
			out.write( str.toString().getBytes() );
			out.flush();
			out.close();
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}

		return new String( buff.toByteArray() );
	}

	@Override
	public boolean equals( final Object obj )
	{
		return getEncoded().equals( obj );
	}

	// FIXME: charAt() and subSequence() will make things barf, need to call toString() first to get expected results

	public CharSequence getEncoded()
	{
		return encoded;
	}

	public CharSequence getPlain()
	{
		return plain;
	}

	@Override
	public int hashCode()
	{
		return getEncoded().hashCode();
	}

	@Override
	public int length()
	{
		return getPlain().length();
	}

	@Override
	public CharSequence subSequence( final int start, final int end )
	{
		return getEncoded().subSequence( start, end );
	}

	@Override
	public String toString()
	{
		return getEncoded().toString();
	}
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.fusesource.jansi;

import static org.fusesource.jansi.AnsiRenderer.render;
import static org.fusesource.jansi.AnsiRenderer.test;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

/**
 * Print writer which supports automatic ANSI color rendering via {@link AnsiRenderer}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @since 1.1
 */
public class AnsiRenderWriter extends PrintWriter
{
	public AnsiRenderWriter( final OutputStream out )
	{
		super( out );
	}

	public AnsiRenderWriter( final OutputStream out, final boolean autoFlush )
	{
		super( out, autoFlush );
	}

	public AnsiRenderWriter( final Writer out )
	{
		super( out );
	}

	public AnsiRenderWriter( final Writer out, final boolean autoFlush )
	{
		super( out, autoFlush );
	}

	@Override
	public PrintWriter format( final Locale l, final String format, final Object... args )
	{
		print( String.format( l, format, args ) );
		return this;
	}

	//
	// Need to prevent partial output from being written while formatting or we will get rendering exceptions
	//

	@Override
	public PrintWriter format( final String format, final Object... args )
	{
		print( String.format( format, args ) );
		return this;
	}

	@Override
	public void write( final String s )
	{
		if ( test( s ) )
			super.write( render( s ) );
		else
			super.write( s );
	}
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2015 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.logger.experimental;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;

import com.chiorichan.lang.EnumColor;
import com.chiorichan.lang.IException;
import com.chiorichan.logger.Log;
import com.google.common.collect.Lists;

class LogRecord implements ILogEvent
{
	static class LogElement
	{
		Level level;
		String msg;
		EnumColor color;
		long time = System.currentTimeMillis();

		LogElement( Level level, String msg, EnumColor color )
		{
			this.level = level;
			this.msg = msg;
			this.color = color;
		}
	}

	String header = null;

	final List<LogElement> elements = Lists.newLinkedList();

	LogRecord()
	{

	}

	@Override
	public void exceptions( Throwable... throwables )
	{
		for ( Throwable t : throwables )
			if ( t instanceof IException )
			{
				if ( ( ( IException ) t ).reportingLevel().isEnabled() )
					log( Level.SEVERE, EnumColor.NEGATIVE + "" + EnumColor.RED + t.getMessage() );
			}
			else
				log( Level.SEVERE, EnumColor.NEGATIVE + "" + EnumColor.RED + t.getMessage() );
	}

	@Override
	public void flush()
	{
		StringBuilder sb = new StringBuilder();

		if ( header != null )
			sb.append( EnumColor.RESET + header );

		for ( LogElement e : elements )
			sb.append( EnumColor.RESET + "" + EnumColor.GRAY + "\n  |-> " + new SimpleDateFormat( "ss.SSS" ).format( e.time ) + " " + e.color + e.msg );

		Log.get().log( Level.INFO, "\r" + sb.toString() );

		elements.clear();
	}

	@Override
	public void header( String msg, Object... objs )
	{
		header = String.format( msg, objs );
	}

	@Override
	public void log( Level level, String msg, Object... objs )
	{
		if ( objs.length < 1 )
			elements.add( new LogElement( level, msg, EnumColor.fromLevel( level ) ) );
		else
			elements.add( new LogElement( level, String.format( msg, objs ), EnumColor.fromLevel( level ) ) );
	}
}

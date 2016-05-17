/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import com.chiorichan.AppConfig;
import com.chiorichan.lang.EnumColor;
import com.google.common.base.Strings;

public class DefaultLogFormatter extends Formatter
{
	public static boolean debugMode = false;
	public static int debugModeHowDeep = 1;
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat timeFormat;
	private boolean useColor;

	private boolean formatConfigLoaded = false;

	public DefaultLogFormatter()
	{
		this( true );
	}

	public DefaultLogFormatter( boolean useColor )
	{
		this.useColor = useColor;
		dateFormat = new SimpleDateFormat( "MM-dd" );
		timeFormat = new SimpleDateFormat( "HH:mm:ss.SSS" );
	}

	@Override
	public String format( LogRecord record )
	{
		if ( AppConfig.get().isConfigLoaded() && !formatConfigLoaded )
		{
			dateFormat = new SimpleDateFormat( AppConfig.get().getString( "console.dateFormat", "MM-dd" ) );
			timeFormat = new SimpleDateFormat( AppConfig.get().getString( "console.timeFormat", "HH:mm:ss.SSS" ) );
			formatConfigLoaded = true;
		}

		String style = AppConfig.get().isConfigLoaded() ? AppConfig.get().getString( "console.style", "&r&7[&d%ct&7] %dt %tm [%lv&7]&f" ) : "&r&7%dt %tm [%lv&7]&f";

		Throwable ex = record.getThrown();

		if ( style.contains( "%ct" ) )
		{
			String threadName = Thread.currentThread().getName();

			if ( threadName.length() > 10 )
				threadName = threadName.substring( 0, 2 ) + ".." + threadName.substring( threadName.length() - 6 );
			else if ( threadName.length() < 10 )
				threadName = threadName + Strings.repeat( " ", 10 - threadName.length() );

			style = style.replaceAll( "%ct", threadName );
		}

		style = style.replaceAll( "%dt", dateFormat.format( record.getMillis() ) );
		style = style.replaceAll( "%tm", timeFormat.format( record.getMillis() ) );

		int howDeep = debugModeHowDeep;

		if ( debugMode )
		{
			StackTraceElement[] var1 = Thread.currentThread().getStackTrace();

			for ( StackTraceElement var2 : var1 )
				if ( !var2.getClassName().toLowerCase().contains( "java" ) && !var2.getClassName().toLowerCase().contains( "sun" ) && !var2.getClassName().toLowerCase().contains( "log" ) && !var2.getMethodName().equals( "sendMessage" ) && !var2.getMethodName().equals( "sendRawMessage" ) )
				{
					howDeep--;

					if ( howDeep <= 0 )
					{
						style += " " + var2.getClassName() + "$" + var2.getMethodName() + ":" + var2.getLineNumber();
						break;
					}
				}
		}

		if ( style.contains( "%lv" ) )
			style = style.replaceAll( "%lv", EnumColor.fromLevel( record.getLevel() ) + record.getLevel().getLocalizedName().toUpperCase() );

		style += " " + formatMessage( record );

		if ( !style.endsWith( "\r" ) )
			style += "\n";

		if ( ex != null )
		{
			StringWriter writer = new StringWriter();
			ex.printStackTrace( new PrintWriter( writer ) );
			style += writer;
		}

		if ( !useColor )
			return EnumColor.removeAltColors( style );
		else
			return EnumColor.transAltColors( style );
	}

	public boolean useColor()
	{
		return useColor;
	}
}

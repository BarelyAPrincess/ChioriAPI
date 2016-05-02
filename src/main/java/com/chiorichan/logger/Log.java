/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.logger;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chiorichan.AppController;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.util.FileFunc;
import com.chiorichan.util.Versioning;

public class Log implements LogAPI
{
	private static final Set<Log> loggers = new HashSet<>();
	private static final Logger rootLogger = Logger.getLogger( "" );
	private static final PrintStream altOutputStream = new PrintStream( new FileOutputStream( FileDescriptor.out ) );

	static
	{
		for ( Handler h : rootLogger.getHandlers() )
			rootLogger.removeHandler( h );
	}

	public static void addFileHandler( String filename, boolean useColor, int archiveLimit, Level level )
	{
		try
		{
			File log = new File( AppController.config().getDirectoryLogs(), filename + ".log" );

			if ( log.exists() )
			{
				if ( archiveLimit > 0 )
					FileFunc.gzFile( log, new File( AppController.config().getDirectoryLogs(), new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" ).format( new Date() ) + "-" + filename + ".log.gz" ) );
				log.delete();
			}

			cleanupLogs( "-" + filename + ".log.gz", archiveLimit );

			FileHandler fileHandler = new FileHandler( log.getAbsolutePath() );
			fileHandler.setLevel( level );
			fileHandler.setFormatter( new DefaultLogFormatter( useColor ) );

			addHandler( fileHandler );
		}
		catch ( Exception e )
		{
			get().severe( "Failed to log to '" + filename + ".log'", e );
		}
	}

	public static void addHandler( Handler h )
	{
		rootLogger.addHandler( h );
	}

	private static void cleanupLogs( final String suffix, int limit )
	{
		File[] files = AppController.config().getDirectoryLogs().listFiles( new FilenameFilter()
		{
			@Override
			public boolean accept( File dir, String name )
			{
				return name.toLowerCase().endsWith( suffix );
			}
		} );

		if ( files == null || files.length < 1 )
			return;

		// Delete all logs, no archiving!
		if ( limit < 1 )
		{
			for ( File f : files )
				f.delete();
			return;
		}

		FileFunc.SortableFile[] sfiles = new FileFunc.SortableFile[files.length];

		for ( int i = 0; i < files.length; i++ )
			sfiles[i] = new FileFunc.SortableFile( files[i] );

		Arrays.sort( sfiles );

		if ( sfiles.length > limit )
			for ( int i = 0; i < sfiles.length - limit; i++ )
				sfiles[i].f.delete();
	}

	public static Log get()
	{
		return get( "" );
	}

	public static Log get( LogSource source )
	{
		return get( source.getLoggerId() );
	}

	/**
	 * Gets an instance of Log for provided loggerId. If the logger does not exist one will be created.
	 *
	 * @param id
	 *             The logger id
	 * @return ConsoleLogger An empty loggerId will return the System Logger.
	 */
	public static Log get( String id )
	{
		if ( id == null || id.length() == 0 )
			id = "Core";

		for ( Log log : loggers )
			if ( log.getId().equals( id ) )
				return log;

		Log log = new Log( id );
		loggers.add( log );
		return log;
	}

	public static Logger getRootLogger()
	{
		return rootLogger;
	}

	public static void removeHandler( Handler h )
	{
		rootLogger.removeHandler( h );
	}

	private final Logger logger;
	private final String id;
	private boolean hasErrored = false;

	/**
	 * Attempts to find a logger based on the id provided. If you would like to use your own Logger, be sure to create it with the same id prior to using any of the built-in getLogger() methods or you will need to use the replaceLogger() method.
	 *
	 * @param id
	 *             The logger id
	 */
	protected Log( String id )
	{
		this.id = id;
		Logger logger = Logger.getLogger( id );

		if ( logger == null )
			logger = new ChildLogger( id );

		logger.setParent( getRootLogger() );
		logger.setLevel( Level.ALL );

		this.logger = logger;
	}

	@Override
	public void debug( Object... var1 )
	{
		if ( !Versioning.isDevelopment() || var1.length < 1 )
			return;

		for ( Object var2 : var1 )
			if ( var2 != null )
				info( EnumColor.NEGATIVE + "" + EnumColor.GOLD + ">>>>   " + var2.toString() + "   <<<< " );
	}

	@Override
	public void dev( Object... var1 )
	{
		if ( !Versioning.isDevelopment() || var1.length < 1 )
			return;

		for ( Object var2 : var1 )
			if ( var2 != null )
				info( EnumColor.NEGATIVE + "" + EnumColor.WHITE + "[DEV NOTICE] " + var2.toString() );
	}

	@Override
	public void fine( String var1 )
	{
		log( Level.FINE, var1 );
	}

	@Override
	public void finer( String var1 )
	{
		log( Level.FINER, var1 );
	}

	@Override
	public void finest( String var1 )
	{
		log( Level.FINEST, var1 );
	}

	@Override
	public String getId()
	{
		return id;
	}

	public Logger getLogger()
	{
		return logger;
	}

	public boolean hasErrored()
	{
		return hasErrored;
	}

	@Override
	public void highlight( String msg )
	{
		log( Level.INFO, EnumColor.GOLD + "" + EnumColor.NEGATIVE + msg );
	}

	@Override
	public void info( String s )
	{
		log( Level.INFO, EnumColor.WHITE + s );
	}

	@Override
	public void log( Level l, String msg )
	{
		try
		{
			if ( hasErrored )
				altOutputStream.println( "Failover Logger [" + l.getName() + "] " + msg );
			else
				logger.log( l, msg );
		}
		catch ( Throwable t )
		{
			markError( t );
			if ( Versioning.isDevelopment() )
				throw t;
		}
	}

	@Override
	public void log( Level l, String msg, Object... params )
	{
		try
		{
			if ( hasErrored )
				altOutputStream.println( "Failover Logger [" + l.getName() + "] " + msg );
			else
				logger.log( l, msg, params );
		}
		catch ( Throwable t )
		{
			markError( t );
			if ( Versioning.isDevelopment() )
				throw t;
		}
	}

	@Override
	public void log( Level l, String msg, Throwable t )
	{
		try
		{
			if ( hasErrored )
				altOutputStream.println( "Failover Logger [" + l.getName() + "] " + msg );
			else
				logger.log( l, msg, t );
		}
		catch ( Throwable tt )
		{
			markError( tt );
			if ( Versioning.isDevelopment() )
				throw tt;
		}
	}

	private void markError( Throwable t )
	{
		hasErrored = true;

		altOutputStream.println( EnumColor.RED + "" + EnumColor.NEGATIVE + "The child logger \"" + getId() + "\" has thrown an unrecoverable exception!" );
		altOutputStream.println( EnumColor.RED + "" + EnumColor.NEGATIVE + "Please report the following stacktrace to the application developer." );
		if ( Versioning.isDevelopment() )
			altOutputStream.println( EnumColor.RED + "" + EnumColor.NEGATIVE + "ATTENTION DEVELOPER: Calling the method \"Log.get( [log name] ).unmarkError()\" will reset the errored state." );
		t.printStackTrace( altOutputStream );
	}

	public String[] multilineColorRepeater( String var1 )
	{
		return multilineColorRepeater( var1.split( "\\n" ) );
	}

	public String[] multilineColorRepeater( String[] var1 )
	{
		String color = EnumColor.getLastColors( var1[0] );

		for ( int l = 0; l < var1.length; l++ )
			var1[l] = color + var1[l];

		return var1;
	}

	@Override
	public void panic( String var1 )
	{
		severe( var1 );
		AppController.stopApplication( var1 );
	}

	@Override
	public void panic( String var1, Object... objs )
	{
		severe( var1, objs );
		AppController.stopApplication( String.format( var1, objs ) );
	}

	@Override
	public void panic( String var1, Throwable t )
	{
		severe( var1, t );
		AppController.stopApplication( var1 );
	}

	@Override
	public void panic( Throwable e )
	{
		severe( e );
		AppController.stopApplication( "The server is stopping due to a severe error!" );
	}

	@Override
	public void severe( String s )
	{
		log( Level.SEVERE, EnumColor.RED + s );
	}

	@Override
	public void severe( String s, Object... objs )
	{
		log( Level.SEVERE, EnumColor.RED + s, objs );
	}

	@Override
	public void severe( String s, Throwable t )
	{
		log( Level.SEVERE, EnumColor.RED + s, t );
	}

	@Override
	public void severe( Throwable t )
	{
		log( Level.SEVERE, EnumColor.RED + t.getMessage(), t );
	}

	public void unmarkError()
	{
		hasErrored = false;
	}

	@Override
	public void warning( String s )
	{
		log( Level.WARNING, EnumColor.GOLD + s );
	}

	@Override
	public void warning( String s, Object... objs )
	{
		log( Level.WARNING, EnumColor.GOLD + s, objs );
	}

	@Override
	public void warning( String s, Throwable throwable )
	{
		log( Level.WARNING, EnumColor.GOLD + s, throwable );
	}
}

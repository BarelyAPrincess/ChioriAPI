package com.chiorichan.logger;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chiorichan.AppController;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.util.Versioning;

public class Log implements LogAPI
{
	private static final Set<Log> loggers = new HashSet<>();
	private static final Logger logger = Logger.getLogger( "" );

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
	 * @param loggerId
	 *             The loggerId we are looking for.
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

	private final Logger logger;
	private final String id;

	/**
	 * Attempts to find a logger based on the id provided.
	 * If you would like to use your own Logger, be sure to create it
	 * with the same id prior to using any of the builtin getLogger() methods
	 * or you will need to use the replaceLogger() method.
	 *
	 * @param loggerId
	 */
	protected Log( String id )
	{
		Logger logger = Logger.getLogger( id );

		if ( logger == null )
			logger = new SubLog( id );

		logger.setParent( getLogManager().getParent() );
		logger.setLevel( Level.ALL );

		this.id = id;
		Log.logger = logger;
	}

	@Override
	public void debug( Object... var1 )
	{
		if ( !Versioning.isDevelopment() || var1.length < 1 )
			return;

		for ( Object var2 : var1 )
			if ( var2 != null )
				info( EnumColor.NEGATIVE + "" + EnumColor.YELLOW + " >>>>   " + var2.toString() + "   <<<< " );
	}

	@Override
	public void exceptions( Throwable... throwables )
	{
		for ( Throwable t : throwables )
			if ( e.errorLevel().isEnabledLevel() )
				if ( e.isScriptingException() )
				{
					ScriptTraceElement element = e.getScriptTrace()[0];
					severe( String.format( EnumColor.NEGATIVE + "" + EnumColor.RED + "Exception %s thrown in file '%s' at line %s:%s, message '%s'", e.getClass().getName(), element.context().filename(), element.getLineNumber(), element.getColumnNumber() > 0 ? element.getColumnNumber() : 0, e.getMessage() ) );
				}
				else
					severe( String.format( EnumColor.NEGATIVE + "" + EnumColor.RED + "Exception %s thrown in file '%s' at line %s, message '%s'", e.getClass().getName(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber(), e.getMessage() ) );
	}

	@Override
	public void fine( String var1 )
	{
		logger.log( Level.FINE, var1 );
	}

	@Override
	public void finer( String var1 )
	{
		logger.log( Level.FINER, var1 );
	}

	@Override
	public void finest( String var1 )
	{
		logger.log( Level.FINEST, var1 );
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
		logger.log( l, msg );
	}

	@Override
	public void log( Level level, String msg, Object... params )
	{
		logger.log( level, msg, params );
	}

	@Override
	public void log( Level l, String msg, Throwable t )
	{
		logger.log( l, msg, t );
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
		AppController.stop( var1 );
	}

	@Override
	public void panic( Throwable e )
	{
		severe( e );
		AppController.stop( "The server is stopping due to a severe error!" );
	}

	@Override
	public void severe( String s )
	{
		log( Level.SEVERE, EnumColor.RED + s );
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

	@Override
	public void warning( String s )
	{
		log( Level.WARNING, EnumColor.GOLD + s );
	}

	@Override
	public void warning( String s, Object... aobject )
	{
		log( Level.WARNING, EnumColor.GOLD + s, aobject );
	}

	@Override
	public void warning( String s, Throwable throwable )
	{
		log( Level.WARNING, EnumColor.GOLD + s, throwable );
	}
}

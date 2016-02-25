package com.chiorichan.lang;

public class UncaughtException extends RuntimeException implements IException
{
	private ReportingLevel level;

	public UncaughtException()
	{
		this( ReportingLevel.E_ERROR );
	}

	public UncaughtException( ReportingLevel level )
	{
		this.level = level;
	}

	public UncaughtException( ReportingLevel level, String message )
	{
		super( message );
		this.level = level;
	}

	public UncaughtException( ReportingLevel level, String msg, Throwable cause )
	{
		super( msg, cause );
		this.level = level;
		if ( cause instanceof UncaughtException )
			throw new IllegalArgumentException( "The cause argument can't be of it's own type." );
	}

	public UncaughtException( ReportingLevel level, Throwable cause )
	{
		super( cause );
		this.level = level;
		if ( cause instanceof UncaughtException )
			throw new IllegalArgumentException( "The cause argument can't be of it's own type." );
	}

	public UncaughtException( String message )
	{
		this( ReportingLevel.E_ERROR, message );
	}

	public UncaughtException( String msg, Throwable cause )
	{
		this( ReportingLevel.E_ERROR, msg, cause );
	}

	public UncaughtException( Throwable cause )
	{
		this( ReportingLevel.E_ERROR, cause );
	}

	@Override
	public boolean handle( ExceptionReport report, ExceptionContext context )
	{
		return false;
	}

	@Override
	public boolean isIgnorable()
	{
		return level.isIgnorable();
	}

	@Override
	public ReportingLevel reportingLevel()
	{
		return level;
	}
}

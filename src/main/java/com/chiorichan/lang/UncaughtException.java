package com.chiorichan.lang;

public class UncaughtException extends ApplicationException
{
	public UncaughtException()
	{
		super( ReportingLevel.E_ERROR );
	}

	public UncaughtException( ReportingLevel level )
	{
		super( level );
	}

	public UncaughtException( ReportingLevel level, String message )
	{
		super( level, message );
	}

	public UncaughtException( ReportingLevel level, String msg, Throwable cause )
	{
		super( level, msg, cause );
	}

	public UncaughtException( ReportingLevel level, Throwable cause )
	{
		super( level, cause );
	}

	public UncaughtException( String message )
	{
		super( ReportingLevel.E_ERROR, message );
	}

	public UncaughtException( String msg, Throwable cause )
	{
		super( ReportingLevel.E_ERROR, msg, cause );
	}

	public UncaughtException( Throwable cause )
	{
		super( ReportingLevel.E_ERROR, cause );
	}

	@Override
	public boolean handle( ExceptionReport report, ExceptionContext context )
	{
		return false;
	}
}

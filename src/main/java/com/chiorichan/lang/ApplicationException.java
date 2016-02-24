package com.chiorichan.lang;



public abstract class ApplicationException extends Exception implements IException
{
	protected final ReportingLevel level;

	public ApplicationException( ReportingLevel level )
	{
		this.level = level;
	}

	public ApplicationException( ReportingLevel level, String message )
	{
		super( message );
		this.level = level;
	}

	public ApplicationException( ReportingLevel level, String message, Throwable cause )
	{
		super( message, cause );
		this.level = level;
		if ( cause instanceof ApplicationException )
			throw new IllegalArgumentException( "The cause argument can't be of it's own type." );
	}

	public ApplicationException( ReportingLevel level, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace )
	{
		super( message, cause, enableSuppression, writableStackTrace );
		this.level = level;
		if ( cause instanceof ApplicationException )
			throw new IllegalArgumentException( "The cause argument can't be of it's own type." );
	}

	public ApplicationException( ReportingLevel level, Throwable cause )
	{
		super( cause );
		this.level = level;
		if ( cause instanceof ApplicationException )
			throw new IllegalArgumentException( "The cause argument can't be of it's own type." );
	}

	@Override
	public String getMessage()
	{
		return super.getMessage();
		// return String.format( "Exception %s thrown in file '%s' at line %s: '%s'", getClass().getName(), getStackTrace()[0].getFileName(), getStackTrace()[0].getLineNumber(), super.getMessage() );
	}

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

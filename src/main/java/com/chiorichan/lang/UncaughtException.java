/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * <p>
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.lang;

public class UncaughtException extends RuntimeException implements IException
{
	private static final long serialVersionUID = 6854413013575591783L;

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
			throwCauseException();
	}

	public UncaughtException( ReportingLevel level, String msg, Throwable cause, boolean throwDuplicate ) throws UncaughtException
	{
		super( msg, cause );
		this.level = level;
		if ( cause instanceof UncaughtException )
			if ( throwDuplicate )
				throw ( UncaughtException ) cause;
			else
				throwCauseException();
	}

	public UncaughtException( ReportingLevel level, Throwable cause )
	{
		super( cause );
		this.level = level;
		if ( cause instanceof UncaughtException )
			throwCauseException();
	}

	public UncaughtException( ReportingLevel level, Throwable cause, boolean throwDuplicate ) throws UncaughtException
	{
		super( cause );
		this.level = level;
		if ( cause instanceof UncaughtException )
			if ( throwDuplicate )
				throw ( UncaughtException ) cause;
			else
				throwCauseException();
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

	@Override public boolean handle( ExceptionReport report, ExceptionContext context )
	{
		return false;
	}

	@Override public boolean isIgnorable()
	{
		return level.isIgnorable();
	}

	@Override public ReportingLevel reportingLevel()
	{
		return level;
	}

	private void throwCauseException()
	{
		throw new IllegalArgumentException( "The cause argument can't be of it's own type." );
	}
}

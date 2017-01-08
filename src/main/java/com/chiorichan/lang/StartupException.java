/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * <p>
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.lang;

public class StartupException extends UncaughtException
{
	private static final long serialVersionUID = 1L;

	public StartupException( String msg )
	{
		super( ReportingLevel.E_ERROR, msg );
	}

	public StartupException( String msg, Throwable e ) throws UncaughtException
	{
		super( ReportingLevel.E_ERROR, msg, e, true );
	}

	public StartupException( Throwable e ) throws UncaughtException
	{
		super( ReportingLevel.E_ERROR, e, true );
	}

	@Override public boolean handle( ExceptionReport report, ExceptionContext context )
	{
		return false;
	}
}

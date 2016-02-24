/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2015 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.permission.lang;

import com.chiorichan.lang.ApplicationException;
import com.chiorichan.lang.ExceptionContext;
import com.chiorichan.lang.ExceptionReport;
import com.chiorichan.lang.ReportingLevel;

/**
 * This exception is thrown when a permissions backend has issues loading
 */
public class PermissionBackendException extends ApplicationException
{
	private static final long serialVersionUID = -133147199740089646L;

	public PermissionBackendException()
	{
		super( ReportingLevel.E_ERROR );
	}

	public PermissionBackendException( String message )
	{
		super( ReportingLevel.E_ERROR, message );
	}

	public PermissionBackendException( String message, Throwable cause )
	{
		super( ReportingLevel.E_ERROR, message, cause );
	}

	public PermissionBackendException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace )
	{
		super( ReportingLevel.E_ERROR, message, cause, enableSuppression, writableStackTrace );
	}

	public PermissionBackendException( Throwable cause )
	{
		super( ReportingLevel.E_ERROR, cause );
	}

	@Override
	public boolean handle( ExceptionReport report, ExceptionContext context )
	{
		return false;
	}
}

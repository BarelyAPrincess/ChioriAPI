/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
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
	public ReportingLevel handle( ExceptionReport report, ExceptionContext context )
	{
		return null;
	}
}

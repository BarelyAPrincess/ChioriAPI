/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2015 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.lang;

/**
 * Covers all exceptions that could throw during plugin loads, unloads or etc.
 */
public class PluginException extends ApplicationException
{
	private static final long serialVersionUID = -985004348649679626L;

	public PluginException()
	{
		super( ReportingLevel.E_ERROR );
	}

	public PluginException( String message )
	{
		super( ReportingLevel.E_ERROR, message );
	}

	public PluginException( String message, Throwable cause )
	{
		super( ReportingLevel.E_ERROR, message, cause );
	}

	public PluginException( Throwable cause )
	{
		super( ReportingLevel.E_ERROR, cause );
	}

	@Override
	public boolean handle( ExceptionReport report, ExceptionContext context )
	{
		return false;
	}
}

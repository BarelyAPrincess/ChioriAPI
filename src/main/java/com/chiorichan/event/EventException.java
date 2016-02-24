/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2015 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.event;

import com.chiorichan.lang.ApplicationException;
import com.chiorichan.lang.ExceptionContext;
import com.chiorichan.lang.ExceptionReport;
import com.chiorichan.lang.ReportingLevel;

public class EventException extends ApplicationException
{
	private static final long serialVersionUID = 3532808232324183999L;

	/**
	 * Constructs a new EventException
	 */
	public EventException()
	{
		super( ReportingLevel.E_ERROR );
	}

	/**
	 * Constructs a new EventException with the given message
	 *
	 * @param message
	 *             The message
	 */
	public EventException( String message )
	{
		super( ReportingLevel.E_ERROR, message );
	}

	/**
	 * Constructs a new EventException with the given message
	 *
	 * @param cause
	 *             The exception that caused this
	 * @param message
	 *             The message
	 */
	public EventException( String message, Throwable cause )
	{
		super( ReportingLevel.E_ERROR, message, cause );
	}

	/**
	 * Constructs a new EventException based on the given Exception
	 *
	 * @param throwable
	 *             Exception that triggered this Exception
	 */
	public EventException( Throwable cause )
	{
		super( ReportingLevel.E_ERROR, cause );
	}

	@Override
	public boolean handle( ExceptionReport report, ExceptionContext context )
	{
		return false;
	}
}

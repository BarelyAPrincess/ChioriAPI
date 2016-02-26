/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.lang;

/**
 * Provides a callback to when a registered exception is thrown
 */
public interface ExceptionCallback
{
	/**
	 * Called for each registered Exception Callback for handling.
	 *
	 * @param cause
	 *             The thrown exception
	 * @param context
	 *             The thrown context
	 * @return The resulting ErrorReporting level
	 *         Returning NULL will, if possible, try the next best matching EvalCallback
	 */
	ReportingLevel callback( Throwable cause, ExceptionReport report, ExceptionContext context );
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.lang;

public interface IException
{
	static void check( IException t )
	{
		if ( ! ( t instanceof Throwable ) )
			throw new IllegalArgumentException( "IException must be implemented by java.lang.Throwable only, this is a serious programming bug!" );
	}

	Throwable getCause();

	ReportingLevel reportingLevel();

	String getMessage();

	/**
	 * Called to properly add exception information to the ExceptionReport which is then used to generate a script trace or {@link ApplicationCrashReport}
	 * <p/>
	 * Typically you would just add your exception to the report with {@code report.addException( this );} and provide some possible unique debug information, if any exists.
	 *
	 * @param report
	 *             The ExceptionReport to fill
	 * @param context
	 *             The Exception Context
	 * @return Did this method successfully handle the reporting of this exception. On false, the application will make up it's own conclusion.
	 */
	boolean handle( ExceptionReport report, ExceptionContext context );

	boolean isIgnorable();

	void printStackTrace();
}

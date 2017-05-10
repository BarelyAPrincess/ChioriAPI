/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.logger.experimental;

import java.util.logging.Level;

/**
 * Interface for {@link LogEvent} and {@link LogRecord}
 */
public interface ILogEvent
{
	void exceptions( Throwable... throwables );

	void log( Level level, String msg, Object... objs );

	void flush();

	void header( String msg, Object... objs );
}

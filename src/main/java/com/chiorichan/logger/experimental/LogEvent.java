/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.logger.experimental;

import java.util.logging.Level;

/**
 */
public class LogEvent implements ILogEvent
{
	final String id;
	final LogRecord record;

	LogEvent( String id, LogRecord record )
	{
		this.id = id;
		this.record = record;
	}

	public void close()
	{
		LogManager.close( this );
	}

	@Override
	public void exceptions( Throwable... exceptions )
	{
		record.exceptions( exceptions );
	}

	@Override
	public void flush()
	{
		record.flush();
	}

	public void flushAndClose()
	{
		flush();
		close();
	}

	@Override
	public void header( String msg, Object... objs )
	{
		record.header( msg, objs );
	}

	@Override
	public void log( Level level, String msg, Object... objs )
	{
		record.log( level, msg, objs );
	}
}

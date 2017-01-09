/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public class LoggerOutputStream extends ByteArrayOutputStream
{
	private final String separator = System.getProperty( "line.separator" );
	private final Log log;
	private final Level level;

	public LoggerOutputStream( Log log, Level level )
	{
		super();
		this.log = log;
		this.level = level;
	}

	@Override
	public void flush() throws IOException
	{
		synchronized ( this )
		{
			super.flush();
			String record = this.toString();
			super.reset();

			if ( record.length() > 0 && !record.equals( separator ) )
				log.log( level, record );
		}
	}
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
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

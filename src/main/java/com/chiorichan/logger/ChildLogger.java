/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.logger;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.chiorichan.AppController;

public class ChildLogger extends Logger
{
	protected ChildLogger( String id )
	{
		super( id, null );
	}

	@Override
	public void log( LogRecord logRecord )
	{
		if ( AppController.config() != null && !AppController.config().getBoolean( "console.hideLoggerName" ) )
			logRecord.setMessage( "&7[" + getName() + "]&f " + logRecord.getMessage() );

		super.log( logRecord );
	}
}

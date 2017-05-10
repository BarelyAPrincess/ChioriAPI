/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.logger;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.chiorichan.AppConfig;

public class ChildLogger extends Logger
{
	protected ChildLogger( String id )
	{
		super( id, null );
	}

	@Override
	public void log( LogRecord logRecord )
	{
		if ( AppConfig.get().isConfigLoaded() && !AppConfig.get().getBoolean( "console.hideLoggerName" ) )
			logRecord.setMessage( "&7[" + getName() + "]&f " + logRecord.getMessage() );

		super.log( logRecord );
	}
}

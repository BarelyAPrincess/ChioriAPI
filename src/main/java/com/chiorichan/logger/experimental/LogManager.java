/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.logger.experimental;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentMap;

import com.chiorichan.tasks.TaskManager;
import com.chiorichan.tasks.TaskRegistrar;
import com.google.common.collect.Maps;

/**
 *
 */
public class LogManager implements TaskRegistrar
{
	static class LogReference extends WeakReference<Object>
	{
		final String key;
		final LogRecord record;

		LogReference( String key, LogRecord record, Object garbage )
		{
			super( garbage, referenceQueue );
			this.key = key;
			this.record = record;
		}
	}
	private static final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();

	private static final ConcurrentMap<String, LogReference> activeLogs = Maps.newConcurrentMap();	public static final LogManager INSTANCE = new LogManager();

	public static void close( LogEvent log )
	{
		activeLogs.remove( log.id );
	}

	public static LogEvent logEvent( String id )
	{
		if ( activeLogs.containsKey( id ) )
			return ( LogEvent ) activeLogs.get( id ).get();

		LogRecord r = new LogRecord();
		LogEvent e = new LogEvent( id, r );
		activeLogs.put( id, new LogReference( id, r, e ) );
		return e;
	}

	private LogManager()
	{
		TaskManager.instance().runTaskAsynchronously( this, new Runnable()
		{
			@Override
			public void run()
			{
				for ( ;; )
					try
					{
						LogReference ref = ( LogReference ) referenceQueue.remove();
						for ( ;; )
						{
							activeLogs.remove( ref.key );
							ref.record.flush();
							ref = ( LogReference ) referenceQueue.remove();
						}
					}
					catch ( InterruptedException e )
					{
						// Do Nothing
					}
			}
		} );
	}

	@Override
	public String getName()
	{
		return "LogManager";
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.logger;

import java.util.logging.Level;

public interface LogAPI
{
	void dev( String format, Object... var1 );

	void debug( String format, Object... args );

	void debug( String msg, Throwable t );

	void fine( String var1 );

	void finer( String var1 );

	void finest( String var1 );

	String getId();

	void info( String s );

	void info( String format, Object... arguments );

	void info( Throwable t );

	void notice( String s );

	void log( Level l, String msg );

	void log( Level level, String msg, Object... params );

	void log( Level l, String msg, Throwable t );

	void panic( String var1 );

	void panic( Throwable e );

	void panic( String var1, Object... objs );

	void panic( String var1, Throwable t );

	void severe( String s );

	void severe( String s, Throwable t );

	void severe( Throwable t );

	void severe( String s, Object... objs );

	void warning( String s );

	void warning( String s, Object... objs );

	void warning( String s, Throwable t );
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.logger;

import java.util.logging.Level;

public interface LogAPI
{
	void dev( String format, Object... var1 );

	void debug( String format, Object... args );

	void debug( String msg, Throwable t );

	void fine( String var1 );

    void fine( String var1, Object... args );

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

package com.chiorichan.logger;

import java.util.logging.Level;

public interface LogAPI
{
	void debug( Object... var1 );

	void fine( String var1 );

	void finer( String var1 );

	void finest( String var1 );

	String getId();

	void highlight( String msg );

	void info( String s );

	void log( Level l, String msg );

	void log( Level level, String msg, Object... params );

	void log( Level l, String msg, Throwable t );

	void panic( String var1 );

	void panic( Throwable e );

	void severe( String s );

	void severe( String s, Throwable t );

	void severe( Throwable t );

	void warning( String s );

	void warning( String s, Object... objs );

	void warning( String s, Throwable t );

	void exceptions( Throwable... throwables );

}

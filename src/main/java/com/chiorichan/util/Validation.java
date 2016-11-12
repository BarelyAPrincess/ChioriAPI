package com.chiorichan.util;

public class Validation
{
	public static <T> T notNull( T var )
	{
		if ( var == null )
			throw new NullPointerException( "Variable can not be null!" );
		return var;
	}

	public static <T> T notNull( T var, String msg, Object... objs )
	{
		if ( var == null )
			throw new NullPointerException( String.format( msg, objs ) );
		return var;
	}
}

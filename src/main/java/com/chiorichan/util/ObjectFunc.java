/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;

import com.chiorichan.lang.StartupException;
import com.google.common.base.Joiner;

public class ObjectFunc<T>
{
	@SuppressWarnings( "unchecked" )
	public static <O> O castThis( Class<?> clz, Object o )
	{
		try
		{
			if ( clz == Integer.class )
				return ( O ) castToIntWithException( o );
			if ( clz == Long.class )
				return ( O ) castToLongWithException( o );
			if ( clz == Double.class )
				return ( O ) castToDoubleWithException( o );
			if ( clz == Boolean.class )
				return ( O ) castToBoolWithException( o );
			if ( clz == String.class )
				return ( O ) castToStringWithException( o );
		}
		catch ( Exception e1 )
		{
			try
			{
				return ( O ) o;
			}
			catch ( Exception e2 )
			{
				try
				{
					return ( O ) castToStringWithException( o );
				}
				catch ( Exception e3 )
				{
					try
					{
						/*
						 * Last and final attempt to get something out of this
						 * object even if it results in the toString() method.
						 */
						return ( O ) ( "" + o );
					}
					catch ( Exception e4 )
					{

					}
				}
			}
		}

		return null;
	}

	public static Boolean castToBool( Object value )
	{
		try
		{
			return castToBoolWithException( value );
		}
		catch ( Exception e )
		{
			return false;
		}
	}

	public static Boolean castToBoolWithException( Object value ) throws ClassCastException
	{
		if ( value == null )
			throw new ClassCastException( "Can't cast `null` to Boolean" );

		if ( value.getClass() == boolean.class || value.getClass() == Boolean.class )
			return ( boolean ) value;

		String val = castToStringWithException( value );

		if ( val == null )
			throw new ClassCastException( "Uncaught Convertion to Boolean of Type: " + value.getClass().getName() );

		switch ( val.trim().toLowerCase() )
		{
			case "yes":
				return true;
			case "no":
				return false;
			case "true":
				return true;
			case "false":
				return false;
			case "1":
				return true;
			case "0":
				return false;
			default:
				throw new ClassCastException( "Uncaught Convertion to Boolean of Type: " + value.getClass().getName() );
		}
	}

	public static Double castToDouble( Object value )
	{
		try
		{
			return castToDoubleWithException( value );
		}
		catch ( Exception e )
		{
			return 0D;
		}
	}

	public static Double castToDoubleWithException( Object value )
	{
		if ( value == null )
			throw new ClassCastException( "Can't cast `null` to Double" );

		if ( value instanceof Long )
			return ( ( Long ) value ).doubleValue();
		if ( value instanceof String )
			return Double.parseDouble( ( String ) value );
		if ( value instanceof Integer )
			return ( ( Integer ) value ).doubleValue();
		if ( value instanceof Double )
			return ( Double ) value;
		if ( value instanceof Boolean )
			return ( boolean ) value ? 1D : 0D;
		if ( value instanceof BigDecimal )
			return ( ( BigDecimal ) value ).setScale( 0, BigDecimal.ROUND_HALF_UP ).doubleValue();

		throw new ClassCastException( "Uncaught Convertion to Integer of Type: " + value.getClass().getName() );
	}

	public static Integer castToInt( Object value )
	{
		try
		{
			return castToIntWithException( value );
		}
		catch ( Exception e )
		{
			return -1;
		}
	}

	public static Integer castToIntWithException( Object value )
	{
		if ( value == null )
			throw new ClassCastException( "Can't cast `null` to Integer" );

		if ( value instanceof Long )
			if ( ( long ) value < Integer.MIN_VALUE || ( long ) value > Integer.MAX_VALUE )
				return ( Integer ) value;
			else
				return null;
		if ( value instanceof String )
			return Integer.parseInt( ( String ) value );
		if ( value instanceof Integer )
			return ( Integer ) value;
		if ( value instanceof Double )
			return ( Integer ) value;
		if ( value instanceof Boolean )
			return ( boolean ) value ? 1 : 0;
		if ( value instanceof BigDecimal )
			return ( ( BigDecimal ) value ).setScale( 0, BigDecimal.ROUND_HALF_UP ).intValue();

		throw new ClassCastException( "Uncaught Convertion to Integer of Type: " + value.getClass().getName() );
	}

	public static Long castToLong( Object value )
	{
		try
		{
			return castToLongWithException( value );
		}
		catch ( ClassCastException e )
		{
			e.printStackTrace();
			return 0L;
		}
	}

	public static Long castToLongWithException( Object value )
	{
		if ( value == null )
			throw new ClassCastException( "Can't cast `null` to Long" );

		if ( value instanceof Long )
			return ( Long ) value;
		if ( value instanceof String )
			return Long.parseLong( ( String ) value );
		if ( value instanceof Integer )
			return Long.parseLong( "" + value );
		if ( value instanceof Double )
			return Long.parseLong( "" + value );
		if ( value instanceof Boolean )
			return ( boolean ) value ? 1L : 0L;
		if ( value instanceof BigDecimal )
			return ( ( BigDecimal ) value ).setScale( 0, BigDecimal.ROUND_HALF_UP ).longValue();

		throw new ClassCastException( "Uncaught Convertion to Long of Type: " + value.getClass().getName() );
	}

	public static String castToString( Object value )
	{
		try
		{
			return castToStringWithException( value );
		}
		catch ( ClassCastException e )
		{
			return null;
		}
	}

	@SuppressWarnings( "rawtypes" )
	public static String castToStringWithException( Object value ) throws ClassCastException
	{
		if ( value == null )
			return null;
		if ( value instanceof Long )
			return Long.toString( ( long ) value );
		if ( value instanceof String )
			return ( String ) value;
		if ( value instanceof Integer )
			return Integer.toString( ( int ) value );
		if ( value instanceof Double )
			return Double.toString( ( double ) value );
		if ( value instanceof Boolean )
			return ( boolean ) value ? "true" : "false";
		if ( value instanceof BigDecimal )
			return ( ( BigDecimal ) value ).toString();
		if ( value instanceof Map )
			return Joiner.on( "," ).withKeyValueSeparator( "=" ).join( ( Map ) value );
		if ( value instanceof List )
			return Joiner.on( "," ).join( ( List ) value );
		throw new ClassCastException( "Uncaught Convertion to String of Type: " + value.getClass().getName() );
	}

	public static String hex2Readable( byte... elements )
	{
		// TODO Char Dump
		String result = "";
		char[] chars = Hex.encodeHex( elements, true );
		for ( int i = 0; i < chars.length; i = i + 2 )
			result += " " + chars[i] + chars[i + 1];

		if ( result.length() > 0 )
			result = result.substring( 1 );

		return result;
	}

	public static String hex2Readable( int... elements )
	{
		byte[] e2 = new byte[elements.length];
		for ( int i = 0; i < elements.length; i++ )
			e2[i] = ( byte ) elements[i];
		return hex2Readable( e2 );
	}

	public static <T> T initClass( Class<T> clz, Object... args ) throws StartupException
	{
		try
		{
			Constructor<T> constructor = clz.getConstructor();
			return constructor.newInstance();
		}
		catch ( NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e )
		{
			String argClasses = Joiner.on( ", " ).join( new ArrayList<String>()
			{
				{
					for ( Object o : args )
						add( o.getClass().getSimpleName() );
				}
			} );
			if ( argClasses.length() == 0 )
				argClasses = "None";
			throw new StartupException( String.format( "Failed to initalize a new instance of %s, does the class have a constructor to match arguments '%s'?", clz.getSimpleName(), argClasses ), e );
		}
	}

	public static Boolean isNull( Object o )
	{
		if ( o == null )
			return true;

		return false;
	}

	public static int safeLongToInt( long l )
	{
		if ( l < Integer.MIN_VALUE )
			return Integer.MIN_VALUE;
		if ( l > Integer.MAX_VALUE )
			return Integer.MAX_VALUE;
		return ( int ) l;
	}

	@SuppressWarnings( {"unchecked", "unused"} )
	public boolean instanceOf( Object obj )
	{
		try
		{
			T testCast = ( T ) obj;
			return true;
		}
		catch ( ClassCastException e )
		{
			return false;
		}
	}
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.terminal;

/**
 * Thrown when an unhandled exception occurs during the execution of a Command
 */
@SuppressWarnings( "serial" )
public class CommandException extends RuntimeException
{
	/**
	 * Creates a new instance of <code>CommandException</code> without detail message.
	 */
	public CommandException()
	{
	}
	
	/**
	 * Constructs an instance of <code>CommandException</code> with the specified detail message.
	 * 
	 * @param msg
	 *            the detail message.
	 */
	public CommandException( String msg )
	{
		super( msg );
	}
	
	public CommandException( String msg, Throwable cause )
	{
		super( msg, cause );
	}
}

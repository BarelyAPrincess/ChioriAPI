/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.messaging;

import com.chiorichan.event.EventException;

/**
 * Thrown for problems encountered within the MessageDispatch class
 */
public class MessageException extends Exception
{
	private static final long serialVersionUID = 6236409081662686334L;
	
	private final MessageSender sender;
	private final Object[] objs;
	
	public MessageException( String message, MessageSender sender, Object[] objs )
	{
		this( message, sender, objs, null );
	}
	
	public MessageException( String message, MessageSender sender, Object[] objs, EventException cause )
	{
		super( message, cause );
		
		this.sender = sender;
		this.objs = objs;
	}
	
	public Object[] getMessages()
	{
		return objs;
	}
	
	public MessageSender getSender()
	{
		return sender;
	}
}

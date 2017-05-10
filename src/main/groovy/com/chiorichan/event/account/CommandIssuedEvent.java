/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.event.account;

import com.chiorichan.account.AccountAttachment;
import com.chiorichan.event.Cancellable;

/**
 * Command Issued Event
 */
public class CommandIssuedEvent extends AccountEvent implements Cancellable
{
	private final AccountAttachment handler;
	private String command;
	private boolean cancelled;

	public CommandIssuedEvent( final String command, final AccountAttachment handler )
	{
		this.command = command;
		this.handler = handler;
	}

	/**
	 * Gets the command that the user is attempting to execute from the console
	 *
	 * @return Command the user is attempting to execute
	 */
	public String getCommand()
	{
		return command;
	}

	/**
	 * Get the command sender.
	 *
	 * @return The sender
	 */
	public AccountAttachment getPermissible()
	{
		return handler;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled( boolean cancel )
	{
		cancelled = cancel;
	}

	/**
	 * Sets the command that the server will execute
	 *
	 * @param message
	 *             New message that the server will execute
	 */
	public void setCommand( String message )
	{
		this.command = message;
	}
}

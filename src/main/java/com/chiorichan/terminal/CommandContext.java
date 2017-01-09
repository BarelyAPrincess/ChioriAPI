/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.terminal;

/**
 * References an issued command.
 */
public class CommandContext
{
	protected final String command;
	protected final Terminal terminal;
	
	public CommandContext( Terminal terminal, String command )
	{
		this.terminal = terminal;
		this.command = command;
	}
}

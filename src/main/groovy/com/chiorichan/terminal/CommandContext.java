/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
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

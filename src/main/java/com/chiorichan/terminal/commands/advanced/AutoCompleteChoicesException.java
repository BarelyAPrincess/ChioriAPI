/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.terminal.commands.advanced;

public class AutoCompleteChoicesException extends RuntimeException
{
	private static final long serialVersionUID = -8163025621439288595L;
	
	protected String argName;
	protected String[] choices;
	
	public AutoCompleteChoicesException( String[] choices, String argName )
	{
		super();
		this.choices = choices;
		this.argName = argName;
	}
	
	public String getArgName()
	{
		return argName;
	}
	
	public String[] getChoices()
	{
		return choices;
	}
}

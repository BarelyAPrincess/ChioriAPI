/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.terminal;

import com.chiorichan.account.lang.AccountResult;

/**
 * Represents a terminal connection end-point
 */
public interface TerminalHandler
{
	public enum TerminalType
	{
		LOCAL, TELNET, WEBSOCKET;
	}
	
	String getIpAddr();
	
	AccountResult kick( String reason );
	
	boolean disconnect();
	
	void println( String... msg );
	
	void print( String... msg );
	
	TerminalType type();
}

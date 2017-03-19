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
 * Represents entities with the ability to execute commands through the {@link CommandDispatch}
 */
public interface CommandSender
{
	String getVariable( String key );
	
	String getVariable( String key, String def );
	
	void setVariable( String key, String val );
}

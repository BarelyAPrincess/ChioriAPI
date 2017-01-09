/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.terminal.commands.advanced;

import com.chiorichan.terminal.commands.AdvancedCommand;

public interface CommandListener
{
	void onRegistered( AdvancedCommand command );
}

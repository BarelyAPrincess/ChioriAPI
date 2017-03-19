/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.permission;

import com.chiorichan.permission.commands.EntityCommands;
import com.chiorichan.permission.commands.GroupCommands;
import com.chiorichan.permission.commands.PermissionCommands;
import com.chiorichan.permission.commands.PromotionCommands;
import com.chiorichan.permission.commands.ReferenceCommands;
import com.chiorichan.permission.commands.UtilityCommands;
import com.chiorichan.terminal.commands.AdvancedCommand;

public class PermissionCommand extends AdvancedCommand
{
	public PermissionCommand()
	{
		super( "pex" );
		setAliases( "perms" );
		
		register( new GroupCommands() );
		register( new PromotionCommands() );
		register( new EntityCommands() );
		register( new UtilityCommands() );
		register( new ReferenceCommands() );
		register( new PermissionCommands() );
	}
}

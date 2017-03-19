/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.permission.event;

public class PermissibleSystemEvent extends PermissibleEvent
{
	public enum Action
	{
		BACKEND_CHANGED, RELOADED, WORLDINHERITANCE_CHANGED, DEFAULTGROUP_CHANGED, DEBUGMODE_TOGGLE, REINJECT_PERMISSIBLES,
	}
	
	protected Action action;
	
	public PermissibleSystemEvent( Action action )
	{
		super( action.toString() );
		
		this.action = action;
	}
	
	public Action getAction()
	{
		return action;
	}
}

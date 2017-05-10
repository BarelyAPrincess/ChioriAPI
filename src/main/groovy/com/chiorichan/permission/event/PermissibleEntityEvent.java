/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.permission.event;

import com.chiorichan.permission.PermissibleEntity;

public class PermissibleEntityEvent extends PermissibleEvent
{
	public enum Action
	{
		PERMISSIONS_CHANGED, OPTIONS_CHANGED, INHERITANCE_CHANGED, INFO_CHANGED, TIMEDPERMISSION_EXPIRED, RANK_CHANGED, DEFAULTGROUP_CHANGED, WEIGHT_CHANGED, SAVED, REMOVED, TIMEDGROUP_EXPIRED,
	}
	
	protected PermissibleEntity entity;
	protected Action action;
	
	public PermissibleEntityEvent( PermissibleEntity entity, Action action )
	{
		super( action.toString() );
		
		this.entity = entity;
		this.action = action;
	}
	
	public Action getAction()
	{
		return action;
	}
	
	public PermissibleEntity getEntity()
	{
		return entity;
	}
}

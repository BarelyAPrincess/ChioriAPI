/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package com.chiorichan.helpers;

import com.chiorichan.permission.Permission;
import com.chiorichan.permission.PermissionManager;
import com.chiorichan.permission.PermissionType;

/**
 * Extends the base {@link Namespace} and adds permission specific methods
 */
public class PermissionNamespace extends Namespace
{
	public PermissionNamespace( String... namespace )
	{
		super( namespace );
	}

	public PermissionNamespace( String namespace )
	{
		super( namespace );
	}

	public Permission createPermission()
	{
		return PermissionManager.instance().createNode( getString() );
	}

	public Permission createPermission( PermissionType type )
	{
		return PermissionManager.instance().createNode( getString(), type );
	}

	public Permission getPermission()
	{
		return PermissionManager.instance().getNode( getString() );
	}

	public boolean matches( Permission perm )
	{
		return matches( perm.getNamespace() );
	}
}

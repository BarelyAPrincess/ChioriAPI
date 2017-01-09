/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.permission.backend.memory;

import java.util.Collection;

import com.chiorichan.permission.PermissibleEntity;
import com.chiorichan.permission.PermissibleGroup;
import com.chiorichan.permission.Permission;
import com.chiorichan.permission.PermissionBackend;
import com.chiorichan.permission.PermissionManager;
import com.chiorichan.permission.References;
import com.chiorichan.permission.lang.PermissionBackendException;
import com.google.common.collect.Lists;

/*
 * Memory Backend
 * Zero Persistence. Does not attempt to save any changes.
 */
public class MemoryBackend extends PermissionBackend
{
	private static MemoryBackend backend;

	public static MemoryBackend getBackend()
	{
		return backend;
	}

	public MemoryBackend()
	{
		super();
		backend = this;
	}

	@Override
	public void commit()
	{
		// Nothing to do here!
	}

	@Override
	public PermissibleGroup getDefaultGroup( References refs )
	{
		return PermissionManager.instance().getGroup( "Default" );
	}

	@Override
	public PermissibleEntity getEntity( String name )
	{
		return new MemoryEntity( name );
	}

	@Override
	public Collection<String> getEntityNames()
	{
		return Lists.newArrayList();
	}

	@Override
	public Collection<String> getEntityNames( int type )
	{
		return Lists.newArrayList();
	}

	@Override
	public PermissibleGroup getGroup( String name )
	{
		return new MemoryGroup( name );
	}

	@Override
	public Collection<String> getGroupNames()
	{
		return Lists.newArrayList();
	}

	@Override
	public void initialize() throws PermissionBackendException
	{
		// Nothing to do here!
	}

	@Override
	public void loadEntities()
	{
		// Nothing to do here!
	}

	@Override
	public void loadGroups()
	{
		// Nothing to do here!
	}

	@Override
	public void loadPermissions()
	{
		// Nothing to do here!
	}

	@Override
	public void nodeCommit( Permission perm )
	{
		PermissionManager.getLogger().fine( "MemoryPermission nodes can not be saved. Sorry for the inconvinence. Might you consider changing permission backends. :(" );
	}

	@Override
	public void nodeDestroy( Permission perm )
	{
		// Nothing to do here!
	}

	@Override
	public void nodeReload( Permission perm )
	{
		// Nothing to do here!
	}

	@Override
	public void reloadBackend() throws PermissionBackendException
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void setDefaultGroup( String child, References refs )
	{
		// Nothing to do here!
	}
}

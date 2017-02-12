/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.permission.backend.file;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;

import com.chiorichan.configuration.ConfigurationSection;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.permission.ChildPermission;
import com.chiorichan.permission.PermissibleEntity;
import com.chiorichan.permission.PermissibleGroup;
import com.chiorichan.permission.Permission;
import com.chiorichan.permission.PermissionManager;
import com.chiorichan.permission.PermissionType;
import com.chiorichan.permission.PermissionValue;
import com.chiorichan.permission.References;
import com.chiorichan.helpers.PermissionNamespace;

public class FileEntity extends PermissibleEntity
{
	public FileEntity( String entityId )
	{
		super( entityId );
	}

	@Override
	public void reloadGroups()
	{
		if ( isDebug() )
			PermissionManager.getLogger().info( EnumColor.YELLOW + "Groups being loaded for entity " + getId() );

		clearGroups();
		clearTimedGroups();

		ConfigurationSection groups = FileBackend.getBackend().permissions.getConfigurationSection( "entities." + getId() + ".groups" );
		if ( groups != null )
			for ( String key : groups.getKeys( false ) )
				addGroup0( PermissionManager.instance().getGroup( key ), References.format( groups.getString( key ) ) );
	}

	@Override
	public void reloadPermissions()
	{
		if ( isDebug() )
			PermissionManager.getLogger().info( EnumColor.YELLOW + "Permissions being loaded for entity " + getId() );

		ConfigurationSection permissions = FileBackend.getBackend().permissions.getConfigurationSection( "entities." + getId() + ".permissions" );
		clearPermissions();
		clearTimedPermissions();

		if ( permissions != null )
			for ( String ss : permissions.getKeys( false ) )
			{
				ConfigurationSection permission = permissions.getConfigurationSection( ss );
				PermissionNamespace ns = new PermissionNamespace( ss.replaceAll( "/", "." ) );

				if ( !ns.containsOnlyValidChars() )
				{
					PermissionManager.getLogger().warning( "We failed to add the permission %s to entity %s because it contained invalid characters, namespaces can only contain 0-9, a-z and _." );
					continue;
				}

				Collection<Permission> perms = ns.containsRegex() ? PermissionManager.instance().getNodes( ns ) : Arrays.asList( ns.createPermission() );

				for ( Permission perm : perms )
				{
					PermissionValue value = null;
					if ( permission.getString( "value" ) != null )
						value = perm.getModel().createValue( permission.getString( "value" ) );

					addPermission( new ChildPermission( this, perm, value, -1 ), References.format( permission.getString( "refs" ) ) );
				}
			}
	}

	@Override
	public void remove()
	{
		FileBackend.getBackend().permissions.getConfigurationSection( "entities", true ).set( getId(), null );
	}

	@Override
	public void save()
	{
		if ( isVirtual() )
			return;

		if ( isDebug() )
			PermissionManager.getLogger().info( EnumColor.YELLOW + "Entity " + getId() + " being saved to backend" );

		ConfigurationSection root = FileBackend.getBackend().permissions.getConfigurationSection( "entities." + getId(), true );

		Collection<ChildPermission> children = getChildPermissions( null );
		for ( ChildPermission child : children )
		{
			Permission perm = child.getPermission();
			ConfigurationSection sub = root.getConfigurationSection( "permissions." + perm.getNamespace().replaceAll( "\\.", "/" ), true );
			if ( perm.getType() != PermissionType.DEFAULT )
				sub.set( "value", child.getObject() );

			sub.set( "refs", child.getReferences().isEmpty() ? null : child.getReferences().join() );
		}

		Collection<Entry<PermissibleGroup, References>> groups = getGroupEntrys( null );
		for ( Entry<PermissibleGroup, References> entry : groups )
			root.set( "groups." + entry.getKey().getId(), entry.getValue().join() );
	}
}

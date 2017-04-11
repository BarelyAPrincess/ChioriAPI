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

import com.chiorichan.helpers.NamespaceBase;
import com.google.common.base.Splitter;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Extends the base {@link NamespaceBase} and adds permission specific methods
 */
public class PermissionNamespace extends NamespaceBase<PermissionNamespace>
{
	public static PermissionNamespace parseString( String namespace )
	{
		return parseString( namespace, null );
	}

	public static PermissionNamespace parseStringRegex( String namespace, String regex )
	{
		if ( namespace == null )
			namespace = "";
		if ( regex == null || regex.length() == 0 )
			regex = "\\.";
		return new PermissionNamespace( Splitter.on( Pattern.compile( regex ) ).splitToList( namespace ) );
	}

	public static PermissionNamespace parseString( String namespace, String separator )
	{
		if ( namespace == null )
			namespace = "";
		if ( separator == null || separator.length() == 0 )
			separator = ".";
		return new PermissionNamespace( Splitter.on( separator ).splitToList( namespace ) );
	}

	public PermissionNamespace( String[] nodes )
	{
		super( nodes );
	}

	public PermissionNamespace( List<String> nodes )
	{
		super( nodes );
	}

	public PermissionNamespace()
	{
		super();
	}

	@Override
	protected PermissionNamespace create( String[] nodes )
	{
		return new PermissionNamespace( nodes );
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

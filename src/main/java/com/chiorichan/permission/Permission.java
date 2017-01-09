/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.permission;

import com.chiorichan.ApplicationTerminal;
import com.chiorichan.account.AccountAttachment;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.permission.lang.PermissionException;
import com.chiorichan.util.Namespace;
import com.chiorichan.util.StringFunc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Permission class for each permission node
 */
public final class Permission implements Comparable<Permission>
{
	protected final List<Permission> children = new CopyOnWriteArrayList<>();
	protected final String localName;
	protected PermissionModelValue model;
	protected final Permission parent;

	public Permission( Namespace ns )
	{
		this( ns.getLocalName(), PermissionType.DEFAULT, ns.getNodeCount() <= 1 ? null : PermissionManager.instance().createNode( ns.getParent() ) );
	}

	public Permission( Namespace ns, PermissionType type )
	{
		this( ns.getLocalName(), type, ns.getNodeCount() <= 1 ? null : PermissionManager.instance().createNode( ns.getParent() ) );
	}

	public Permission( String localName )
	{
		this( localName, PermissionType.DEFAULT );
	}

	public Permission( String localName, Permission parent )
	{
		this( localName, PermissionType.DEFAULT, parent );
	}

	public Permission( String localName, PermissionType type )
	{
		this( localName, type, null );
	}

	public Permission( String localName, PermissionType type, Permission parent )
	{
		if ( !localName.matches( "[a-z0-9_]*" ) )
			throw new PermissionException( String.format( "The permission local name '%s' can only contain characters a-z, 0-9, and _.", localName ) );

		this.localName = localName;
		this.parent = parent;

		model = new PermissionModelValue( localName, type, this );
		PermissionManager.instance().addPermission( this );
	}

	public void addChild( Permission node )
	{
		children.add( node );
	}

	public void commit()
	{
		PermissionManager.instance().getBackend().nodeCommit( this );
	}

	@Override
	public int compareTo( Permission perm )
	{
		if ( getNamespace().equals( perm.getNamespace() ) )
			return 0;

		Namespace ns1 = getPermissionNamespace();
		Namespace ns2 = perm.getPermissionNamespace();

		int ln = Math.min( ns1.getNodeCount(), ns2.getNodeCount() );

		for ( int i = 0; i < ln; i++ )
			if ( !ns1.getNode( i ).equals( ns2.getNode( i ) ) )
				return ns1.getNode( i ).compareTo( ns2.getNode( i ) );

		return ns1.getNodeCount() > ns2.getNodeCount() ? -1 : 1;
	}

	public void debugPermissionStack( AccountAttachment sender, int deepth )
	{
		String spacing = deepth > 0 ? StringFunc.repeat( "      ", deepth - 1 ) + "|---> " : "";

		sender.sendMessage( String.format( "%s%s%s=%s", EnumColor.YELLOW, spacing, getLocalName(), model ) );

		deepth++;
		for ( Permission p : children )
			p.debugPermissionStack( sender, deepth );
	}

	public void debugPermissionStack( int deepth )
	{
		debugPermissionStack( ApplicationTerminal.terminal(), deepth );
	}

	public Permission getChild( String name )
	{
		for ( Permission node : children )
			if ( node.getLocalName().equals( name ) )
				return node;
		return null;
	}

	/**
	 * Returns the Permission Children of this Permission
	 *
	 * @return Permission Children
	 */
	public List<Permission> getChildren()
	{
		return Collections.unmodifiableList( children );
	}

	/**
	 * Returns all children of this
	 *
	 * @return List of Permission Children
	 */
	public List<Permission> getChildrenRecursive()
	{
		return getChildrenRecursive( false );
	}

	/**
	 * Returns all children of this
	 *
	 * @param includeParents Shall we include parent Permission of all children
	 * @return List of Permission Children
	 */
	public List<Permission> getChildrenRecursive( boolean includeParents )
	{
		List<Permission> result = new ArrayList<>();

		getChildrenRecursive( result, includeParents );

		return result;
	}

	private void getChildrenRecursive( List<Permission> result, boolean includeParents )
	{
		if ( includeParents || !hasChildren() )
			result.add( this );

		for ( Permission p : getChildren() )
			p.getChildrenRecursive( result, includeParents );
	}

	/**
	 * Returns the unique fully qualified name of this Permission
	 *
	 * @return Fully qualified name
	 */
	public String getLocalName()
	{
		return localName.toLowerCase();
	}

	/**
	 * Return the {@link PermissionModelValue} class instance
	 *
	 * @return {@link PermissionModelValue} class instance
	 */
	public PermissionModelValue getModel()
	{
		return model;
	}

	/**
	 * Returns the dynamic Permission Namespace
	 *
	 * @return The Permission Namespace as a string
	 */
	public String getNamespace()
	{
		String namespace = "";
		Permission curr = this;

		do
		{
			namespace = curr.getLocalName() + "." + namespace;
			curr = curr.getParent();
		}
		while ( curr != null );

		namespace = namespace.substring( 0, namespace.length() - 1 );
		return namespace;
	}

	public Permission getParent()
	{
		return parent;
	}

	/**
	 * Returns the {@link Namespace} class instance
	 *
	 * @return {@link Namespace} class instance
	 */
	public Namespace getPermissionNamespace()
	{
		return new Namespace( getNamespace() );
	}

	public PermissionType getType()
	{
		return model.getType();
	}

	public boolean hasChildren()
	{
		return children.size() > 0;
	}

	void setType( PermissionType type )
	{
		model = new PermissionModelValue( localName, type, this );
	}

	@Override
	public String toString()
	{
		return String.format( "Permission{name=%s,parent=%s,modelValue=%s}", getLocalName(), getParent(), model );
	}
}

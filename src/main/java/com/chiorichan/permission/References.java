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

import java.util.Iterator;
import java.util.Set;

import com.chiorichan.zutils.ZEncryption;
import com.chiorichan.zutils.ZStrings;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * Used to differentiate between each {@link Permission} reference union
 */
public class References implements Iterable<String>
{
	private final Set<String> refs = Sets.newTreeSet();
	
	References()
	{
		
	}
	
	public static References format( String... refs )
	{
		return new References().add( refs );
	}
	
	/**
	 * Merges two References together
	 * 
	 * @param refs
	 *            The Reference to be merged with this one
	 */
	public References add( References refs )
	{
		this.refs.addAll( refs.refs );
		return this;
	}
	
	public References add( String... refs )
	{
		if ( refs == null || refs.length == 0 )
			add( "" );
		else
			for ( String ref : refs )
				if ( ref == null )
					add( "" );
				else if ( ref.contains( "|" ) )
					add( ref.split( "|" ) );
				else if ( ref.contains( "," ) )
					add( ref.split( "," ) );
				else
					this.refs.add( ZStrings.removeInvalidChars( ref.toLowerCase() ) );
		return this;
	}
	
	public String hash()
	{
		return ZEncryption.md5( join() );
	}
	
	public boolean isEmpty()
	{
		return refs.isEmpty() || ( refs.size() == 1 && refs.contains( "" ) );
	}
	
	@Override
	public Iterator<String> iterator()
	{
		return refs.iterator();
	}
	
	public String join()
	{
		return Joiner.on( "," ).join( refs );
	}
	
	public boolean match( References refs )
	{
		// Null means all
		if ( refs == null )
			return true;
		for ( String ref : refs.refs )
			if ( this.refs.contains( ref ) )
				return true;
		// If we failed to find any of the specified references then we try for the default empty one
		return this.refs.contains( "" );
	}
	
	public References remove( References refs )
	{
		this.refs.removeAll( refs.refs );
		return this;
	}
	
	public References remove( String... refs )
	{
		for ( String ref : refs )
			this.refs.remove( ref.toLowerCase() );
		return this;
	}
	
	@Override
	public String toString()
	{
		return "References{" + join() + "}";
	}
}

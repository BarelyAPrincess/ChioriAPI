/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.util;

import java.util.Collection;

import com.google.common.collect.Lists;

/**
 * Used to cast a list of objects from an unknown type to a single target type
 */
public class CollectionCaster<V>
{
	Class<V> vClz;
	
	public CollectionCaster( Class<V> vClz )
	{
		this.vClz = vClz;
	}
	
	public Collection<V> castTypes( Collection<?> col )
	{
		Collection<V> newCol = Lists.newLinkedList();
		
		for ( Object e : col )
		{
			V v = ObjectFunc.castThis( vClz, e );
			
			if ( v != null )
				newCol.add( v );
		}
		
		return newCol;
	}
}

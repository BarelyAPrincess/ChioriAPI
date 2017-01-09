/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.util;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Maps;

/**
 * Used to cast a map of objects from an unknown type to a single target type
 */
public class MapCaster<K, V>
{
	Class<K> kClz;
	Class<V> vClz;
	
	public MapCaster( Class<K> kClz, Class<V> vClz )
	{
		this.kClz = kClz;
		this.vClz = vClz;
	}
	
	public static boolean containsKeys( Map<String, ?> origMap, Collection<String> keys )
	{
		for ( String key : keys )
			if ( origMap.containsKey( key ) )
				return true;
		return false;
	}
	
	public Map<K, V> castTypes( Map<?, ?> map )
	{
		Validate.notNull( map );
		
		Map<K, V> newMap = Maps.newLinkedHashMap();
		
		for ( Entry<?, ?> e : map.entrySet() )
		{
			K k = ObjectFunc.castThis( kClz, e.getKey() );
			V v = ObjectFunc.castThis( vClz, e.getValue() );
			
			if ( k != null && v != null )
				newMap.put( k, v );
		}
		
		return newMap;
	}
}

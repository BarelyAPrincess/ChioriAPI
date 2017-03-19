/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package com.chiorichan.utils;

import com.chiorichan.helpers.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UtilMaps
{
	private UtilMaps()
	{
	}

	public static <T> Map<Integer, List<T>> paginate( List<T> list, int perPage )
	{
		return IntStream.iterate( 0, i -> i + perPage ).limit( ( list.size() + perPage - 1 ) / perPage ).boxed().collect( Collectors.toMap( i -> i / perPage, i -> list.subList( i, Math.min( i + perPage, list.size() ) ) ) );
	}

	public static Map<String, Object> flattenMap( Map<String, Object> map )
	{
		Map<String, Object> result = new HashMap<>();
		flattenMap( result, "", map );
		return result;
	}

	private static void flattenMap( Map<String, Object> result, String path, Map<String, Object> map )
	{
		for ( Map.Entry<String, Object> entry : map.entrySet() )
		{
			String key = path.isEmpty() ? entry.getKey() : path + "/" + entry.getKey();

			if ( entry.getValue() instanceof Map )
				flattenMap( result, key, ( Map<String, Object> ) entry.getValue() );
			else
				result.put( key, entry.getValue() );
		}
	}

	/**
	 * Checks and converts the string key to an integer. Non-numeric keys are removed from the treemap.
	 *
	 * @param map The map to sort
	 * @param <T> The value type
	 * @return The sorted map as a TreeMap
	 */
	public static <T> Map<Integer, T> asNumericallySortedMap( final Map<String, T> map )
	{
		return new TreeMap<Integer, T>()
		{{
			for ( Map.Entry<String, T> entry : map.entrySet() )
			{
				if ( UtilNumbers.isNumber( entry.getKey() ) )
					put( Integer.parseInt( entry.getKey() ), entry.getValue() );
			}
		}};
	}

	public static <K, V> Map<K, V> newHashMap( K key, V val )
	{
		return new HashMap<K, V>()
		{
			{
				put( key, val );
			}
		};
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T first( Map<?, T> map )
	{
		if ( map.size() == 0 )
			return null;
		return ( T ) map.values().toArray()[0];
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T last( Map<?, T> map )
	{
		if ( map.size() == 0 )
			return null;
		return ( T ) map.values().toArray()[map.size() - 1];
	}

	public static <T> Map<String, T> indexMap( List<T> list )
	{
		AtomicInteger inx = new AtomicInteger();
		return list.stream().map( l -> new Pair<>( Integer.toString( inx.getAndIncrement() ), l ) ).collect( Collectors.toMap( p -> p.getKey(), p -> p.getValue() ) );
	}
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * An {@code OptionNameMap} which wraps and behaves like {@code HashMap}.
 * </p>
 */
public class SimpleOptionNameMap<V> implements OptionNameMap<V>
{
	private final Map<String, V> map = new HashMap<>();

	@Override
	public boolean contains( String key )
	{
		return map.containsKey( key );
	}

	@Override
	public V get( String key )
	{
		return map.get( key );
	}

	@Override
	public void put( String key, V newValue )
	{
		map.put( key, newValue );
	}

	@Override
	public void putAll( Iterable<String> keys, V newValue )
	{
		for ( String each : keys )
			map.put( each, newValue );
	}

	@Override
	public void remove( String key )
	{
		map.remove( key );
	}

	@Override
	public Map<String, V> toJavaUtilMap()
	{
		return new HashMap<>( map );
	}
}

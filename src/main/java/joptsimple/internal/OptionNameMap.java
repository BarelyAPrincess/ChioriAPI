/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple.internal;

import java.util.Map;

/**
 * Map-like interface for storing String-value pairs.
 *
 * @param <V>
 *             type of values stored in the map
 */
public interface OptionNameMap<V>
{
	boolean contains( String key );

	V get( String key );

	void put( String key, V newValue );

	void putAll( Iterable<String> keys, V newValue );

	void remove( String key );

	Map<String, V> toJavaUtilMap();
}

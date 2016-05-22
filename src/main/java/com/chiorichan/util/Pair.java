/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com> All Right Reserved.
 */
package com.chiorichan.util;

import java.util.Map;

/**
 * Represents a pair with a Key and Value Object
 */
public class Pair<K, V> implements Map.Entry<K, V>
{
	private K key;
	private V val;

	public Pair()
	{
		this.key = null;
		this.val = null;
	}

	public Pair( K key, V val )
	{
		this.key = key;
		this.val = val;
	}

	@Override
	public K getKey()
	{
		return key;
	}

	@Override
	public V getValue()
	{
		return val;
	}

	public void setKey( K key )
	{
		this.key = key;
	}

	@Override
	public V setValue( V val )
	{
		V old = this.val;
		this.val = val;
		return old;
	}
}

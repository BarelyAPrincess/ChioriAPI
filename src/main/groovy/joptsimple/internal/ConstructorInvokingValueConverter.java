/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple.internal;

import static joptsimple.internal.Reflection.instantiate;

import java.lang.reflect.Constructor;

import joptsimple.ValueConverter;

/**
 * @param <V>
 *             constraint on the type of values being converted to
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class ConstructorInvokingValueConverter<V> implements ValueConverter<V>
{
	private final Constructor<V> ctor;

	ConstructorInvokingValueConverter( Constructor<V> ctor )
	{
		this.ctor = ctor;
	}

	@Override
	public V convert( String value )
	{
		return instantiate( ctor, value );
	}

	@Override
	public String valuePattern()
	{
		return null;
	}

	@Override
	public Class<V> valueType()
	{
		return ctor.getDeclaringClass();
	}
}

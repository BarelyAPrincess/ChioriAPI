/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple.internal;

import static joptsimple.internal.Reflection.invoke;

import java.lang.reflect.Method;

import joptsimple.ValueConverter;

/**
 * @param <V>
 *             constraint on the type of values being converted to
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class MethodInvokingValueConverter<V> implements ValueConverter<V>
{
	private final Method method;
	private final Class<V> clazz;

	MethodInvokingValueConverter( Method method, Class<V> clazz )
	{
		this.method = method;
		this.clazz = clazz;
	}

	@Override
	public V convert( String value )
	{
		return clazz.cast( invoke( method, value ) );
	}

	@Override
	public String valuePattern()
	{
		return null;
	}

	@Override
	public Class<V> valueType()
	{
		return clazz;
	}
}

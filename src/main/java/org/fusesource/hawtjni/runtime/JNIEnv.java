/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.fusesource.hawtjni.runtime;

/**
 * <p>
 * This is a marker class. Methods that take this as an argument will receive that actual native 'JNIEnv *' value. Since this class cannot be instantiated, Java callers must pass null for the value.
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class JNIEnv
{
	private JNIEnv()
	{
	}
}

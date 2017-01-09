/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.fusesource.hawtjni.runtime;

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public enum FieldFlag
{
	/**
	 * Indicate that the item should not be generated. For example,
	 * custom natives are coded by hand.
	 */
	FIELD_SKIP,

	/**
	 * Indicate that the field represents a constant or global
	 * variable. It is expected that the java field will be declared
	 * static.
	 */
	CONSTANT,

	/**
	 * Indicate that the field is a pointer.
	 */
	POINTER_FIELD,
}

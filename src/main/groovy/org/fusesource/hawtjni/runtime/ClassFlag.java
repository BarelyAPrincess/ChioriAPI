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
public enum ClassFlag
{
	/**
	 * Indicate that the item should not be generated. For example,
	 * custom natives are coded by hand.
	 */
	CLASS_SKIP,

	/**
	 * Indicate that the platform source is in C++
	 */
	CPP,

	/**
	 * Indicate that this class will define a structure
	 */
	STRUCT,

	/**
	 * Indicate that structure name is a typedef (It should
	 * not be prefixed with 'struct' to reference it.)
	 */
	TYPEDEF,

	/**
	 * Indicate that the struct should get zeroed out before
	 * setting any of it's fields. Comes in handy when
	 * you don't map all the struct fields to java fields but
	 * still want the fields that are not mapped initialized.
	 */
	ZERO_OUT,
}

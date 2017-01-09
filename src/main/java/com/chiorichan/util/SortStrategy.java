/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.util;

/**
 * Provides a desired Sorting Strategy to utility classes
 */
enum SortStrategy
{
	/**
	 * If the map contains more than one key with the same value, predecessors will be overridden
	 */
	Default,
	/**
	 * Will increment keys to next available index to make room
	 */
	MoveNext,
	/**
	 * Will decrement keys to last available index to make room
	 */
	MovePrevious;
}

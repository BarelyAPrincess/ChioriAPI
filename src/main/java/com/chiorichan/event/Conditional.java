/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.event;

/**
 * Used when an event can finish early based on a conditional check<br>
 * Keep in mind that {@link EventPriority#MONITOR} will still fire regardless
 */
public interface Conditional
{
	/**
	 * Should we execute the next {@link RegisteredListener}
	 * 
	 * @param context
	 *            The next {@link RegisteredListener} in the event chain
	 * @return return true to execute
	 */
	boolean conditional( RegisteredListener context ) throws EventException;
}

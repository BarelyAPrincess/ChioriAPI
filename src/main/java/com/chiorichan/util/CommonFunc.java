/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.util;

public class CommonFunc
{
	/**
	 * Gets Epoch
	 * 
	 * @return The precise epoch
	 */
	@Deprecated
	public static int getEpoch()
	{
		return ( int ) ( System.currentTimeMillis() / 1000 );
	}
}

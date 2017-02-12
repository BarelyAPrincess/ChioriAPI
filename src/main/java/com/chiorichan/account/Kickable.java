/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.account;

import com.chiorichan.account.lang.AccountResult;

/**
 * Indicates kickable account logins
 */
public interface Kickable
{
	/**
	 * Attempts to kick Account from server
	 * 
	 * @param reason
	 *            The reason for kick
	 * @return Result of said kick attempt
	 */
	AccountResult kick( String reason );
	
	String getId();
}

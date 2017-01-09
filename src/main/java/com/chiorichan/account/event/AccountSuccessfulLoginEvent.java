/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.account.event;

import com.chiorichan.account.AccountMeta;
import com.chiorichan.account.AccountPermissible;
import com.chiorichan.account.lang.AccountResult;

/**
 * Fired when the Account was successfully logged in
 */
public class AccountSuccessfulLoginEvent extends AccountFailedLoginEvent
{
	AccountPermissible via;
	
	public AccountSuccessfulLoginEvent( AccountMeta meta, AccountPermissible via, AccountResult result )
	{
		super( meta, via, result );
		this.via = via;
	}
	
	public AccountPermissible getAccountPermissible()
	{
		return via;
	}
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.account.event;

import com.chiorichan.account.AccountMeta;
import com.chiorichan.account.AccountPermissible;
import com.chiorichan.account.lang.AccountResult;

/**
 * Fired when an Account login failed
 */
public class AccountFailedLoginEvent extends AccountEvent
{
	private AccountResult result;
	
	AccountFailedLoginEvent( AccountMeta acct, AccountPermissible via, AccountResult result )
	{
		super( acct, via );
		this.result = result;
	}
	
	public AccountFailedLoginEvent( AccountMeta acct, AccountResult result )
	{
		super( acct );
		this.result = result;
	}
	
	public AccountResult getResult()
	{
		return result;
	}
}

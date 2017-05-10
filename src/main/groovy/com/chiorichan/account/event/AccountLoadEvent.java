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

import com.chiorichan.account.Account;

/**
 * Fired when an Account is being loaded into memory
 */
public class AccountLoadEvent extends AccountEvent
{
	public AccountLoadEvent( Account acct )
	{
		super( acct );
	}
}

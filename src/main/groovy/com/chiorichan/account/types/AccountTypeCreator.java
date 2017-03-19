/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.account.types;

import com.chiorichan.account.AccountCreator;
import com.chiorichan.account.AccountMeta;
import com.chiorichan.account.lang.AccountException;
import com.chiorichan.event.Listener;

/**
 * Used as Account Type Creator
 */
public abstract class AccountTypeCreator implements Listener, AccountCreator
{
	@Override
	public void save( AccountMeta meta ) throws AccountException
	{
		save( meta.context() );
	}
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.account.auth;

import com.chiorichan.account.AccountMeta;
import com.chiorichan.account.AccountPermissible;
import com.chiorichan.account.AccountType;
import com.chiorichan.account.lang.AccountException;
import com.chiorichan.account.lang.AccountDescriptiveReason;

/**
 * Usually only used to authenticate the NONE login
 */
public final class NullAccountAuthenticator extends AccountAuthenticator
{
	class NullAccountCredentials extends AccountCredentials
	{
		NullAccountCredentials( AccountMeta meta )
		{
			super( NullAccountAuthenticator.this, AccountDescriptiveReason.LOGIN_SUCCESS, meta );
		}
	}
	
	NullAccountAuthenticator()
	{
		super( "null" );
	}
	
	@Override
	public AccountCredentials authorize( AccountMeta acct, AccountPermissible perm ) throws AccountException
	{
		if ( acct != AccountType.ACCOUNT_NONE )
			throw new AccountException( AccountDescriptiveReason.INCORRECT_LOGIN, acct );
		
		return new NullAccountCredentials( acct );
	}
	
	@Override
	public AccountCredentials authorize( AccountMeta acct, Object... credentials ) throws AccountException
	{
		if ( acct != AccountType.ACCOUNT_NONE )
			throw new AccountException( AccountDescriptiveReason.INCORRECT_LOGIN, acct );
		
		return new NullAccountCredentials( acct );
	}
}

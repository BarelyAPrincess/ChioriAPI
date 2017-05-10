/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.account.types;

import com.chiorichan.AppConfig;
import com.chiorichan.account.AccountContext;
import com.chiorichan.account.AccountCreator;
import com.chiorichan.account.AccountMeta;
import com.chiorichan.account.AccountType;
import com.chiorichan.account.lang.AccountException;

import java.util.ArrayList;
import java.util.List;

/**
 * Used as Account Type Creator
 */
public abstract class AccountTypeCreator implements AccountCreator
{
	private final List<String> additionalAccountFields = AppConfig.get().getStringList( "accounts.fields", new ArrayList<>() );

	@Override
	public List<String> getLoginKeys()
	{
		return additionalAccountFields;
	}

	@Override
	public void save( AccountMeta meta ) throws AccountException
	{
		save( meta.getContext() );
	}

	@Override
	public AccountContext createAccount( String locId, String acctId ) throws AccountException
	{
		AccountContext context = new AccountContextImpl( this, AccountType.SQL, acctId, locId );

		context.setValue( "numLoginFail", 0 );
		context.setValue( "lastLoginFail", 0 );
		context.setValue( "actkey", "" );

		save( context );
		return context;
	}
}

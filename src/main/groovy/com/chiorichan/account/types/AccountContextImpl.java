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

import java.util.Map;
import java.util.Map.Entry;

import com.chiorichan.account.AccountContext;
import com.chiorichan.account.AccountCreator;
import com.chiorichan.account.AccountMeta;
import com.chiorichan.account.AccountType;

/**
 * Implements the Account Context
 */
final class AccountContextImpl extends AccountContext
{
	AccountContextImpl( AccountCreator creator, AccountType type )
	{
		super( creator, type );
	}

	AccountContextImpl( AccountCreator creator, AccountType type, String acctId, String siteId )
	{
		super( creator, type, siteId, acctId );
	}

	AccountContextImpl( AccountCreator creator, AccountType type, String acctId, String siteId, boolean keepLoaded )
	{
		super( creator, type, siteId, acctId, keepLoaded );
	}

	void setAcctId( String acctId )
	{
		this.acctId = acctId;
	}

	void setLocationId( String locId )
	{
		this.locId = locId;
	}

	void setValues( Map<String, Object> meta )
	{
		for ( Entry<String, Object> entry : meta.entrySet() )
			if ( !AccountMeta.IGNORED_KEYS.contains( entry.getKey() ) )
				rawMeta.put( entry.getKey(), entry.getValue() );
	}
}

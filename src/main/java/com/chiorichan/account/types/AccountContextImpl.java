/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2015 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
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
		super( creator, type, acctId, siteId );
	}

	AccountContextImpl( AccountCreator creator, AccountType type, String acctId, String siteId, boolean keepLoaded )
	{
		super( creator, type, acctId, siteId, keepLoaded );
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

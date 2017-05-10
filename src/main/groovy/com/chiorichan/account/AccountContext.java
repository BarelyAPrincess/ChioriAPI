/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.chiorichan.account.auth.AccountCredentials;
import com.chiorichan.account.lang.AccountDescriptiveReason;
import com.chiorichan.account.lang.AccountException;
import com.google.common.collect.Maps;

/**
 * Provides context to an Accounts existence
 */
public class AccountContext
{
	/**
	 * Contains a list of keys that can be used to match logins
	 */
	protected final List<String> loginKeys;

	/**
	 * Used to remember the last instance of {@link AccountCredentials} used
	 */
	AccountCredentials credentials = null;

	protected final Map<String, Object> rawMeta = Maps.newHashMap();
	protected final AccountCreator creator;
	protected final AccountType type;
	private AccountMeta acct;
	protected boolean keepLoaded = false;
	protected String acctId = null;
	protected String locId = null;

	protected AccountContext( AccountCreator creator, AccountType type )
	{
		this.creator = creator;
		this.type = type;

		loginKeys = new ArrayList<String>( creator.getLoginKeys() );

		loginKeys.add( "acctId" );
	}

	protected AccountContext( AccountCreator creator, AccountType type, String locId, String acctId )
	{
		this( creator, type );
		this.acctId = acctId;
		this.locId = locId;
	}

	protected AccountContext( AccountCreator creator, AccountType type, String locId, String acctId, boolean keepLoaded )
	{
		this( creator, type, locId, acctId );
		this.keepLoaded = keepLoaded;
	}

	public AccountCreator creator()
	{
		return creator;
	}

	public AccountCredentials credentials()
	{
		return credentials;
	}

	public String getAcctId()
	{
		if ( acctId == null || acctId.isEmpty() )
			return "<Not Set>";
		return acctId;
	}

	public String getAcctIdWithException() throws AccountException
	{
		if ( acctId == null || acctId.isEmpty() )
			throw new AccountException( AccountDescriptiveReason.EMPTY_ID, AccountType.ACCOUNT_NONE );

		return acctId;
	}

	public String getLocId()
	{
		if ( locId == null || locId.isEmpty() )
			return "%";
		return locId;
	}

	public Object getValue( String key )
	{
		return rawMeta.get( key );
	}

	public Map<String, Object> getValues()
	{
		return Collections.unmodifiableMap( rawMeta );
	}

	public boolean keepLoaded()
	{
		return keepLoaded;
	}

	public AccountMeta meta()
	{
		if ( acct == null )
			acct = new AccountMeta( this );
		return acct;
	}

	public void save() throws AccountException
	{
		creator.save( this );
	}

	void setAccount( AccountMeta acct )
	{
		this.acct = acct;
	}

	public void setKeepLoaded( boolean keepLoaded )
	{
		this.keepLoaded = keepLoaded;
	}

	public void setValue( String key, Object value )
	{
		rawMeta.put( key, value );
	}

	public AccountType type()
	{
		return type;
	}
}

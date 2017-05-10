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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides an easy to use Account Resolver
 */
class AccountList implements Iterable<AccountMeta>
{
	private volatile List<AccountMeta> accounts = new CopyOnWriteArrayList<>();

	void clear()
	{
		accounts.clear();
	}

	@Override
	public Iterator<AccountMeta> iterator()
	{
		return accounts.iterator();
	}

	void put( AccountMeta meta )
	{
		// Prevents the overriding of the builtin Accounts
		if ( ( "none".equals( meta.getId() ) || "default".equals( meta.getId() ) || "root".equals( meta.getId() ) ) )
			return;

		for ( AccountMeta acct : accounts )
			if ( Objects.equals( acct.getLocId(), meta.getLocId() ) && Objects.equals( acct.getId(), meta.getId() ) )
				return;

		accounts.add( meta );
	}

	void remove( String locId, String acctId )
	{
		for ( AccountMeta acct : accounts )
			if ( Objects.equals( acct.getId(), acctId ) && ( "%".equals( locId ) || Objects.equals( acct.getLocation(), locId ) ) )
				accounts.remove( acct );
	}

	public Map<String, AccountMeta> map()
	{
		return accounts.stream().collect( Collectors.toMap( m -> m.getLocId() + "_" + m.getId(), m -> m ) );
	}

	public Stream<AccountMeta> stream()
	{
		return accounts.stream();
	}

	public List<AccountMeta> list()
	{
		return new ArrayList<>( accounts );
	}

}

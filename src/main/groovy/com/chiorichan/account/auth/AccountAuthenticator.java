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

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.chiorichan.account.AccountMeta;
import com.chiorichan.account.AccountPermissible;
import com.chiorichan.account.lang.AccountException;
import com.google.common.collect.Lists;

/**
 * References available Account Authenticators
 */
public abstract class AccountAuthenticator
{
	/**
	 * Holds reference to loaded Account Authenticators
	 */
	private static final List<AccountAuthenticator> authenticators = Lists.newArrayList();

	/**
	 * Typically only used for authenticating the NONE login
	 * This will fail for all other logins
	 */
	public static final NullAccountAuthenticator NULL = new NullAccountAuthenticator();

	/**
	 * Used to authenticate any Account that supports plain text passwords
	 */
	public static final PlainTextAccountAuthenticator PASSWORD = new PlainTextAccountAuthenticator();

	/**
	 * Typically only used to authenticate relogins, for security, token will change with each successful auth
	 */
	public static final OnetimeTokenAccountAuthenticator TOKEN = new OnetimeTokenAccountAuthenticator();

	public static List<AccountAuthenticator> getAuthenticators()
	{
		return Collections.unmodifiableList( authenticators );
	}

	@SuppressWarnings( "unchecked" )
	public static <T extends AccountAuthenticator> T byName( String name )
	{
		Validate.notEmpty( name );

		for ( AccountAuthenticator aa : authenticators )
			if ( name.equalsIgnoreCase( aa.name ) )
				return ( T ) aa;
		return null;
	}

	private String name;

	AccountAuthenticator( String name )
	{
		this.name = name;
		authenticators.add( this );
	}

	/**
	 * Used to resume a saved session login
	 *
	 * @param acct
	 *            The Account Meta
	 * @param perm
	 *            An instance of the {@link com.chiorichan.account.AccountAttachment}
	 * @return The authorized account credentials
	 */
	public abstract AccountCredentials authorize( AccountMeta acct, AccountPermissible perm ) throws AccountException;

	/**
	 * Used to check Account Credentials prior to creating the Account Instance
	 *
	 * @param acct
	 *            The Account Meta
	 * @param credentials
	 *            The Credentials to use for authentication
	 * @return An instance of the Account Credentials
	 */
	public abstract AccountCredentials authorize( AccountMeta acct, Object... credentials ) throws AccountException;
}

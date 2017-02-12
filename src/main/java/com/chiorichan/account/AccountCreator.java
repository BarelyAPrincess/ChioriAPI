/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.account;

import java.util.List;

import com.chiorichan.account.lang.AccountException;
import com.chiorichan.account.lang.AccountResult;
import com.chiorichan.permission.PermissibleEntity;

/**
 * Specifies which methods are required for a class to manage accounts
 */
public interface AccountCreator
{
	/**
	 * Attempts to save the supplied {@link AccountMeta}
	 *
	 * @param account The {@link AccountMeta} to save
	 * @throws AccountException If there are issues handling the account
	 */
	void save( AccountMeta account ) throws AccountException;

	/**
	 * Attempt to save the supplied {@link AccountContext}
	 *
	 * @param accountContext The {@link AccountContext} to save
	 * @throws AccountException If there are issues handling the account
	 */
	void save( AccountContext accountContext ) throws AccountException;

	/**
	 * Attempts to reload data for the supplied {@link AccountMeta}
	 *
	 * @param account The {@link AccountMeta} to reload
	 * @throws AccountException If there are issues handling the account
	 */
	void reload( AccountMeta account ) throws AccountException;

	/**
	 * Check if this Account Creator is enabled and functioning
	 *
	 * @return True if it is
	 */
	boolean isEnabled();

	/**
	 * Called by {@link AccountPermissible#login()} and {@link AccountPermissible#login(com.chiorichan.account.auth.AccountAuthenticator, String, Object...)} when a login failed to be successful
	 *
	 * @param meta   The {@link AccountMeta} involved in the login
	 * @param result What was the failed result
	 */
	void failedLogin( AccountMeta meta, AccountResult result );

	/**
	 * Called by {@link AccountPermissible#login()} and {@link AccountPermissible#login(com.chiorichan.account.auth.AccountAuthenticator, String, Object...)} when a login was successful
	 *
	 * @param meta The {@link AccountMeta} involved in the login
	 * @throws AccountException If there are issues handling the account
	 */
	void successLogin( AccountMeta meta ) throws AccountException;

	/**
	 * Called by {@link AccountMeta#AccountMeta(AccountContext)} when it finishes constructing it's class and getting reference to it's {@link PermissibleEntity}
	 *
	 * @param meta   The {@link AccountMeta} involved in the login
	 * @param entity The new {@link AccountPermissible} instance
	 */
	void successInit( AccountMeta meta, PermissibleEntity entity );

	/**
	 * Called by {@link AccountPermissible#login()} and {@link AccountPermissible#login(com.chiorichan.account.auth.AccountAuthenticator, String, Object...)} before the login is validated for an {@link AccountResult}
	 *
	 * @param meta        The {@link AccountMeta} involved in the login
	 * @param via         What {@link AccountPermissible} is trying to login this Account
	 * @param acctId      The Account Id
	 * @param credentials The credentials that are to be passed to the related credentials
	 * @throws AccountException If there are issues handling the account
	 */
	void preLogin( AccountMeta meta, AccountPermissible via, String acctId, Object... credentials ) throws AccountException;

	/**
	 * See {@link AccountMeta#getDisplayName()}
	 */
	String getDisplayName( AccountMeta meta );

	/**
	 * Returns what keys can be used to match a login to it's Account, e.g., phone, email
	 *
	 * @return List of keys
	 */
	List<String> getLoginKeys();

	/**
	 * Checks if any Accounts exist within this creator matching provided acctId.
	 * This method is called by the {@link AccountManager#generateAcctId(String)} method exclusively to generate a new Account Identifier.
	 *
	 * @param acctId The Account Id to check
	 * @return True is it exists
	 */
	boolean exists( String acctId );

	/**
	 * Create specified account within the {@link AccountType} data
	 *
	 * @param acctId The account Id associated with this new Account
	 * @param siteId The site Id associated with this new Account, e.g., % = All Sites
	 * @return The new AccountContext
	 * @throws AccountException If there are issues handling the account
	 */
	AccountContext createAccount( String acctId, String siteId ) throws AccountException;
}

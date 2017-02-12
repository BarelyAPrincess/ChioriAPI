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

import com.chiorichan.account.event.AccountLoadEvent;
import com.chiorichan.account.event.AccountLookupEvent;
import com.chiorichan.account.lang.AccountDescriptiveReason;
import com.chiorichan.account.lang.AccountException;
import com.chiorichan.event.EventBus;
import com.chiorichan.event.EventRegistrar;
import com.chiorichan.logger.LogSource;
import com.chiorichan.services.ServiceManager;
import com.chiorichan.services.ServiceProvider;
import com.chiorichan.tasks.TaskRegistrar;

/**
 * Handles the task and events between the {@link EventBus} and {@link AccountManager}
 */
public abstract class AccountEvents implements EventRegistrar, TaskRegistrar, ServiceProvider, ServiceManager, LogSource
{
	void fireAccountLoad( AccountMeta meta )
	{
		EventBus.instance().callEvent( new AccountLoadEvent( meta ) );
	}

	/**
	 * Tries to find the account from our cached account list.<br>
	 * Will also try and find the account within the Garbage Collection map to aid Memory Leaks.
	 *
	 * @param id
	 *             The acctId to try and retrieve.
	 * @return
	 *         The account found. Will return NULL is not found.
	 */
	AccountMeta fireAccountLookup( String acctId )
	{
		AccountLookupEvent event = new AccountLookupEvent( acctId );

		EventBus.instance().callEvent( event );

		if ( !event.getDescriptiveReason().getReportingLevel().isSuccess() )
		{
			AccountManager.getLogger().warning( event.getDescriptiveReason().getMessage() );
			return null;
		}

		if ( event.getContext() == null )
			return null;

		AccountMeta acct = new AccountMeta( event.getContext() );

		return acct;
	}

	AccountMeta fireAccountLookupWithException( String acctId ) throws AccountException
	{
		AccountLookupEvent event = new AccountLookupEvent( acctId );

		EventBus.instance().callEvent( event );

		if ( !event.getDescriptiveReason().getReportingLevel().isSuccess() )
			throw new AccountException( event.getDescriptiveReason(), acctId );

		if ( event.getContext() == null )
			throw new AccountException( AccountDescriptiveReason.INCORRECT_LOGIN, acctId );

		AccountMeta acct = new AccountMeta( event.getContext() );

		return acct;
	}
}

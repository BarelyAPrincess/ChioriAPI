/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.account.types;

import java.util.Arrays;
import java.util.List;

import com.chiorichan.ApplicationTerminal;
import com.chiorichan.account.AccountContext;
import com.chiorichan.account.AccountMeta;
import com.chiorichan.account.AccountPermissible;
import com.chiorichan.account.AccountType;
import com.chiorichan.account.event.AccountLoadEvent;
import com.chiorichan.account.event.AccountLookupEvent;
import com.chiorichan.account.lang.AccountResult;
import com.chiorichan.event.EventHandler;
import com.chiorichan.permission.PermissibleEntity;
import com.chiorichan.permission.PermissionDefault;
import com.chiorichan.permission.event.PermissibleEntityEvent;
import com.chiorichan.permission.event.PermissibleEntityEvent.Action;
import com.chiorichan.tasks.Timings;

/**
 * Handles Memory Accounts, e.g., Root and None
 */
public class MemoryTypeCreator extends AccountTypeCreator
{
	public static final MemoryTypeCreator INSTANCE = new MemoryTypeCreator();

	MemoryTypeCreator()
	{
		super();
	}

	@Override
	public AccountContext createAccount( String acctId, String siteId )
	{
		AccountContext context = new AccountContextImpl( this, AccountType.SQL, acctId, siteId );

		context.setValue( "date", Timings.epoch() );

		return context;
	}

	@Override
	public boolean exists( String acctId )
	{
		return "none".equals( acctId ) || "root".equals( acctId );
	}

	@Override
	public void failedLogin( AccountMeta meta, AccountResult result )
	{
		// Do Nothing
	}

	@Override
	public String getDisplayName( AccountMeta meta )
	{
		return null;
	}

	@Override
	public List<String> getLoginKeys()
	{
		return Arrays.asList( new String[] {} );
	}

	public AccountType getType()
	{
		return AccountType.MEMORY;
	}

	@Override
	public boolean isEnabled()
	{
		return true; // Always
	}

	@EventHandler( )
	public void onAccountLoadEvent( AccountLoadEvent event )
	{
		// Do Nothing
	}

	@EventHandler
	public void onAccountLookupEvent( AccountLookupEvent event )
	{
		// Do Nothing
	}

	@EventHandler
	public void onPermissibleEntityEvent( PermissibleEntityEvent event )
	{
		// We do this to prevent the root account from losing it's OP permission node

		if ( event.getAction() == Action.PERMISSIONS_CHANGED )
			if ( AccountType.isRootAccount( event.getEntity() ) )
			{
				event.getEntity().addPermission( PermissionDefault.OP.getNode(), true, null );
				event.getEntity().setVirtual( true );
			}
	}

	@Override
	public void preLogin( AccountMeta meta, AccountPermissible via, String acctId, Object... creds )
	{
		// Called before the NONE and ROOT Account logs in
	}

	@Override
	public void reload( AccountMeta account )
	{
		// Do Nothing
	}

	@Override
	public void save( AccountContext context )
	{
		// Do Nothing!
	}

	@Override
	public void successInit( AccountMeta meta, PermissibleEntity entity )
	{
		if ( meta.context().creator() == this && AccountType.isRootAccount( meta ) )
		{
			entity.addPermission( PermissionDefault.OP.getNode(), true, null );
			entity.setVirtual( true );
			meta.instance().registerAttachment( ApplicationTerminal.terminal() );
		}

		if ( meta.context().creator() == this && AccountType.isNoneAccount( meta ) )
			entity.setVirtual( true );
	}

	@Override
	public void successLogin( AccountMeta meta )
	{
		// Do Nothing
	}
}

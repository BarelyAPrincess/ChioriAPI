/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2015 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan;

import java.util.Collection;
import java.util.Scanner;

import com.chiorichan.account.AccountAttachment;
import com.chiorichan.account.AccountLocation;
import com.chiorichan.account.AccountInstance;
import com.chiorichan.account.AccountMeta;
import com.chiorichan.account.AccountPermissible;
import com.chiorichan.account.AccountType;
import com.chiorichan.account.auth.AccountAuthenticator;
import com.chiorichan.account.lang.AccountDescriptiveReason;
import com.chiorichan.account.lang.AccountException;
import com.chiorichan.account.lang.AccountResult;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.logger.Log;
import com.chiorichan.logger.LogSource;
import com.chiorichan.messaging.MessageSender;
import com.chiorichan.permission.PermissibleEntity;
import com.chiorichan.permission.lang.PermissionBackendException;
import com.chiorichan.util.ObjectFunc;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Provides the console output of the server.
 * We also attach the Root Account here
 */
public class ApplicationTerminal extends AccountPermissible implements AccountAttachment, LogSource
{
	public static final ApplicationTerminal INSTANCE = new ApplicationTerminal();
	private static boolean isInitialized = false;

	public static void init() throws PermissionBackendException
	{
		if ( isInitialized )
			throw new IllegalStateException( "The Application Terminal has already been initialized." );

		assert INSTANCE != null;

		INSTANCE.init0();

		isInitialized = true;
	}


	private ApplicationTerminal()
	{

	}

	@Override
	protected void failedLogin( AccountResult result )
	{
		// Do Nothing
	}

	@Override
	public AccountLocation getCollective()
	{
		return AccountType.ACCOUNT_ROOT.getCollective();
	}

	@Override
	public String getCollectiveId()
	{
		return AccountType.ACCOUNT_ROOT.getCollectiveId();
	}

	@Override
	public String getDisplayName()
	{
		return "Server Console";
	}

	@Override
	public PermissibleEntity getEntity()
	{
		return AccountType.ACCOUNT_ROOT.getEntity();
	}

	@Override
	public String getId()
	{
		return AccountType.ACCOUNT_ROOT.getId();
	}

	@Override
	public String getIpAddr()
	{
		return null;
	}

	@Override
	public Collection<String> getIpAddresses()
	{
		return Lists.newArrayList();
	}

	@Override
	public String getLoggerId()
	{
		return "Console";
	}

	@Override
	public AccountPermissible getPermissible()
	{
		return this;
	}

	@Override
	public String getVariable( String string )
	{
		return null;
	}

	@Override
	public String getVariable( String key, String def )
	{
		return def;
	}

	public void init0()
	{

	}

	@Override
	public AccountInstance instance()
	{
		return AccountType.ACCOUNT_ROOT.instance();
	}

	@Override
	public void login()
	{
		// Disabled and Ignored!
	}

	@Override
	public AccountResult login( AccountAuthenticator auth, String acctId, Object... credObjs )
	{
		return new AccountResult( acctId, AccountDescriptiveReason.FEATURE_DISABLED );
	}

	@Override
	public AccountResult loginWithException( AccountAuthenticator auth, String acctId, Object... credObjs ) throws AccountException
	{
		throw new AccountException( AccountDescriptiveReason.FEATURE_DISABLED, acctId );
	}

	@Override
	public AccountResult logout()
	{
		return new AccountResult( AccountType.ACCOUNT_NONE, AccountDescriptiveReason.FEATURE_DISABLED );
	}

	@Override
	public AccountMeta meta()
	{
		return AccountType.ACCOUNT_ROOT;
	}

	public void pause( String msg, int timeout )
	{
		int last = 100;
		do
		{
			if ( timeout / 1000 < last )
			{
				Log.get().info( EnumColor.GOLD + "" + EnumColor.NEGATIVE + String.format( msg, timeout / 1000 + 1 + " seconds" ).toUpperCase() + "/r" );
				last = timeout / 1000;
			}

			try
			{
				timeout = timeout - 250;
				Thread.sleep( 250 );
			}
			catch ( InterruptedException e )
			{
				e.printStackTrace();
			}
		}
		while ( timeout > 0 );
	}

	public String prompt( String msg, String... keys )
	{
		Scanner scanner = new Scanner( System.in );

		Log.get().highlight( msg );

		while ( true )
		{
			String key = scanner.next();

			for ( String s : keys )
				if ( key.equalsIgnoreCase( s ) || key.toUpperCase().startsWith( s.toUpperCase() ) )
				{
					scanner.close();
					return s;
				}

			Log.get().warning( key + " is not an available option, please press " + Joiner.on( "," ).join( keys ) + " to continue." );
		}
	}

	@Override
	public void sendMessage( MessageSender sender, Object... objs )
	{
		for ( Object obj : objs )
			try
			{
				Log.get( this ).info( sender.getDisplayName() + ": " + ObjectFunc.castToStringWithException( obj ) );
			}
			catch ( ClassCastException e )
			{
				Log.get( this ).info( sender.getDisplayName() + " sent object " + obj.getClass().getName() + " but we had no idea how to properly output it to your terminal." );
			}
	}

	@Override
	public void sendMessage( Object... objs )
	{
		for ( Object obj : objs )
			try
			{
				Log.get( this ).info( ObjectFunc.castToStringWithException( obj ) );
			}
			catch ( ClassCastException e )
			{
				Log.get( this ).info( "Received object " + obj.getClass().getName() + " but we had no idea how to properly output it to your terminal." );
			}
	}

	@Override
	public void setVariable( String key, String value )
	{
		// TODO New Empty Method
	}

	@Override
	protected void successfulLogin()
	{
		// Do Nothing
	}
}

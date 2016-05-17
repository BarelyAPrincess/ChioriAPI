/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Scanner;

import com.chiorichan.account.AccountAttachment;
import com.chiorichan.account.AccountInstance;
import com.chiorichan.account.AccountLocation;
import com.chiorichan.account.AccountMeta;
import com.chiorichan.account.AccountPermissible;
import com.chiorichan.account.AccountType;
import com.chiorichan.account.auth.AccountAuthenticator;
import com.chiorichan.account.lang.AccountDescriptiveReason;
import com.chiorichan.account.lang.AccountException;
import com.chiorichan.account.lang.AccountResult;
import com.chiorichan.lang.ApplicationException;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.logger.Log;
import com.chiorichan.logger.LogSource;
import com.chiorichan.messaging.MessageSender;
import com.chiorichan.permission.PermissibleEntity;
import com.chiorichan.services.AppManager;
import com.chiorichan.services.ServiceManager;
import com.chiorichan.util.FileFunc;
import com.chiorichan.util.ObjectFunc;
import com.chiorichan.util.Application;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Provides the console output of the server. We also attach the Root Account here
 */
public class ApplicationTerminal extends AccountPermissible implements AccountAttachment, LogSource, ServiceManager
{
	static
	{
		try
		{
			AppManager.manager( ApplicationTerminal.class ).init();
		}
		catch ( ApplicationException e )
		{
			AppController.handleExceptions( e );
		}
	}

	public static ApplicationTerminal terminal()
	{
		return AppManager.manager( ApplicationTerminal.class ).instance();
	}

	public ApplicationTerminal()
	{

	}

	@Override
	protected void failedLogin( AccountResult result )
	{
		// Do Nothing
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
	public AccountLocation getLocation()
	{
		return AccountType.ACCOUNT_ROOT.getLocation();
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

	@Override
	public void init() throws ApplicationException
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

	public void showBanner()
	{
		try
		{
			InputStream is = null;
			try
			{
				is = getClass().getClassLoader().getResourceAsStream( "com/chiorichan/banner.txt" );

				String[] banner = new String( FileFunc.inputStream2Bytes( is ) ).split( "\\n" );

				for ( String l : banner )
					Log.get().info( EnumColor.GOLD + l );

				Log.get().info( EnumColor.NEGATIVE + "" + EnumColor.GOLD + "Starting " + Application.getProduct() + " Version " + Application.getVersion() );
				Log.get().info( EnumColor.NEGATIVE + "" + EnumColor.GOLD + Application.getCopyright() );
			}
			finally
			{
				if ( is != null )
					is.close();
			}
		}
		catch ( IOException e )
		{

		}
	}

	@Override
	protected void successfulLogin()
	{
		// Do Nothing
	}
}

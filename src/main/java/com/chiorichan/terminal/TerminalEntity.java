/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.terminal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import com.chiorichan.AppController;
import com.chiorichan.account.AccountLocation;
import com.chiorichan.account.AccountManager;
import com.chiorichan.account.AccountPermissible;
import com.chiorichan.account.AccountType;
import com.chiorichan.account.LocationService;
import com.chiorichan.account.auth.AccountAuthenticator;
import com.chiorichan.account.lang.AccountException;
import com.chiorichan.account.lang.AccountResult;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.messaging.MessageSender;
import com.chiorichan.permission.PermissibleEntity;
import com.chiorichan.util.ObjectFunc;
import com.chiorichan.util.Versioning;

/**
 * Used to interact with commands and logs
 */
public abstract class TerminalEntity extends AccountPermissible implements Terminal
{
	protected TerminalHandler handler;
	private String prompt = "";

	public TerminalEntity( TerminalHandler handler )
	{
		this.handler = handler;

		try
		{
			loginWithException( AccountAuthenticator.NULL, AccountType.ACCOUNT_NONE.getId() );
		}
		catch ( AccountException e )
		{
			if ( e.getResult().hasCause() )
				e.getResult().getCause().printStackTrace();
			AccountManager.getLogger().severe( e.getMessage() );

			handler.kick( "Internal Server Error: " + e.getMessage() );
		}
	}

	public void displayWelcomeMessage()
	{
		handler.println( String.format( "%s%sWelcome to %s Version %s!", EnumColor.NEGATIVE, EnumColor.GOLD, Versioning.getProduct(), Versioning.getVersion() ) );
		handler.println( String.format( "%s%s%s", EnumColor.NEGATIVE, EnumColor.GOLD, Versioning.getCopyright() ) );
	}

	public abstract void finish();

	@Override
	public PermissibleEntity getEntity()
	{
		return meta().getEntity();
	}

	public TerminalHandler getHandler()
	{
		return handler;
	}

	@Override
	public String getIpAddr()
	{
		return handler.getIpAddr();
	}

	@Override
	public Collection<String> getIpAddresses()
	{
		return Arrays.asList( getIpAddr() );
	}

	@Override
	public AccountLocation getLocation()
	{
		return ( ( LocationService ) AppController.getService( AccountLocation.class ) ).getDefaultLocation();
	}

	@Override
	public final AccountPermissible getPermissible()
	{
		return this;
	}

	@Override
	public String getVariable( String key )
	{
		return getVariable( key, null );
	}

	@Override
	public AccountResult kick( String reason )
	{
		return handler.kick( EnumColor.AQUA + "You are being kicked for reason: " + EnumColor.RESET + reason );
	}

	@Override
	public void prompt()
	{
		handler.print( "\r" + prompt );
	}

	@Override
	public void resetPrompt()
	{
		try
		{
			prompt = EnumColor.GREEN + getId() + "@" + InetAddress.getLocalHost().getHostName() + EnumColor.RESET + ":" + EnumColor.BLUE + "~" + EnumColor.RESET + "$ ";
		}
		catch ( UnknownHostException e )
		{
			prompt = EnumColor.GREEN + getId() + "@localhost ~$ ";
		}

		prompt();
	}

	@Override
	public void sendMessage( MessageSender sender, Object... objs )
	{
		for ( Object obj : objs )
			try
			{
				handler.println( sender.getDisplayName() + ": " + ObjectFunc.castToStringWithException( obj ) );
			}
			catch ( ClassCastException e )
			{
				handler.println( sender.getDisplayName() + " sent object " + obj.getClass().getName() + " but we had no idea how to properly output it to your terminal." );
			}
	}

	@Override
	public void sendMessage( Object... objs )
	{
		for ( Object obj : objs )
			try
			{
				handler.println( ObjectFunc.castToStringWithException( obj ) );
			}
			catch ( ClassCastException e )
			{
				handler.println( "Received object " + obj.getClass().getName() + " but we had no idea how to properly output it to your terminal." );
			}
	}

	@Override
	public void setPrompt( String prompt )
	{
		if ( prompt != null )
			this.prompt = prompt;

		prompt();
	}

	@Override
	protected void successfulLogin()
	{
		// TODO Unregister from old login, i.e., NONE Account
		// Temp Fix until a better way is found, i.e., logout!
		AccountType.ACCOUNT_NONE.instance().unregisterAttachment( this );
		registerAttachment( this );
	}

	@Override
	public String toString()
	{
		return String.format( "TerminalEntity{EntityId=%s,ip=%s}", meta().getId(), getIpAddr() );
	}
}

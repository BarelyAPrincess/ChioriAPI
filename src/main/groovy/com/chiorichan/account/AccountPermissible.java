/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package com.chiorichan.account;

import com.chiorichan.AppConfig;
import com.chiorichan.account.auth.AccountAuthenticator;
import com.chiorichan.account.auth.AccountCredentials;
import com.chiorichan.account.event.AccountFailedLoginEvent;
import com.chiorichan.account.event.AccountPreLoginEvent;
import com.chiorichan.account.event.AccountSuccessfulLoginEvent;
import com.chiorichan.account.lang.AccountDescriptiveReason;
import com.chiorichan.account.lang.AccountException;
import com.chiorichan.account.lang.AccountResult;
import com.chiorichan.event.EventBus;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.lang.ReportingLevel;
import com.chiorichan.permission.Permissible;
import com.chiorichan.tasks.Timings;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Used on classes that can support Account Logins
 */
public abstract class AccountPermissible extends Permissible implements Account
{
	/**
	 * The logged in account associated with this session
	 */
	protected AccountInstance account = null;

	private boolean checkAccount()
	{
		if ( account == null )
			account = AccountType.ACCOUNT_NONE.instance();

		return !AccountType.isNoneAccount( account );
	}

	public final AccountInstance getAccount()
	{
		checkAccount();
		return account;
	}

	protected abstract void failedLogin( AccountResult result );

	@Override
	public String getDisplayName()
	{
		return instance().getDisplayName();
	}

	@Override
	public boolean isInitialized()
	{
		return account != null;
	}

	@Override
	public String getId()
	{
		if ( !isInitialized() )
			return null;

		return instance().getId();
	}

	/**
	 * Used by {@link #login()} and {@link #login(AccountAuthenticator, String, String, Object...)} to maintain persistent login information
	 *
	 * @param key The key to get
	 * @return The String result
	 */
	public abstract String getVariable( String key );

	/**
	 * See {@link #getVariable(String)}
	 *
	 * @param def Specifies a default value to return if the requested key is null
	 */
	public abstract String getVariable( String key, String def );

	/**
	 * Called from subclass once subclass has finished loading
	 */
	protected void initialized()
	{
		login();
	}

	@Override
	public AccountInstance instance()
	{
		return account;
	}

	/**
	 * Attempts to authenticate using saved Account Credentials
	 */
	public void login()
	{
		String authName = getVariable( "auth" );
		String locId = getVariable( "locId" );
		String acctId = getVariable( "acctId" );

		if ( authName != null && !authName.isEmpty() )
		{
			AccountAuthenticator auth = AccountAuthenticator.byName( authName );
			login( auth, locId, acctId, this );
		}
	}

	public AccountResult loginWithException( AccountAuthenticator auth, String locId, String acctId, Object... credObjs ) throws AccountException
	{
		AccountResult result = login( auth, locId, acctId, credObjs );

		if ( !result.isSuccess() )
			throw new AccountException( result );

		return result;
	}

	/**
	 * Attempts to authenticate the Account Id using the specified {@link AccountAuthenticator} and Credentials
	 *
	 * @param auth     The {@link AccountAuthenticator}
	 * @param acctId   The Account Id
	 * @param credObjs The Account Credentials. Exact credentials depend on what AccountAuthenticator was provided.
	 * @return The {@link AccountResult}
	 */
	public AccountResult login( AccountAuthenticator auth, String locId, String acctId, Object... credObjs )
	{
		AccountResult result = new AccountResult( locId, acctId );
		AccountMeta meta = null;

		try
		{
			if ( auth != null )
			{
				AccountManager.instance().resolveAccount( result );
				meta = result.getAccount();

				if ( meta == null )
					return result;

				meta.getContext().creator().preLogin( meta, this, acctId, credObjs );
				AccountPreLoginEvent event = new AccountPreLoginEvent( meta, this, acctId, credObjs );

				EventBus.instance().callEvent( event );

				if ( !event.getDescriptiveReason().getReportingLevel().isIgnorable() )
				{
					result.setReason( event.getDescriptiveReason() );
					return result;
				}

				AccountCredentials credentials = auth.authorize( meta, credObjs );
				meta.getContext().credentials = credentials;

				if ( credentials.getDescriptiveReason().getReportingLevel().isSuccess() )
				{
					result.setReason( AccountDescriptiveReason.LOGIN_SUCCESS );

					AccountInstance acct = meta.instance();

					if ( acct.countAttachments() > 1 && AppConfig.get().getBoolean( "accounts.singleLogin" ) )
						for ( AccountAttachment ap : acct.getAttachments() )
							if ( ap instanceof Kickable )
								( ( Kickable ) ap ).kick( AppConfig.get().getString( "accounts.singleLoginMessage", "You logged in from another location." ) );

					meta.set( "lastLogin", Timings.epoch() );

					// XXX Should we track all past IPs or only the current ones and what about local logins?
					Set<String> ips = Sets.newLinkedHashSet();
					if ( meta.getString( "lastLoginIp" ) != null )
						ips.addAll( Splitter.on( "|" ).splitToList( meta.getString( "lastLoginIp" ) ) );
					ips.addAll( getIpAddresses() );

					if ( ips.size() > 5 )
						meta.set( "lastLoginIp", Joiner.on( "|" ).join( new LinkedList<>( ips ).subList( ips.size() - 5, ips.size() ) ) );
					else if ( ips.size() > 0 )
						meta.set( "lastLoginIp", Joiner.on( "|" ).join( ips ) );
					setVariable( "acctId", meta.getId() );

					meta.save();

					account = acct;

					successfulLogin();
					meta.getContext().creator().successLogin( meta );
					EventBus.instance().callEvent( new AccountSuccessfulLoginEvent( meta, this, result ) );
				}
				else
					result.setReason( credentials.getDescriptiveReason() );
			}
			else
				result.setReason( new AccountDescriptiveReason( "The Authenticator was null!", ReportingLevel.L_ERROR ) );
		}
		catch ( AccountException e )
		{
			if ( e.getResult() == null )
			{
				result.setReason( e.getReason() );
				if ( e.hasCause() )
					result.setCause( e.getCause() );
			}
			else
				result = e.getResult();
		}
		catch ( Throwable t )
		{
			result.setCause( t );
			result.setReason( AccountDescriptiveReason.INTERNAL_ERROR );
		}

		if ( !result.isSuccess() )
		{
			failedLogin( result );
			if ( meta != null )
				meta.getContext().creator().failedLogin( meta, result );
			EventBus.instance().callEvent( new AccountFailedLoginEvent( meta, result ) );
		}

		if ( AccountManager.instance().isDebug() )
		{
			if ( !result.isIgnorable() && result.hasCause() )
				result.getCause().printStackTrace();

			AccountManager.getLogger().info( ( result.isSuccess() ? EnumColor.GREEN : EnumColor.YELLOW ) + "Session Login: [id='" + acctId + "',reason='" + result.getFormattedMessage() + "']" );
		}

		return result;
	}

	protected void registerAttachment( AccountAttachment attachment )
	{
		checkAccount();
		account.registerAttachment( attachment );
	}

	protected void unregisterAttachment( AccountAttachment attachment )
	{
		checkAccount();
		account.unregisterAttachment( attachment );
	}

	public abstract List<String> getIpAddresses();

	public AccountResult logout()
	{
		if ( !AccountType.isNoneAccount( account ) )
		{
			AccountManager.getLogger().info( EnumColor.GREEN + "Successful Logout: [id='" + account.getId() + "',locId='" + ( account.getLocation() == null ? "null" : account.getLocation().getId() ) + "',displayName='" + account.getDisplayName() + "',ipAddresses='" + account.getIpAddresses() + "']" );

			account = null;
			checkAccount();
			destroyEntity();

			setVariable( "auth", null );
			setVariable( "locId", null );
			setVariable( "acctId", null );
			setVariable( "token", null );

			return new AccountResult( account.meta(), AccountDescriptiveReason.LOGOUT_SUCCESS );
		}
		return new AccountResult( account.meta(), AccountDescriptiveReason.ACCOUNT_MISSING );
	}

	@Override
	public AccountMeta meta()
	{
		return instance().meta();
	}

	public abstract void setVariable( String key, String value );

	protected abstract void successfulLogin() throws AccountException;

	public boolean hasLogin()
	{
		return checkAccount();
	}
}

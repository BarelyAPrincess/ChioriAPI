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

import com.chiorichan.AppConfig;
import com.chiorichan.Versioning;
import com.chiorichan.account.lang.AccountDescriptiveReason;
import com.chiorichan.account.lang.AccountException;
import com.chiorichan.account.lang.AccountResolveResult;
import com.chiorichan.event.account.KickEvent;
import com.chiorichan.logger.Log;
import com.chiorichan.services.AppManager;
import com.chiorichan.utils.UtilEncryption;
import com.chiorichan.utils.UtilObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides Account Management to the Server
 */
public final class AccountManager extends AccountEvents
{
	public static Log getLogger()
	{
		return Log.get( instance() );
	}

	public static AccountManager instance()
	{
		return AppManager.manager( AccountManager.class ).instance();
	}

	public static AccountManager instanceWithoutException()
	{
		return AppManager.manager( AccountManager.class ).instanceWithoutException();
	}

	final AccountList accounts = new AccountList();

	private boolean isDebug = false;

	private int maxLogins = -1;

	public AccountManager()
	{

	}

	public AccountMeta createAccount( String locId ) throws AccountException
	{
		String acctId;

		do
			acctId = UtilEncryption.rand( 8, true, true );
		while ( accountExists( locId, acctId ) );

		return createAccount( locId, acctId );
	}

	public AccountMeta createAccount( String locId, String acctId ) throws AccountException
	{
		return createAccount( locId, acctId, AccountType.getDefaultType() );
	}

	public AccountMeta createAccount( String locId, String acctId, AccountType type ) throws AccountException
	{
		if ( !type.isEnabled() )
			throw new AccountException( AccountDescriptiveReason.FEATURE_DISABLED, locId, acctId );

		return new AccountMeta( type.getCreator().createAccount( locId, acctId ) );
	}

	public AccountMeta resolveAccount( String locId, String acctId )
	{
		try
		{
			return resolveAccountWithException( locId, acctId );
		}
		catch ( AccountException e )
		{
			return null;
		}
	}

	public AccountMeta resolveAccountWithException( String locId, String acctId ) throws AccountException
	{
		if ( "none".equals( acctId ) || "default".equals( acctId ) )
			return AccountType.ACCOUNT_NONE;

		if ( "root".equals( acctId ) )
			return AccountType.ACCOUNT_ROOT;

		for ( AccountMeta am : accounts )
			for ( String key : am.getContext().loginKeys )
				if ( acctId.equals( am.getString( key ) ) && ( "%".equals( locId ) || locId.equals( am.getLocId() ) ) )
					return am;

		for ( AccountType type : AccountType.getAccountTypes() )
		{
			AccountResolveResult result = type.getCreator().resolveAccount( locId, acctId );
			if ( result != null && result.getDescriptiveReason().getReportingLevel().isSuccess() )
			{
				AccountMeta meta = new AccountMeta( result.getContext() );
				accounts.put( meta );
				return meta;
			}
			else if ( result != null && !result.getDescriptiveReason().getReportingLevel().isIgnorable() )
			{
				throw new AccountException( result.getDescriptiveReason(), locId, acctId );
			}
		}

		throw new AccountException( AccountDescriptiveReason.INCORRECT_LOGIN, locId, acctId );
	}

	private boolean accountExists( String locId, String acctId )
	{
		if ( "none".equals( acctId ) || "default".equals( acctId ) || "root".equals( acctId ) )
			return true;

		for ( AccountMeta am : accounts )
			for ( String key : am.getContext().loginKeys )
				if ( acctId.equals( am.getString( key ) ) && ( "%".equals( locId ) || locId.equals( am.getLocId() ) ) )
					return true;

		for ( AccountType type : AccountType.getAccountTypes() )
			if ( type.getCreator().accountExists( locId, acctId ) )
				return true;
		return false;
	}

	@Deprecated
	public AccountMeta getAccountPartial( String partial ) throws AccountException
	{
		Validate.notNull( partial );

		AccountMeta found = null;
		String lowerName = partial.toLowerCase();
		int delta = Integer.MAX_VALUE;
		for ( AccountMeta meta : getAccounts() )
			if ( meta.getId().toLowerCase().startsWith( lowerName ) )
			{
				int curDelta = meta.getId().length() - lowerName.length();
				if ( curDelta < delta )
				{
					found = meta;
					delta = curDelta;
				}
				if ( curDelta == 0 )
					break;
			}
		return found;
	}

	@Deprecated
	public List<AccountMeta> getAccounts()
	{
		return Collections.unmodifiableList( getAccounts0() );
	}

	@Deprecated
	public List<AccountMeta> getAccounts( String query )
	{
		Validate.notNull( query );

		if ( query.contains( "|" ) )
		{
			List<AccountMeta> result = new ArrayList<>();
			for ( String s : Splitter.on( "|" ).split( query ) )
				if ( s != null && !s.isEmpty() )
					result.addAll( getAccounts( s ) );
			return result;
		}

		boolean isLower = query.toLowerCase().equals( query ); // Is query string all lower case?

		return accounts.stream().filter( m ->
		{
			String id = isLower ? m.getId().toLowerCase() : m.getId();
			if ( !UtilObjects.isEmpty( id ) && id.contains( query ) )
				return true;

			id = isLower ? m.getDisplayName().toLowerCase() : m.getDisplayName();
			return !UtilObjects.isEmpty( id ) && id.contains( query );

			// TODO Figure out how to further check these values.
			// Maybe send the check into the Account Creator
		} ).collect( Collectors.toList() );
	}

	@Deprecated
	public List<AccountMeta> getAccounts( String key, String value )
	{
		Validate.notNull( key );
		Validate.notNull( value );

		if ( value.contains( "|" ) )
		{
			List<AccountMeta> result = new ArrayList<>();
			for ( String s : Splitter.on( "|" ).split( value ) )
				if ( s != null && !s.isEmpty() )
					result.addAll( getAccounts( key, s ) );
			return result;
		}

		boolean isLower = value.toLowerCase().equals( value ); // Is query string all lower case?

		return accounts.stream().filter( m ->
		{
			String str = isLower ? m.getString( key ).toLowerCase() : m.getString( key );
			return !UtilObjects.isEmpty( str ) && str.contains( value );
		} ).collect( Collectors.toList() );
	}

	@Deprecated
	List<AccountMeta> getAccounts0()
	{
		return accounts.list();
	}

	public List<AccountMeta> getAccountsByLocation( AccountLocation loc )
	{
		Validate.notNull( loc );
		return accounts.stream().filter( m -> m.getLocation() == loc ).collect( Collectors.toList() );
	}

	public List<AccountMeta> getAccountsByLocation( String locId )
	{
		LocationService service = AppManager.getService( AccountLocation.class );
		return service == null ? null : getAccountsByLocation( service.getLocation( locId ) );
	}

	public Set<Account> getBanned()
	{
		Set<Account> accts = new HashSet<>();
		for ( AccountMeta meta : accounts )
			if ( meta.getPermissibleEntity().isBanned() )
				accts.add( meta );
		return accts;
	}

	public Set<Account> getInitializedAccounts()
	{
		Set<Account> accts = new HashSet<>();
		for ( AccountMeta meta : accounts )
			if ( meta.isInitialized() )
				accts.add( meta );
		return accts;
	}

	@Override
	public String getLoggerId()
	{
		return "AcctMgr";
	}

	@Override
	public String getName()
	{
		return "AccountManager";
	}

	public Set<Account> getOperators()
	{
		Set<Account> accts = new HashSet<>();
		for ( AccountMeta meta : accounts )
			if ( meta.getPermissibleEntity().isOp() )
				accts.add( meta );
		return accts;
	}

	/**
	 * Gets all Account Permissibles by crawling the {@link AccountMeta} and {@link AccountInstance}
	 *
	 * @return A set of AccountPermissibles
	 */
	public Collection<AccountAttachment> getPermissibles()
	{
		Set<AccountAttachment> accts = new HashSet<>();
		for ( AccountMeta meta : accounts )
			if ( meta.isInitialized() )
				accts.addAll( meta.instance().getAttachments() );
		return accts;
	}

	public Set<Account> getWhitelisted()
	{
		Set<Account> accts = new HashSet<>();
		for ( AccountMeta meta : accounts )
			if ( meta.getPermissibleEntity().isWhitelisted() )
				accts.add( meta );
		return accts;
	}

	@Override
	public void init()
	{
		isDebug = AppConfig.get().getBoolean( "accounts.debug" );
		maxLogins = AppConfig.get().getInt( "accounts.maxLogins", -1 );
	}

	public boolean isDebug()
	{
		return isDebug || Versioning.isDevelopment();
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	public void reload()
	{
		save();
		accounts.clear();
	}

	public void save()
	{
		for ( AccountMeta meta : accounts )
			try
			{
				meta.save();
			}
			catch ( AccountException e )
			{
				e.printStackTrace();
			}
	}

	public void shutdown( String reason )
	{
		try
		{
			Set<Kickable> kickables = Sets.newHashSet();
			for ( AccountMeta acct : getAccounts() )
				if ( acct.isInitialized() )
					for ( AccountAttachment attachment : acct.instance().getAttachments() )
						if ( attachment.getPermissible() instanceof Kickable )
							kickables.add( ( Kickable ) attachment.getPermissible() );
						else if ( attachment instanceof Kickable )
							kickables.add( ( Kickable ) attachment );

			KickEvent.kick( AccountType.ACCOUNT_ROOT, kickables ).setReason( reason ).fire();
		}
		catch ( Throwable t )
		{
			// Ignore
		}

		save();
	}
}

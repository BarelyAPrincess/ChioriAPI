/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package com.chiorichan.account;

import com.chiorichan.AppConfig;
import com.chiorichan.Versioning;
import com.chiorichan.account.lang.AccountDescriptiveReason;
import com.chiorichan.account.lang.AccountException;
import com.chiorichan.event.EventBus;
import com.chiorichan.event.account.KickEvent;
import com.chiorichan.logger.Log;
import com.chiorichan.services.AppManager;
import com.chiorichan.utils.UtilEncryption;
import com.chiorichan.utils.UtilObjects;
import com.chiorichan.utils.UtilStrings;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

	boolean isDebug = false;

	int maxAccounts = -1;

	public AccountManager()
	{

	}

	public AccountMeta createAccount( String acctId, String siteId ) throws AccountException
	{
		return createAccount( acctId, siteId, AccountType.getDefaultType() );
	}

	public AccountMeta createAccount( String acctId, String siteId, AccountType type ) throws AccountException
	{
		if ( !type.isEnabled() )
			throw new AccountException( AccountDescriptiveReason.FEATURE_DISABLED, acctId );

		AccountContext context = type.getCreator().createAccount( acctId, siteId );

		return new AccountMeta( context );
	}

	private boolean exists( String acctId )
	{
		if ( accounts.keySet().contains( acctId ) )
			return true;

		for ( AccountType type : AccountType.getAccountTypes() )
			if ( type.getCreator().exists( acctId ) )
				return true;
		return false;
	}

	public String generateAcctId( String seed )
	{
		String acctId = "";

		if ( seed == null || seed.isEmpty() )
			acctId = "ab123C";
		else
		{
			seed = seed.replaceAll( "[\\W\\d]", "" );

			acctId = UtilStrings.randomChars( seed, 2 ).toLowerCase();
			String sum = UtilStrings.removeLetters( UtilEncryption.md5( seed ) );
			acctId += sum.length() < 3 ? UtilEncryption.randomize( "123" ) : sum.substring( 0, 3 );
			acctId += UtilStrings.randomChars( seed, 1 ).toUpperCase();
		}

		if ( acctId == null || acctId.isEmpty() )
			acctId = "ab123C";

		int tries = 1;
		do
		{
			Validate.notEmpty( acctId );
			Validate.validState( acctId.length() == 6 );
			Validate.validState( acctId.matches( "[a-z]{2}[0-9]{3}[A-Z]" ) );

			// When our tries are divisible by 25 we attempt to randomize the last letter for more chances.
			if ( tries % 25 == 0 )
				acctId = acctId.substring( 0, 4 ) + UtilEncryption.randomize( acctId.substring( 5 ) );

			acctId = acctId.substring( 0, 2 ) + UtilEncryption.randomize( "123" ) + acctId.substring( 5 );

			tries++;
		}
		while ( exists( acctId ) );

		return acctId;
	}

	public AccountMeta getAccount( String acctId )
	{
		AccountMeta acct = accounts.get( acctId );

		if ( acct == null )
		{
			acct = fireAccountLookup( acctId );

			if ( acct == null )
				return null;

			accounts.put( acct );
		}

		return acct;
	}

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

	public List<AccountMeta> getAccounts()
	{
		return Collections.unmodifiableList( getAccounts0() );
	}

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

	List<AccountMeta> getAccounts0()
	{
		return accounts.list();
	}

	public List<AccountMeta> getAccountsByLocation( AccountLocation collective )
	{
		Validate.notNull( collective );
		return accounts.stream().filter( m -> m.getLocation() == collective ).collect( Collectors.toList() );
	}

	public List<AccountMeta> getAccountsByLocation( String locationId )
	{
		LocationService service = AppManager.getService( AccountLocation.class );
		return getAccountsByLocation( service.getLocation( locationId ) );
	}

	public AccountMeta getAccountWithException( String acctId ) throws AccountException
	{
		AccountMeta acct = accounts.get( acctId );

		if ( acct == null )
		{
			acct = fireAccountLookupWithException( acctId );

			Validate.notNull( acct );
			accounts.put( acct );
		}

		return acct;
	}

	public Set<Account> getBanned()
	{
		Set<Account> accts = Sets.newHashSet();
		for ( AccountMeta meta : accounts )
			if ( meta.getEntity().isBanned() )
				accts.add( meta );
		return accts;
	}

	public Set<Account> getInitializedAccounts()
	{
		Set<Account> accts = Sets.newHashSet();
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
		Set<Account> accts = Sets.newHashSet();
		for ( AccountMeta meta : accounts )
			if ( meta.getEntity().isOp() )
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
		Set<AccountAttachment> accts = Sets.newHashSet();
		for ( AccountMeta meta : accounts )
			if ( meta.isInitialized() )
				accts.addAll( meta.instance().getAttachments() );
		return accts;
	}

	public Set<Account> getWhitelisted()
	{
		Set<Account> accts = Sets.newHashSet();
		for ( AccountMeta meta : accounts )
			if ( meta.getEntity().isWhitelisted() )
				accts.add( meta );
		return accts;
	}

	@Override
	public void init()
	{
		isDebug = AppConfig.get().getBoolean( "accounts.debug" );
		maxAccounts = AppConfig.get().getInt( "accounts.maxLogins", -1 );

		EventBus.instance().registerEvents( AccountType.MEMORY.getCreator(), this );
		EventBus.instance().registerEvents( AccountType.SQL.getCreator(), this );
		EventBus.instance().registerEvents( AccountType.FILE.getCreator(), this );
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

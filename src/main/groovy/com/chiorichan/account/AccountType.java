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
import com.chiorichan.account.types.AccountTypeCreator;
import com.chiorichan.account.types.FileTypeCreator;
import com.chiorichan.account.types.MemoryTypeCreator;
import com.chiorichan.account.types.SqlTypeCreator;
import com.chiorichan.messaging.MessageSender;
import com.chiorichan.permission.PermissibleEntity;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks AccountsTypes available on this server and their handler classes
 */
public final class AccountType
{
	private static final Map<String, AccountType> types = new ConcurrentHashMap<>();

	/**
	 * Loads Accounts from a SQL Table
	 */
	public static final AccountType SQL = new AccountType( "sql", SqlTypeCreator.INSTANCE, true );

	/**
	 * Loads Accounts from the File System
	 */
	public static final AccountType FILE = new AccountType( "file", FileTypeCreator.INSTANCE, true );

	/**
	 * Provides internally builtin accounts<br>
	 * Only exists as a support handler to builtin accounts
	 */
	public static final AccountType MEMORY = new AccountType( "memory", MemoryTypeCreator.INSTANCE, true );

	/**
	 * References the builtin no login Account with NO PERMISSIONS
	 */
	public static final AccountMeta ACCOUNT_NONE = new AccountMeta( new AccountContext( MemoryTypeCreator.INSTANCE, MEMORY, "%", "none", true ) );

	/**
	 * References the builtin root Account with ALL PERMISSIONS and then some!
	 */
	public static final AccountMeta ACCOUNT_ROOT = new AccountMeta( new AccountContext( MemoryTypeCreator.INSTANCE, MEMORY, "%", "root", true ) );

	static
	{
		AccountManager.instance().accounts.put( ACCOUNT_NONE );
		AccountManager.instance().accounts.put( ACCOUNT_ROOT );
	}

	public static Collection<AccountType> getAccountTypes()
	{
		return Collections.unmodifiableCollection( types.values() );
	}

	public static AccountType getDefaultType()
	{
		for ( AccountType type : getAccountTypes() )
			if ( type.isDefault() )
				return type;


		AccountManager.getLogger().warning( "No valid default AccountType was found, please check your configuration." );
		return MEMORY;
	}

	public static Set<AccountType> getEnabledAccountTypes()
	{
		Set<AccountType> typesAll = new HashSet<>( types.values() );
		for ( AccountType at : typesAll )
			if ( !at.isEnabled() )
				typesAll.remove( at );
		return Collections.unmodifiableSet( typesAll );
	}

	/**
	 * Tries to find an AccountType based on name alone<br>
	 * Handy for non-builtin types that register with the AccountPipeline
	 *
	 * @param name The name to find
	 * @return The matching AccountType, null if none exist
	 */
	public static AccountType getTypeByName( String name )
	{
		return types.get( name.toLowerCase() );
	}

	public static boolean isNoneAccount( Account account )
	{
		return account == null || account.getId().equalsIgnoreCase( "none" );
	}

	public static boolean isNoneAccount( MessageSender sender )
	{
		return sender == null || sender.getId().equalsIgnoreCase( "none" );
	}

	public static boolean isNoneAccount( PermissibleEntity entity )
	{
		return entity == null || entity.getId().equalsIgnoreCase( "none" );
	}

	public static boolean isRootAccount( Account account )
	{
		return account.getId().equalsIgnoreCase( "root" );
	}

	public static boolean isRootAccount( MessageSender sender )
	{
		return sender.getId().equalsIgnoreCase( "root" );
	}

	public static boolean isRootAccount( PermissibleEntity entity )
	{
		return entity.getId().equalsIgnoreCase( "root" );
	}

	private final boolean builtin;

	private final String name;

	private final AccountTypeCreator creator;

	/**
	 * Registers a new non-builtin AccountType
	 *
	 * @param name The AccountType name
	 */
	public AccountType( String name, AccountTypeCreator creator )
	{
		this( name, creator, false );
	}

	/**
	 * Internal Use Only
	 */
	private AccountType( String name, AccountTypeCreator creator, boolean builtin )
	{
		Validate.notNull( name );
		Validate.notNull( creator );

		if ( !name.toLowerCase().equals( name ) )
			throw new IllegalStateException( "AccountType names are expected to be in lowercase and singular" );

		if ( types.containsKey( name ) )
			throw new IllegalStateException( "AccountType `" + name + "` is already registered with this server" );

		types.put( name, this );

		this.name = name;
		this.creator = creator;
		this.builtin = builtin;
	}

	public AccountTypeCreator getCreator()
	{
		return creator;
	}

	/**
	 * Gets the name of this AccountType
	 *
	 * @return The AccountType name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Is this a builtin AccountType, i.e., SQL, FILE, or MEMORY
	 *
	 * @return Is it builtin?
	 */
	public boolean isBuiltin()
	{
		return builtin;
	}

	/**
	 * Checks if this type is default per server configuration
	 *
	 * @return True if it is default
	 */
	public boolean isDefault()
	{
		// Memory accounts are never default
		if ( this == AccountType.MEMORY )
			return false;

		boolean def = AppConfig.get().getBoolean( "accounts.defaultType", false );

		if ( def && !isEnabled() )
			throw new IllegalStateException( "Your default Account Type is '" + getName() + "' and it's not enabled, possibly due to a startup failure." );

		return def;
	}

	/**
	 * Checks if this type was enabled in the server configuration
	 *
	 * @return True if it is enabled
	 */
	public boolean isEnabled()
	{
		/* Memory accounts are always enabled */
		if ( this == AccountType.MEMORY )
			return true;

		/* Lastly we ask the AccountCreator directly if it's enabled. */
		return getCreator().isEnabled();
	}
}

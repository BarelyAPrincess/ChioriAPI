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

import com.chiorichan.account.lang.AccountException;
import com.chiorichan.zutils.ZObjects;
import com.chiorichan.lang.UncaughtException;
import com.chiorichan.permission.PermissibleEntity;
import com.chiorichan.permission.PermissionManager;
import com.chiorichan.services.AppManager;
import com.chiorichan.zutils.ZEncryption;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.Validate;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class AccountMeta implements Account, Iterable<Entry<String, Object>>
{
	public static final List<String> IGNORED_KEYS = Arrays.asList( "locId", "acctId" );

	/**
	 * Used to store our Account Metadata besides the required builtin key names.
	 */
	private final Map<String, Object> metadata = Maps.newTreeMap( String.CASE_INSENSITIVE_ORDER );

	/**
	 * Provides context into our existence
	 */
	private final AccountContext context;

	/**
	 * Used as our reference to the Account Instance.<br>
	 * We use a WeakReference so the account can be logged out automatically when no longer used.
	 */
	private WeakReference<AccountInstance> account = null;

	/**
	 * Site Id
	 */
	private final String locId;

	/**
	 * Account Id
	 */
	private final String acctId;

	/**
	 * Used to keep the Account Instance loaded in the memory when {@link #keepInMemory} is set to true<br>
	 * This counters our weak reference for variable {@link #account}
	 */
	@SuppressWarnings( "unused" )
	private AccountInstance strongReference = null;

	/**
	 * Weak references the {@link PermissibleEntity} over at the Permission Manager.<br>
	 * Again, we use the {@link WeakReference} so it can be garbage collected when unused,<br>
	 * we reload it from the Permission Manager once needed again.
	 */
	private WeakReference<PermissibleEntity> permissibleEntity = null;

	/**
	 * Indicates if we should keep the Account Instance loaded in Memory
	 */
	private boolean keepInMemory = false;

	AccountMeta( AccountContext context )
	{
		Validate.notNull( context );

		context.setAccount( this );

		this.context = context;
		acctId = context.getAcctId();
		locId = context.getLocationId();

		metadata.putAll( context.getValues() );

		/**
		 * Populate the PermissibleEntity for reasons... and notify the Account Creator
		 */
		context.creator().successInit( this, getEntity() );
	}

	public boolean containsKey( String key )
	{
		return metadata.containsKey( key );
	}

	/**
	 * Returns the {@link AccountContext} responsible for our existence
	 *
	 * @return
	 *         Instance of AccountContext
	 */
	public AccountContext context()
	{
		return context;
	}

	public Boolean getBoolean( String key )
	{
		try
		{
			return ZObjects.castToBoolWithException( metadata.get( key ) );
		}
		catch ( ClassCastException e )
		{
			return false;
		}
	}

	@Override
	public String getDisplayName()
	{
		String name = context.creator().getDisplayName( this );
		return name == null ? getId() : name;
	}

	@Override
	public PermissibleEntity getEntity()
	{
		if ( permissibleEntity == null || permissibleEntity.get() == null )
		{
			PermissionManager mgr = AppManager.getService( PermissibleEntity.class );
			if ( mgr == null )
				throw new UncaughtException( "PermissibleEntity provider is not available, check load order!" );
			permissibleEntity = new WeakReference<PermissibleEntity>( mgr.getEntity( getId() ) );
		}

		return permissibleEntity.get();
	}

	@Override
	public String getId()
	{
		return acctId;
	}

	public Integer getInteger( String key )
	{
		return getInteger( key, 0 );
	}

	public Integer getInteger( String key, int def )
	{
		Object obj = metadata.get( key );
		Integer val = ZObjects.castToInt( obj );

		return val == null ? def : val;
	}

	public Set<String> getKeys()
	{
		return metadata.keySet();
	}

	/**
	 * Get the associated AccountLocation
	 *
	 * @return
	 *         The {@link AccountLocation} for the associated locationId, will return null if no service is registered
	 */
	@Override
	public AccountLocation getLocation()
	{
		LocationService service = AppManager.getService( AccountLocation.class );
		if ( service == null )
			return null;
		return service.getLocation( locId );
	}

	public String getLogoffMessage()
	{
		return getId() + " has logged off the server";
	}

	public Map<String, Object> getMeta()
	{
		return Collections.unmodifiableMap( metadata );
	}

	public Object getObject( String key )
	{
		return metadata.get( key );
	}

	public String getString( String key )
	{
		return getString( key, null );
	}

	public String getString( String key, String def )
	{
		String val = ZObjects.castToString( metadata.get( key ) );
		return val == null ? def : val;
	}

	private AccountInstance initAccount()
	{
		AccountInstance account = new AccountInstance( this );
		this.account = new WeakReference<AccountInstance>( account );

		if ( keepInMemory )
			strongReference = account;

		AccountManager.instance().fireAccountLoad( this );

		return account;
	}

	@Override
	public AccountInstance instance()
	{
		if ( !isInitialized() )
			initAccount();

		return account.get();
	}

	@Override
	public boolean isInitialized()
	{
		return account != null && account.get() != null;
	}

	@Override
	public Iterator<Entry<String, Object>> iterator()
	{
		return Collections.unmodifiableMap( metadata ).entrySet().iterator();
	}

	/**
	 * Returns if the Account is will be kept in memory
	 * If you want to know if the Account is currently being kept in memory, See {@link #keptInMemory()}
	 *
	 * @return
	 *         Will be kept in memory?
	 */
	public boolean keepInMemory()
	{
		return isInitialized() && keepInMemory;
	}

	/**
	 * Sets if the Account should stay loaded in the VM memory
	 *
	 * @param state
	 *             Stay in memory?
	 */
	public void keepInMemory( boolean state )
	{
		strongReference = state ? account.get() : null;
		keepInMemory = state;
	}

	/**
	 * Returns if the Account is being kept in memory
	 * If you want to know if the Account will be kept in memory, See {@link #keepInMemory()}
	 *
	 * @return
	 *         Is being kept in memory? Will always return false if the Account is not initialized.
	 */
	public boolean keptInMemory()
	{
		return isInitialized() && keepInMemory;
	}

	public Set<String> keySet()
	{
		return Collections.unmodifiableSet( metadata.keySet() );
	}

	@Override
	public AccountMeta meta()
	{
		return this;
	}

	public void reload() throws AccountException
	{
		context.creator().reload( this );
	}

	public void requireActivation()
	{
		metadata.put( "actnum", ZEncryption.randomize( "z154f98wfjascvc" ) );
	}

	public void save() throws AccountException
	{
		context.creator().save( this );
	}

	public void set( String key, Object obj )
	{
		Validate.notNull( key );

		if ( obj == null )
			metadata.remove( key );
		else
			metadata.put( key, obj );
	}

	@Override
	public String toString()
	{
		return "AccountMeta{acctId=" + acctId + ",locId=" + locId + "," + Joiner.on( "," ).withKeyValueSeparator( "=" ).join( metadata ) + "}";
	}
}

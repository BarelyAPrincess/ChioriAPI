/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.account.types;

import com.chiorichan.AppConfig;
import com.chiorichan.Versioning;
import com.chiorichan.account.AccountContext;
import com.chiorichan.account.AccountLocation;
import com.chiorichan.account.AccountMeta;
import com.chiorichan.account.AccountPermissible;
import com.chiorichan.account.AccountType;
import com.chiorichan.account.LocationService;
import com.chiorichan.account.lang.AccountDescriptiveReason;
import com.chiorichan.account.lang.AccountException;
import com.chiorichan.account.lang.AccountResolveResult;
import com.chiorichan.account.lang.AccountResult;
import com.chiorichan.datastore.sql.SQLTable;
import com.chiorichan.datastore.sql.SQLTableColumns;
import com.chiorichan.datastore.sql.query.SQLQuerySelect;
import com.chiorichan.datastore.sql.skel.SQLWhereGroup;
import com.chiorichan.lang.ReportingLevel;
import com.chiorichan.permission.PermissibleEntity;
import com.chiorichan.permission.Permission;
import com.chiorichan.permission.PermissionDefault;
import com.chiorichan.services.AppManager;
import com.chiorichan.tasks.Timings;
import com.chiorichan.utils.UtilDB;
import com.chiorichan.utils.UtilObjects;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Handles Accounts that are loaded from SQL
 */
public class SqlTypeCreator extends AccountTypeCreator
{
	public static final SqlTypeCreator INSTANCE = new SqlTypeCreator();

	public SqlTypeCreator()
	{

	}

	@Override
	public String getDisplayName( AccountMeta meta )
	{
		if ( meta.getString( "fname" ) != null && !meta.getString( "fname" ).isEmpty() && meta.getString( "name" ) != null && !meta.getString( "name" ).isEmpty() )
			return meta.getString( "fname" ) + " " + meta.getString( "name" );

		if ( meta.getString( "name" ) != null && !meta.getString( "name" ).isEmpty() )
			return meta.getString( "name" );

		if ( meta.getString( "email" ) != null && !meta.getString( "email" ).isEmpty() )
			return meta.getString( "email" );

		return null;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	@Override
	public AccountResolveResult resolveAccount( String locId, String acctId )
	{
		try
		{
			return new AccountResolveResult( readAccount( locId, acctId ), AccountDescriptiveReason.LOGIN_SUCCESS );
		}
		catch ( SQLException e )
		{
			if ( Versioning.isDevelopment() )
				e.printStackTrace();

			return new AccountResolveResult( null, AccountDescriptiveReason.INTERNAL_ERROR ).setCause( e );
		}
		catch ( AccountException e )
		{
			return new AccountResolveResult( null, e.getReason() );
		}
	}

	@Override
	public void preLogin( AccountMeta meta, AccountPermissible via, String acctId, Object... credentials ) throws AccountException
	{
		if ( meta.getInteger( "numLoginFail" ) > 5 )
			if ( meta.getInteger( "numLoginFail" ) > Timings.epoch() - 1800 )
				throw new AccountException( AccountDescriptiveReason.UNDER_ATTACK, meta );

		if ( !meta.getString( "actkey" ).equals( "" ) )
			throw new AccountException( AccountDescriptiveReason.ACCOUNT_NOT_ACTIVATED, meta );
	}

	public AccountLocation resolveLocId( String locId )
	{
		LocationService service = AppManager.getService( AccountLocation.class );
		return UtilObjects.isEmpty( locId ) ? null : service == null ? null : service.getLocation( locId );
	}

	public AccountContext readAccount( String locId, String acctId ) throws AccountException, SQLException
	{
		if ( UtilObjects.isEmpty( acctId ) )
			throw new AccountException( AccountDescriptiveReason.EMPTY_ID, locId, acctId );

		Set<String> loginKeys = new HashSet<>( getLoginKeys() );
		SQLTable table = null;

		loginKeys.add( "acctId" );
		loginKeys.add( "username" );

		AccountLocation location = resolveLocId( locId );

		if ( location != null )
		{
			Set<String> additionalLoginKeys = location.getAccountFields();
			if ( additionalLoginKeys != null )
				loginKeys.addAll( additionalLoginKeys );

			table = location.getAccountTable();
		}
		if ( table == null )
			table = AppConfig.get().getDatabase().table( AppConfig.get().getString( "accounts.sqlTable", "accounts" ) );

		Set<String> accountColumnSet = new HashSet<>( table.columnNames() );

		SQLQuerySelect select = table.select().group().whereMatches( "locId", locId ).or().whereMatches( "locId", "%" ).parent();
		SQLWhereGroup group = select.and().group();

		for ( String loginKey : loginKeys )
			if ( !loginKey.isEmpty() )
				if ( accountColumnSet.contains( loginKey ) )
					group.or().where( loginKey ).matches( acctId );
				else
					for ( String c : accountColumnSet )
						if ( c.equalsIgnoreCase( loginKey ) )
						{
							group.or().where( c ).matches( acctId );
							break;
						}

		select.execute();

		if ( select.rowCount() < 1 )
			throw new AccountException( AccountDescriptiveReason.INCORRECT_LOGIN, locId, acctId );

		AccountContextImpl context = new AccountContextImpl( this, AccountType.SQL );
		Map<String, String> row = select.stringRow();

		context.setAcctId( row.get( "acctId" ) );
		context.setLocationId( row.get( "locId" ) );
		context.setValues( select.row() );

		return context;
	}

	@Override
	public boolean accountExists( String locId, String acctId )
	{
		try
		{
			return readAccount( locId, acctId ) != null;
		}
		catch ( AccountException | SQLException e )
		{
			return false;
		}
	}

	@Override
	public void reload( AccountMeta meta ) throws AccountException
	{
		try
		{
			readAccount( meta.getLocId(), meta.getId() );
		}
		catch ( SQLException e )
		{
			e.printStackTrace();
		}
	}

	public SQLTableColumns checkTable( SQLTable table ) throws SQLException
	{
		SQLTableColumns columns = table.columns();

		table.addColumnVar( "acctId", 255 );
		table.addColumnVar( "locId", 255 );
		table.addColumnVar( "email", 255 );
		table.addColumnVar( "username", 255 );
		table.addColumnVar( "name", 255 );
		table.addColumnVar( "fname", 255 );
		table.addColumnVar( "actkey", 255 );
		table.addColumnVar( "lastLoginIp", 255 );
		table.addColumnInt( "numLoginIp", 20 );
		table.addColumnInt( "numLoginFail", 20 );
		table.addColumnInt( "lastLoginFail", 20 );
		table.addColumnInt( "lastActive", 20 );
		table.addColumnInt( "lastLogin", 20 );

		return columns.refresh();
	}

	@Override
	public void save( AccountContext context ) throws AccountException
	{
		UtilObjects.notNull( context );

		try
		{
			Map<String, Object> metaData = new HashMap<>( context.meta() == null ? context.getValues() : context.meta().getMeta() );

			metaData.put( "acctId", context.getAcctId() );
			metaData.put( "locId", context.getLocId() );

			SQLTable table = null;
			AccountLocation location = resolveLocId( context.getLocId() );

			if ( location != null )
				table = location.getAccountTable();
			if ( table == null )
				table = AppConfig.get().getDatabase().table( AppConfig.get().getString( "accounts.sqlTable", "accounts" ) );

			SQLTableColumns columns = checkTable( table );

			for ( Entry<String, Object> e : metaData.entrySet() )
			{
				String key = e.getKey();

				String type = UtilDB.objectToSqlType( e.getValue() );
				if ( !columns.contains( key ) )
					try
					{
						table.addColumn( type, key );
					}
					catch ( SQLException se )
					{
						throw new AccountException( new AccountDescriptiveReason( "Failed to create SQL column '" + key + "' with type '" + type + "' in the 'accounts' table", ReportingLevel.E_ERROR ), se, context.meta() );
					}
			}

			columns.refresh();

			for ( SQLTableColumns.SQLColumn col : columns.columnsRequired() )
				if ( !metaData.containsKey( col.name() ) )
					metaData.put( col.name(), UtilDB.sqlTypeToObject( col.type() ) );

			SQLQuerySelect select = table.select().whereMatches( "locId", context.getLocId() ).whereMatches( "acctId", context.getAcctId() ).limit( 1 ).execute();

			if ( select.rowCount() > 0 )
				table.update().values( metaData ).whereMatches( "locId", context.getLocId() ).whereMatches( "acctId", context.getAcctId() ).limit( 1 ).execute();
			else
				table.insert().values( metaData ).execute();
		}
		catch ( SQLException e )
		{
			throw new AccountException( e, context.meta() );
		}
		catch ( Throwable t )
		{
			t.printStackTrace();
		}
	}

	@Override
	public void successInit( AccountMeta meta, PermissibleEntity entity )
	{
		Permission userNode = PermissionDefault.USER.getNode();
		if ( meta.getContext().creator() == this && !entity.checkPermission( userNode ).isAssigned() )
			entity.addPermission( userNode, true, null );
	}

	@Override
	public void failedLogin( AccountMeta meta, AccountResult result )
	{
		UtilObjects.notNull( meta );

		try
		{
			SQLTable table = null;
			AccountLocation location = resolveLocId( meta.getLocId() );

			if ( location != null )
				table = location.getAccountTable();
			if ( table == null )
				table = AppConfig.get().getDatabase().table( AppConfig.get().getString( "accounts.sqlTable", "accounts" ) );

			meta.set( "lastLoginFail", Timings.epoch() );
			meta.set( "numLoginFail", meta.getInteger( "numLoginFail" ) + 1 );

			table.update().whereMatches( "locId", meta.getLocId() ).whereMatches( "acctId", meta.getId() ).value( "lastLoginFail", meta.getInteger( "lastLoginFail" ) ).value( "numLoginFail", meta.getInteger( "numLoginFail" ) ).execute();
		}
		catch ( SQLException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void successLogin( AccountMeta meta ) throws AccountException
	{
		UtilObjects.notNull( meta );

		try
		{
			SQLTable table = null;
			AccountLocation location = resolveLocId( meta.getLocId() );

			if ( location != null )
				table = location.getAccountTable();
			if ( table == null )
				table = AppConfig.get().getDatabase().table( AppConfig.get().getString( "accounts.sqlTable", "accounts" ) );

			meta.set( "lastActive", Timings.epoch() );
			meta.set( "lastLogin", Timings.epoch() );
			meta.set( "lastLoginFail", 0 );
			meta.set( "numLoginFail", 0 );

			table.update().whereMatches( "locId", meta.getLocId() ).whereMatches( "acctId", meta.getId() ).value( "lastActive", meta.getInteger( "lastActive" ) ).value( "lastLogin", meta.getInteger( "lastLogin" ) ).value( "lastLoginFail", 0 ).value( "numLoginFail", 0 ).execute();
		}
		catch ( SQLException e )
		{
			e.printStackTrace();
		}
	}
}

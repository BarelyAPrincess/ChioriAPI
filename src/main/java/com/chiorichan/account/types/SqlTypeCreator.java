/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package com.chiorichan.account.types;

import com.chiorichan.AppConfig;
import com.chiorichan.Versioning;
import com.chiorichan.account.AccountContext;
import com.chiorichan.account.AccountManager;
import com.chiorichan.account.AccountMeta;
import com.chiorichan.account.AccountPermissible;
import com.chiorichan.account.AccountType;
import com.chiorichan.account.event.AccountLookupEvent;
import com.chiorichan.account.lang.AccountDescriptiveReason;
import com.chiorichan.account.lang.AccountException;
import com.chiorichan.account.lang.AccountResult;
import com.chiorichan.datastore.sql.SQLTable;
import com.chiorichan.datastore.sql.SQLTableColumns;
import com.chiorichan.datastore.sql.bases.SQLDatastore;
import com.chiorichan.datastore.sql.query.SQLQuerySelect;
import com.chiorichan.event.EventHandler;
import com.chiorichan.lang.ReportingLevel;
import com.chiorichan.permission.PermissibleEntity;
import com.chiorichan.permission.Permission;
import com.chiorichan.permission.PermissionDefault;
import com.chiorichan.tasks.Timings;
import com.chiorichan.zutils.ZDB;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Handles Accounts that are loaded from SQL
 */
public class SqlTypeCreator extends AccountTypeCreator
{
	public static final SqlTypeCreator INSTANCE = new SqlTypeCreator();

	final SQLDatastore sql;
	final String table;
	final List<String> accountFields;
	private boolean enabled = true;

	public SqlTypeCreator()
	{
		sql = AppConfig.get().getDatabase();

		if ( sql == null )
		{
			AccountManager.getLogger().warning( "We could not enable the `SqlLoginHandler` due to an unconfigured SQL Database, which is required." );
			enabled = false;
		}
		else
			AccountManager.getLogger().info( "The `SqlLoginHandler` was enabled successfully with database '" + AppConfig.get().getDatabase() + "'" );

		// TODO Check if the database has the right tables.
		// TODO Add the ability to select which database to use for logins

		table = AppConfig.get().getString( "accounts.sqlType.table", "accounts" );
		accountFields = AppConfig.get().getStringList( "accounts.sqlType.fields", new ArrayList<String>() );
	}


	@Override
	public AccountContext createAccount( String acctId, String siteId ) throws AccountException
	{
		AccountContext context = new AccountContextImpl( this, AccountType.SQL, acctId, siteId );

		context.setValue( "date", Timings.epoch() );
		context.setValue( "numloginfailed", 0 );
		context.setValue( "lastloginfail", 0 );
		context.setValue( "actnum", "0" );

		save( context );
		return context;
	}

	@Override
	public boolean exists( String acctId )
	{
		try
		{
			return sql.table( table ).select().where( "acctId" ).matches( acctId ).execute().rowCount() > 0;
		}
		catch ( SQLException e )
		{
			return false;
		}
	}

	@Override
	public void failedLogin( AccountMeta meta, AccountResult result )
	{
		try
		{
			sql.table( table ).update().value( "lastActive", Timings.epoch() ).value( "lastLoginFail", 0 ).value( "numLoginFail", 0 ).where( "acctID" ).matches( meta.getId() ).execute();
			// sql.queryUpdate( "UPDATE `" + table + "` SET `lastActive` = '" + Timings.epoch() + "', `lastLoginFail` = 0, `numLoginFail` = 0 WHERE `acctID` = '" + meta.getId() + "'" );
		}
		catch ( SQLException e )
		{
			e.printStackTrace();
		}
		meta.set( "lastActive", Timings.epoch() );
		meta.set( "lastLoginFail", 0 );
		meta.set( "numLoginFail", 0 );
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
	public List<String> getLoginKeys()
	{
		return accountFields;
	}

	/*
	 * public ResultSet getResultSet( String uid ) throws SQLException
	 * {
	 * if ( uid == null || uid.isEmpty() )
	 * return null;
	 *
	 * SQLExecute execute = sql.table( "accounts" ).select().where( "acctId" ).equals( uid ).limit( 1 ).execute();
	 *
	 * if ( execute.rowCount() < 1 )
	 * return null;
	 *
	 * return execute.resultSet();
	 * }
	 */

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@EventHandler
	public void onAccountLookupEvent( AccountLookupEvent event )
	{
		try
		{
			event.setResult( readAccount( event.getAcctId() ), AccountDescriptiveReason.LOGIN_SUCCESS );
		}
		catch ( SQLException e )
		{
			if ( Versioning.isDevelopment() )
				e.printStackTrace();

			event.setResult( null, AccountDescriptiveReason.INTERNAL_ERROR ).setCause( e );
		}
		catch ( AccountException e )
		{
			event.setResult( null, e.getReason() );
		}
	}

	@Override
	public void preLogin( AccountMeta meta, AccountPermissible via, String acctId, Object... credentials ) throws AccountException
	{
		if ( meta.getInteger( "numloginfail" ) > 5 )
			if ( meta.getInteger( "lastloginfail" ) > Timings.epoch() - 1800 )
				throw new AccountException( AccountDescriptiveReason.UNDER_ATTACK, meta );

		if ( !meta.getString( "actnum" ).equals( "0" ) )
			throw new AccountException( AccountDescriptiveReason.ACCOUNT_NOT_ACTIVATED, meta );
	}

	public AccountContext readAccount( String acctId ) throws AccountException, SQLException
	{
		if ( acctId == null || acctId.isEmpty() )
			throw new AccountException( AccountDescriptiveReason.EMPTY_ID, acctId );

		Set<String> accountFieldSet = new HashSet<String>( accountFields );
		Set<String> accountColumnSet = new HashSet<String>( sql.table( table ).columnNames() );

		accountFieldSet.add( "acctId" );
		accountFieldSet.add( "username" );

		SQLQuerySelect select = sql.table( table ).select();

		// String additionalAccountFields = "";
		for ( String f : accountFieldSet )
			if ( !f.isEmpty() )
				if ( accountColumnSet.contains( f ) )
					select.or().where( f ).matches( acctId );
				else
					for ( String c : accountColumnSet )
						if ( c.equalsIgnoreCase( f ) )
						{
							select.or().where( c ).matches( acctId );
							break;
						}

		select.execute();

		// ResultSet rs = sql.query( "SELECT * FROM `" + table + "` WHERE " + additionalAccountFields.substring( 4 ) + ";" );

		AccountContextImpl context = new AccountContextImpl( this, AccountType.SQL, acctId, "%" );

		if ( select.rowCount() < 1 )
			throw new AccountException( AccountDescriptiveReason.INCORRECT_LOGIN, acctId );

		Map<String, String> row = select.stringRow();

		context.setAcctId( row.get( "acctId" ) );
		context.setLocationId( row.get( "siteId" ) );
		context.setValues( select.row() );

		return context;
	}

	@Override
	public void reload( AccountMeta meta ) throws AccountException
	{
		try
		{
			readAccount( meta.getId() );
		}
		catch ( SQLException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void save( AccountContext context ) throws AccountException
	{
		try
		{
			Map<String, Object> metaData = new HashMap<String, Object>( context.meta() == null ? context.getValues() : context.meta().getMeta() );

			metaData.put( "acctId", context.getAcctId() );
			metaData.put( "locId", context.getLocationId() );

			SQLTable table = sql.table( this.table );
			SQLTableColumns columns = table.columns();

			if ( !columns.contains( "acctId" ) )
				table.addColumnVar( "acctId", 255 );
			if ( !columns.contains( "siteId" ) )
				table.addColumnVar( "siteId", 255 );

			columns.refresh();

			for ( Entry<String, Object> e : metaData.entrySet() )
			{
				String key = e.getKey();

				String type = ZDB.objectToSqlType( e.getValue() );
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
					metaData.put( col.name(), ZDB.sqlTypeToObject( col.type() ) );


			SQLQuerySelect select = table.select().where( "acctId" ).matches( context.getAcctId() ).limit( 1 ).execute();

			if ( select.rowCount() > 0 )
				table.update().values( metaData ).where( "acctId" ).matches( context.getAcctId() ).limit( 1 ).execute();
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
		if ( meta.context().creator() == this && !entity.checkPermission( userNode ).isAssigned() )
			entity.addPermission( userNode, true, null );
	}

	@Override
	public void successLogin( AccountMeta meta ) throws AccountException
	{
		try
		{
			sql.table( "accounts" ).update().value( "lastActive", Timings.epoch() ).value( "lastLogin", Timings.epoch() ).value( "lastLoginFail", 0 ).value( "numLoginFail", 0 ).where( "acctId" ).matches( meta.getId() ).execute();
		}
		catch ( SQLException e )
		{
			throw new AccountException( e, meta );
		}
	}

	/*
	 * @Override
	 * public List<AccountMeta> getAccounts()
	 * {
	 * List<AccountMeta> metas = Lists.newArrayList();
	 *
	 * try
	 * {
	 * ResultSet rs = sql.query( "SELECT * FROM `" + table + "`;" );
	 *
	 * if ( rs == null || sql.getRowCount( rs ) < 1 )
	 * return Lists.newArrayList();
	 *
	 * do
	 * {
	 * AccountMeta meta = new AccountMeta();
	 * meta.setAll( DatabaseEngine.convertRow( rs ) );
	 * meta.set( "displayName", ( rs.getString( "fname" ).isEmpty() ) ? rs.getString( "name" ) : rs.getString( "fname" ) + " " + rs.getString( "name" ) );
	 * metas.add( meta );
	 * }
	 * while ( rs.next() );
	 * }
	 * catch ( SQLException e )
	 * {
	 * return metas;
	 * }
	 *
	 * return metas;
	 * }
	 */
}

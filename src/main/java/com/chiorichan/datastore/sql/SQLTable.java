/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.datastore.sql;

import com.chiorichan.datastore.sql.bases.SQLiteDatastore;
import com.chiorichan.datastore.sql.query.SQLQueryDelete;
import com.chiorichan.datastore.sql.query.SQLQueryInsert;
import com.chiorichan.datastore.sql.query.SQLQuerySelect;
import com.chiorichan.datastore.sql.query.SQLQueryUpdate;
import com.chiorichan.zutils.ZObjects;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Interfaces with MySQL Table
 */
public class SQLTable extends SQLBase<SQLTable>
{
	private final String table;
	private final DatabaseMetaData meta;

	public SQLTable( SQLWrapper sql, String table ) throws SQLException
	{
		super( sql, false );
		this.table = table;
		meta = sql.getMetaData();
	}

	public SQLTable addColumn( String colType, String colName ) throws SQLException
	{
		return addColumn( colType, colName, null );
	}

	/**
	 * This method is tailored for use with MySQL and returns a result for the primary key.
	 * This method needs patching for other sql types and the ability to return more than one primary key
	 *
	 * @return The tables primary key
	 * @throws SQLException
	 */
	public String primaryKey() throws SQLException
	{
		ResultSet rs = query( String.format( "SHOW INDEX FROM `%s`", table ), false ).getResultSet();
		if ( !rs.first() )
			return null;
		return rs.getString( "Column_name" );
	}

	public SQLTable addColumn( String colType, String colName, Object def ) throws SQLException
	{
		SQLTableColumns columns = columns();

		if ( columns.contains( colName ) )
			throw new SQLException( "There already exists a column by the name of '" + colName + "'" );

		String defString = def == null ? "NULL" : "NOT NULL DEFAULT '" + ZObjects.castToString( def ) + "'";

		if ( exists() )
			query( String.format( "ALTER TABLE `%s` ADD `%s` %s %s;", table, colName, colType, defString ), true );
		else
			query( String.format( "CREATE TABLE `%s` ( `%s` %s %s );", table, colName, colType, defString ), true );

		return this;
	}

	public SQLTable addColumnInt( String colName, int i ) throws SQLException
	{
		return addColumn( "INTEGER(" + i + ")", colName );
	}

	public SQLTable addColumnInt( String colName, int i, int def ) throws SQLException
	{
		return addColumn( "INTEGER(" + i + ")", colName, def );
	}

	public SQLTable addColumnText( String colName ) throws SQLException
	{
		return addColumn( "TEXT", colName );
	}

	public SQLTable addColumnText( String colName, String def ) throws SQLException
	{
		return addColumnText( colName, def );
	}

	public SQLTable addColumnVar( String colName, int i ) throws SQLException
	{
		return addColumnVar( colName, i, null );
	}

	public SQLTable addColumnVar( String colName, int i, String def ) throws SQLException
	{
		if ( i > 256 )
			throw new SQLException( "VARCHAR does not support more than 256 bytes" );
		if ( def != null && def.length() > i )
			throw new SQLException( "Default is more than max size" );

		if ( sql.datastore() instanceof SQLiteDatastore )
			return addColumn( "TEXT", colName, def );
		else
			return addColumn( "VARCHAR(" + i + ")", colName, def );
	}

	public List<String> columnNames() throws SQLException
	{
		List<String> rtn = new LinkedList<>();

		query( "SELECT * FROM `" + table + "` LIMIT 1;", false );

		ResultSetMetaData metaData = resultSet().getMetaData();

		for ( int i = 1; i < metaData.getColumnCount() + 1; i++ )
			rtn.add( metaData.getColumnName( i ) );

		return rtn;
	}

	public SQLTableColumns columns() throws SQLException
	{
		return new SQLTableColumns( sql, table );
	}

	public SQLQueryDelete delete()
	{
		return new SQLQueryDelete( sql, table );
	}

	public SQLTable drop() throws SQLException
	{
		query( String.format( "DROP TABLE `%s` IF EXISTS;", table ), true );
		return this;
	}

	public SQLTable dropColumn( String colName ) throws SQLException
	{
		query( String.format( "ALTER TABLE `%s` DROP `%s`;", table, colName ), true );
		return this;
	}

	@Override
	protected SQLTable execute0() throws SQLException
	{
		return this;
	}

	public boolean exists()
	{
		return exists( false );
	}

	private boolean exists( boolean retry )
	{
		try
		{
			ResultSet rs = meta.getTables( null, null, null, null );

			while ( rs.next() )
				if ( rs.getString( 3 ).equalsIgnoreCase( table ) )
				{
					setPass();
					return true;
				}
		}
		catch ( CommunicationsException e )
		{
			if ( !retry )
				return exists( true );
			setFail( e );
		}
		catch ( SQLException e )
		{
			setFail( e );
		}
		return false;
	}

	public SQLQueryInsert insert()
	{
		return new SQLQueryInsert( sql, table );
	}

	public ResultSetMetaData metaData( String table ) throws SQLException
	{
		ResultSet rs = new SQLRawQuery( sql, "SELECT * FROM " + table ).resultSet();
		return rs.getMetaData();
	}

	@Override
	public int rowCount() throws SQLException
	{
		return new SQLQuerySelect( sql, table ).rowCount();
	}

	public SQLQuerySelect select()
	{
		return new SQLQuerySelect( sql, table );
	}

	public SQLQuerySelect select( Collection<String> fields )
	{
		return select().fields( fields );
	}

	@Override
	public Object[] sqlValues()
	{
		return new Object[0];
	}

	@Override
	public SQLTable clone()
	{
		try
		{
			SQLTable clone = new SQLTable( sql, table );
			super.clone( clone );
			return clone;
		}
		catch ( SQLException e )
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toSqlQuery()
	{
		return null;
	}

	public SQLQueryUpdate update()
	{
		return new SQLQueryUpdate( sql, table );
	}
}

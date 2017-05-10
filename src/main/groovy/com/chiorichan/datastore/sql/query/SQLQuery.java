/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.datastore.sql.query;

import com.chiorichan.datastore.sql.SQLBase;
import com.chiorichan.datastore.sql.SQLWrapper;
import com.chiorichan.utils.UtilDB;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * SQL Query Constructor
 */
public final class SQLQuery extends SQLBase<SQLQuery>
{
	private String query;
	private boolean update;
	private List<Object> values;

	public SQLQuery( SQLWrapper sql )
	{
		super( sql, true );
	}

	public SQLQuery( SQLWrapper sql, boolean autoExecute )
	{
		super( sql, autoExecute );
	}

	public SQLQuery( SQLWrapper sql, String query, boolean autoExecute, Object... values )
	{
		super( sql, autoExecute );
		this.query = query;
		this.values = Arrays.asList( values );
		updateExecution();
	}

	public SQLQuery( SQLWrapper sql, String query, Object... values )
	{
		super( sql, true );
		this.query = query;
		this.values = Arrays.asList( values );
		updateExecution();
	}

	@Override
	protected SQLQuery execute0() throws SQLException
	{
		query( query, update, sqlValues() );

		return this;
	}

	@SuppressWarnings( "deprecation" )
	@Override
	public int rowCount()
	{
		// This might be the worst way to do this!

		try
		{
			if ( query.toLowerCase().startsWith( "select" ) )
				return UtilDB.rowCount( result() );
			else
				return statement().getUpdateCount();
		}
		catch ( NullPointerException | SQLException e )
		{
			return -1;
		}
	}

	public SQLQuery sqlQuery( String query, Object... values )
	{
		this.query = query;
		this.values = Arrays.asList( values );
		update = false;
		updateExecution();
		return this;
	}

	public SQLQuery sqlQueryUpdate( String query, Object... values )
	{
		this.query = query;
		this.values = Arrays.asList( values );
		update = true;
		updateExecution();
		return this;
	}

	@Override
	public Object[] sqlValues()
	{
		return values.toArray();
	}

	@Override
	public SQLQuery clone()
	{
		SQLQuery clone = new SQLQuery( sql );

		super.clone( clone );

		clone.query = this.query;
		clone.update = this.update;
		clone.values = new ArrayList<>( this.values );

		return clone;
	}

	@Override
	public String toSqlQuery()
	{
		return query;
	}
}

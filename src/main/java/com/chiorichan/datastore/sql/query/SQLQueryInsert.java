/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.datastore.sql.query;

import com.chiorichan.datastore.DatastoreManager;
import com.chiorichan.datastore.sql.SQLBase;
import com.chiorichan.datastore.sql.SQLTable;
import com.chiorichan.datastore.sql.SQLTableColumns;
import com.chiorichan.datastore.sql.SQLWrapper;
import com.chiorichan.datastore.sql.skel.SQLSkelValues;
import com.chiorichan.util.StringFunc;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * SQL Query for Insert
 */
public final class SQLQueryInsert extends SQLBase<SQLQueryInsert> implements SQLSkelValues<SQLQueryInsert>, Cloneable
{
	private List<String> requiredValues = Lists.newArrayList();
	private Map<String, Object> values = Maps.newHashMap();
	private String table;

	public SQLQueryInsert( SQLWrapper sql, String table )
	{
		this( sql, table, false );
	}

	public SQLQueryInsert( SQLWrapper sql, String table, boolean autoExecute )
	{
		super( sql, autoExecute );
		this.table = table;

		try
		{
			SQLTableColumns sqlColumns = new SQLTable( sql, table ).columns();
			requiredValues = sqlColumns.columnNamesRequired();
		}
		catch ( SQLException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	protected SQLQueryInsert execute0() throws SQLException
	{
		if ( !hasRequiredColumnsBeenSatisfied() )
			throw new SQLException( "The required columns were not satisfied. Provided columns were '" + Joiner.on( "," ).join( values.keySet() ) + "', required columns are '" + Joiner.on( "," ).join( requiredValues ) + "'" );

		query( toSqlQuery(), true, sqlValues() );
		return this;
	}

	public List<String> getRequiredColumns()
	{
		return requiredValues;
	}

	public boolean hasRequiredColumnsBeenSatisfied()
	{
		return values.keySet().containsAll( requiredValues );
	}

	@Override
	public int rowCount()
	{
		try
		{
			return statement().getUpdateCount();
		}
		catch ( NullPointerException | SQLException e )
		{
			return -1;
		}
	}

	@Override
	public Object[] sqlValues()
	{
		return values.values().toArray();
	}

	public String table()
	{
		return table;
	}

	@Override
	public String toSqlQuery()
	{
		synchronized ( this )
		{
			if ( values.size() == 0 )
				throw new IllegalStateException( "Invalid Query State: There are no values to be inserted" );

			List<String> segments = Lists.newLinkedList();

			segments.add( "INSERT INTO" );

			segments.add( StringFunc.wrap( table(), '`' ) );

			segments.add( String.format( "(%s) VALUES (%s)", Joiner.on( ", " ).join( StringFunc.wrap( values.keySet(), '`' ) ), Joiner.on( ", " ).join( StringFunc.repeatToList( "?", values.values().size() ) ) ) );

			return Joiner.on( " " ).join( segments ) + ";";
		}
	}

	@Override
	public SQLQueryInsert value( String key, Object val )
	{
		values.put( key, val );
		return this;
	}

	@Override
	public SQLQueryInsert values( Map<String, Object> map )
	{
		values.putAll( map );
		return this;
	}

	@Override
	public SQLQueryInsert values( String[] keys, Object[] valuesArray )
	{
		for ( int i = 0; i < Math.min( keys.length, valuesArray.length ); i++ )
			values.put( keys[i], valuesArray[i] );

		if ( keys.length != valuesArray.length )
			DatastoreManager.getLogger().warning( "SQLQueryInsert omited values/keys because the two lengths did not match, so we used the minimum of the two. Keys: (" + Joiner.on( ", " ).join( keys ) + ") Values: (" + Joiner.on( ", " ).join( valuesArray ) + ")" );

		return this;
	}

	@Override
	public SQLQueryInsert clone()
	{
		SQLQueryInsert clone = new SQLQueryInsert( sql, table );

		super.clone( clone );

		clone.requiredValues.addAll( this.requiredValues );
		clone.values.putAll( this.values );

		return clone;
	}
}

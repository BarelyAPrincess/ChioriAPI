/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.datastore.sql;

import com.chiorichan.utils.UtilDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@SuppressWarnings( "rawtypes" )
public class SQLExecute<P extends SQLBase> extends SQLResultSet implements SQLResultSkel
{
	private P parent;

	public SQLExecute( ResultSet result, P parent )
	{
		super( result );
		this.parent = parent;
	}

	@Override
	public Map<String, Map<String, Object>> map() throws SQLException
	{
		return UtilDB.resultToMap( result );
	}

	public P parent()
	{
		return parent;
	}

	@Override
	public Map<String, Object> rowAbsolute( int row ) throws SQLException
	{
		if ( result.absolute( row ) )
			return UtilDB.rowToMap( result );
		return null;
	}

	@Override
	public Map<String, Object> rowFirst() throws SQLException
	{
		if ( result.first() )
			return UtilDB.rowToMap( result );
		return null;
	}

	@Override
	public Map<String, Object> rowLast() throws SQLException
	{
		if ( result.last() )
			return UtilDB.rowToMap( result );
		return null;
	}

	@Override
	public Map<String, Object> row() throws SQLException
	{
		return UtilDB.rowToMap( result );
	}

	@Override
	public int rowCount() throws SQLException
	{
		return parent.rowCount();
	}

	@Override
	public Set<Map<String, Object>> set() throws SQLException
	{
		return UtilDB.resultToSet( result );
	}

	@Override
	public Map<String, Map<String, String>> stringMap() throws SQLException
	{
		return UtilDB.resultToStringMap( result );
	}

	@Override
	public Map<String, String> stringRow() throws SQLException
	{
		return UtilDB.rowToStringMap( result );
	}

	@Override
	public Set<Map<String, String>> stringSet() throws SQLException
	{
		return UtilDB.resultToStringSet( result );
	}

	@Override
	public String toSqlQuery() throws SQLException
	{
		if ( result.getStatement() instanceof PreparedStatement )
			return UtilDB.toString( ( PreparedStatement ) result.getStatement() );
		return null;
	}
}

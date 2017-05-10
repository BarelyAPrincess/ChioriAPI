/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.database;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

public class SqlTableColumns implements Iterable<String>
{
	public class SqlColumn
	{
		public final String name;
		public final int type;
		public final String label;
		public final String className;
		
		SqlColumn( String name, int type, String label, String className )
		{
			this.name = name;
			this.type = type;
			this.label = label;
			this.className = className;
		}
		
		public Object newType()
		{
			switch ( className )
			{
				case "java.lang.String":
					return "";
				case "java.lang.Integer":
					return 0;
				case "java.lang.Boolean":
					return false;
				default:
					// Loader.getLogger().debug( "Column Class: " + className );
					throw new IllegalArgumentException( "We could not instigate the proper column type " + className + " for column " + name + ", this might need to be implemented." );
			}
		}
	}
	
	private final List<SqlColumn> columns = Lists.newArrayList();
	
	void add( ResultSetMetaData metaData, int index ) throws SQLException
	{
		columns.add( new SqlColumn( metaData.getColumnName( index ), metaData.getColumnType( index ), metaData.getColumnLabel( index ), metaData.getColumnClassName( index ) ) );
	}
	
	public int count()
	{
		return columns.size();
	}
	
	public SqlColumn get( String name )
	{
		for ( SqlColumn c : columns )
			if ( c.name.equals( name ) )
				return c;
		return null;
	}
	
	@Override
	public Iterator<String> iterator()
	{
		List<String> rtn = Lists.newArrayList();
		for ( SqlColumn m : columns )
			rtn.add( m.name );
		return rtn.iterator();
	}
}

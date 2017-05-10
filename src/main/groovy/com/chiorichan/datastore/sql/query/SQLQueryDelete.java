/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package com.chiorichan.datastore.sql.query;

import com.chiorichan.datastore.DatastoreManager;
import com.chiorichan.datastore.sql.SQLBase;
import com.chiorichan.datastore.sql.SQLWrapper;
import com.chiorichan.datastore.sql.skel.SQLSkelLimit;
import com.chiorichan.datastore.sql.skel.SQLSkelWhere;
import com.chiorichan.datastore.sql.skel.SQLWhereElement;
import com.chiorichan.datastore.sql.skel.SQLWhereElementSep;
import com.chiorichan.datastore.sql.skel.SQLWhereGroup;
import com.chiorichan.datastore.sql.skel.SQLWhereKeyValue;
import com.chiorichan.utils.UtilStrings;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * SQL Query Interface for Delete
 */
public final class SQLQueryDelete extends SQLBase<SQLQueryDelete> implements SQLSkelWhere<SQLQueryDelete, SQLQueryDelete>, SQLSkelLimit<SQLQueryDelete>
{
	private SQLWhereElementSep currentSeparator = SQLWhereElementSep.NONE;
	private final List<SQLWhereElement> elements = new LinkedList<>();
	private final List<Object> sqlValues = new LinkedList<>();
	private boolean needsUpdate = true;
	private int offset = -1;
	private int limit = -1;
	private String table;

	public SQLQueryDelete( SQLWrapper sql, String table )
	{
		super( sql, false );
		this.table = table;
	}

	public SQLQueryDelete( SQLWrapper sql, String table, boolean autoExecute )
	{
		super( sql, autoExecute );
		this.table = table;
	}

	@Override
	public SQLQueryDelete and()
	{
		if ( elements.size() < 1 )
			currentSeparator = SQLWhereElementSep.NONE;
		else
			currentSeparator = SQLWhereElementSep.AND;
		return this;
	}

	@Override
	protected SQLQueryDelete execute0() throws SQLException
	{
		query( toSqlQuery(), true, sqlValues() );
		return this;
	}

	@Override
	public SQLWhereGroup<SQLQueryDelete, SQLQueryDelete> group()
	{
		SQLWhereGroup<SQLQueryDelete, SQLQueryDelete> group = new SQLWhereGroup<SQLQueryDelete, SQLQueryDelete>( this, this );
		group.seperator( currentSeparator );
		elements.add( group );
		needsUpdate = true;
		or();
		return group;
	}

	@Override
	public int limit()
	{
		return limit;
	}

	@Override
	public SQLQueryDelete limit( int limit )
	{
		this.limit = limit;
		needsUpdate = true;
		return this;
	}

	@Override
	public SQLQueryDelete take( int take )
	{
		return this.limit( take );
	}

	@Override
	public SQLQueryDelete limit( int limit, int offset )
	{
		this.limit = limit;
		this.offset = offset;
		needsUpdate = true;
		return this;
	}

	@Override
	public int offset()
	{
		return offset;
	}

	@Override
	public SQLQueryDelete offset( int offset )
	{
		this.offset = offset;
		needsUpdate = true;
		return this;
	}

	@Override
	public SQLQueryDelete skip( int skip )
	{
		return this.offset( skip );
	}

	@Override
	public SQLQueryDelete or()
	{
		if ( elements.size() < 1 )
			currentSeparator = SQLWhereElementSep.NONE;
		else
			currentSeparator = SQLWhereElementSep.OR;
		return this;
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
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public Object[] sqlValues()
	{
		if ( needsUpdate )
			toSqlQuery();
		return sqlValues.toArray();
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
			List<String> segments = Lists.newLinkedList();

			segments.add( "DELETE FROM" );

			segments.add( UtilStrings.wrap( table(), '`' ) );

			sqlValues.clear();

			if ( elements.size() > 0 )
			{
				segments.add( "WHERE" );

				for ( SQLWhereElement e : elements )
				{
					if ( e.seperator() != SQLWhereElementSep.NONE && e != elements.get( 0 ) )
						segments.add( e.seperator().toString() );
					segments.add( e.toSqlQuery() );
					sqlValues.addAll( e.values().collect( Collectors.toList() ) );
				}
			}

			if ( limit() > 0 )
				segments.add( "LIMIT " + limit() );

			if ( offset() > 0 )
				segments.add( "OFFSET " + offset() );

			needsUpdate = false;

			return Joiner.on( " " ).join( segments ) + ";";
		}
	}

	@Override
	public SQLQueryDelete where( Map<String, Object> map )
	{
		for ( Entry<String, Object> e : map.entrySet() )
		{
			String key = e.getKey();
			Object val = e.getValue();

			if ( key.startsWith( "|" ) )
			{
				key = key.substring( 1 );
				or();
			}
			else if ( key.startsWith( "&" ) )
			{
				key = key.substring( 1 );
				and();
			}

			if ( val instanceof Map )
				try
				{
					SQLWhereGroup<?, ?> group = group();

					@SuppressWarnings( "unchecked" )
					Map<String, Object> submap = ( Map<String, Object> ) val;
					for ( Entry<String, Object> e2 : submap.entrySet() )
					{
						String key2 = e2.getKey();
						Object val2 = e2.getValue();

						if ( key2.startsWith( "|" ) )
						{
							key2 = key2.substring( 1 );
							group.or();
						}
						else if ( key2.startsWith( "&" ) )
						{
							key2 = key2.substring( 1 );
							group.and();
						}

						where( key2 ).matches( val2 );
					}
				}
				catch ( ClassCastException ee )
				{
					DatastoreManager.getLogger().severe( ee );
				}
			else
				where( key ).matches( val );
		}

		return this;
	}

	@Override
	public SQLQueryDelete where( SQLWhereElement element )
	{
		element.seperator( currentSeparator );
		elements.add( element );
		needsUpdate = true;
		and();

		return this;
	}

	@Override
	public SQLWhereKeyValue<SQLQueryDelete> where( String key )
	{
		return new SQLWhereKeyValue<SQLQueryDelete>( this, key );
	}

	@Override
	public SQLQueryDelete whereMatches( Collection<String> valueKeys, Collection<Object> valueValues )
	{
		SQLWhereGroup<SQLQueryDelete, SQLQueryDelete> group = new SQLWhereGroup<SQLQueryDelete, SQLQueryDelete>( this, this );

		List<String> listKeys = new ArrayList<>( valueKeys );
		List<Object> listValues = new ArrayList<>( valueValues );

		for ( int i = 0; i < Math.min( listKeys.size(), listValues.size() ); i++ )
		{
			SQLWhereKeyValue<SQLWhereGroup<SQLQueryDelete, SQLQueryDelete>> groupElement = group.where( listKeys.get( i ) );
			groupElement.seperator( SQLWhereElementSep.AND );
			groupElement.matches( listValues.get( i ) );
		}

		group.parent();
		or();
		return this;
	}

	@Override
	public SQLQueryDelete whereMatches( Map<String, Object> values )
	{
		SQLWhereGroup<SQLQueryDelete, SQLQueryDelete> group = new SQLWhereGroup<SQLQueryDelete, SQLQueryDelete>( this, this );

		for ( Entry<String, Object> val : values.entrySet() )
		{
			SQLWhereKeyValue<SQLWhereGroup<SQLQueryDelete, SQLQueryDelete>> groupElement = group.where( val.getKey() );
			groupElement.seperator( SQLWhereElementSep.AND );
			groupElement.matches( val.getValue() );
		}

		group.parent();
		or();
		return this;
	}

	@Override
	public SQLQueryDelete whereMatches( String key, Object value )
	{
		return new SQLWhereKeyValue<SQLQueryDelete>( this, key ).matches( value );
	}

	@Override
	public SQLQueryDelete clone()
	{
		SQLQueryDelete clone = new SQLQueryDelete( sql, table );

		super.clone( clone );

		clone.currentSeparator = this.currentSeparator;
		clone.elements.addAll( this.elements );
		clone.sqlValues.addAll( this.sqlValues );
		clone.needsUpdate = this.needsUpdate;
		clone.offset = this.offset;
		clone.limit = this.limit;

		return clone;
	}

	@Override
	public SQLWhereElementSep separator()
	{
		return currentSeparator;
	}
}

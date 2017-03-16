/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.datastore.sql.query;

import com.chiorichan.datastore.DatastoreManager;
import com.chiorichan.datastore.sql.SQLBase;
import com.chiorichan.datastore.sql.SQLWrapper;
import com.chiorichan.datastore.sql.skel.SQLSkelLimit;
import com.chiorichan.datastore.sql.skel.SQLSkelValues;
import com.chiorichan.datastore.sql.skel.SQLSkelWhere;
import com.chiorichan.datastore.sql.skel.SQLWhereElement;
import com.chiorichan.datastore.sql.skel.SQLWhereElementSep;
import com.chiorichan.datastore.sql.skel.SQLWhereGroup;
import com.chiorichan.datastore.sql.skel.SQLWhereKeyValue;
import com.chiorichan.zutils.ZStrings;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * SQL Query for Update
 */
public final class SQLQueryUpdate extends SQLBase<SQLQueryUpdate> implements SQLSkelValues<SQLQueryUpdate>, SQLSkelWhere<SQLQueryUpdate, SQLQueryUpdate>, SQLSkelLimit<SQLQueryUpdate>, Cloneable
{
	private SQLWhereElementSep currentSeparator = SQLWhereElementSep.NONE;
	private final List<SQLWhereElement> elements = Lists.newLinkedList();
	private final List<Object> sqlValues = Lists.newLinkedList();
	private final Map<String, Object> values = Maps.newHashMap();
	private boolean needsUpdate = true;
	private int offset = -1;
	private int limit = -1;
	private String table;

	public SQLQueryUpdate( SQLWrapper sql, String table )
	{
		super( sql, false );
		this.table = table;
	}

	public SQLQueryUpdate( SQLWrapper sql, String table, boolean autoExecute )
	{
		super( sql, autoExecute );
		this.table = table;
	}

	@Override
	public SQLQueryUpdate and()
	{
		if ( elements.size() < 1 )
			currentSeparator = SQLWhereElementSep.NONE;
		else
			currentSeparator = SQLWhereElementSep.AND;
		return this;
	}

	@Override
	protected SQLQueryUpdate execute0() throws SQLException
	{
		query( toSqlQuery(), true, sqlValues() );
		return this;
	}

	@Override
	public SQLWhereGroup<SQLQueryUpdate, SQLQueryUpdate> group()
	{
		SQLWhereGroup<SQLQueryUpdate, SQLQueryUpdate> group = new SQLWhereGroup<SQLQueryUpdate, SQLQueryUpdate>( this, this );
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
	public SQLQueryUpdate limit( int limit )
	{
		this.limit = limit;
		needsUpdate = true;
		return this;
	}

	@Override
	public SQLQueryUpdate take( int take )
	{
		return this.limit( take );
	}

	@Override
	public SQLQueryUpdate limit( int limit, int offset )
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
	public SQLQueryUpdate offset( int offset )
	{
		this.offset = offset;
		needsUpdate = true;
		return this;
	}

	@Override
	public SQLQueryUpdate skip( int skip )
	{
		return this.offset( skip );
	}

	@Override
	public SQLQueryUpdate or()
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
			return -1;
		}
	}

	@Override
	public Object[] sqlValues()
	{
		if ( needsUpdate )
			toSqlQuery();
		return ArrayUtils.addAll( values.values().toArray( new Object[0] ), sqlValues.toArray() );
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
				throw new IllegalStateException( "Invalid Query State: There are no values to be updated" );

			List<String> segments = Lists.newLinkedList();

			segments.add( "UPDATE" );

			segments.add( ZStrings.wrap( table(), '`' ) );

			segments.add( "SET" );

			List<String> sets = Lists.newLinkedList();

			for ( String key : values.keySet() )
				sets.add( String.format( "`%s` = ?", key ) );

			segments.add( Joiner.on( ", " ).join( sets ) );

			sqlValues.clear();

			if ( elements.size() > 0 )
			{
				segments.add( "WHERE" );

				for ( SQLWhereElement e : elements )
				{
					if ( e.seperator() != SQLWhereElementSep.NONE && e != elements.get( 0 ) )
						segments.add( e.seperator().toString() );
					segments.add( String.format( e.toSqlQuery(), "?" ) );
					if ( e.value() == null )
						sqlValues.add( "null" );
					else
						sqlValues.add( e.value() );
				}
			}

			if ( limit() > 0 )
				segments.add( "LIMIT " + limit() );

			needsUpdate = false;

			return Joiner.on( " " ).join( segments ) + ";";
		}
	}

	@Override
	public SQLQueryUpdate value( String key, Object val )
	{
		values.put( key, val );
		needsUpdate = true;
		return this;
	}

	@Override
	public SQLQueryUpdate values( Map<String, Object> map )
	{
		for ( Entry<String, Object> e : map.entrySet() )
			values.put( e.getKey(), e.getValue() );
		needsUpdate = true;
		return this;
	}

	@Override
	public SQLQueryUpdate values( String[] keys, Object[] valuesArray )
	{
		for ( int i = 0; i < Math.min( keys.length, valuesArray.length ); i++ )
			values.put( keys[i], valuesArray[i] );

		if ( keys.length != valuesArray.length )
			DatastoreManager.getLogger().warning( "SQLQueryUpdate omitted values/keys because the two lengths did not match, so we used the minimum of the two. Keys: (" + Joiner.on( ", " ).join( keys ) + ") Values: (" + Joiner.on( ", " ).join( valuesArray ) + ")" );

		needsUpdate = true;

		return this;
	}

	@Override
	public SQLQueryUpdate where( Map<String, Object> map )
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
	public SQLQueryUpdate where( SQLWhereElement element )
	{
		element.seperator( currentSeparator );
		elements.add( element );
		needsUpdate = true;
		and();

		return this;
	}

	@Override
	public SQLWhereKeyValue<SQLQueryUpdate> where( String key )
	{
		return new SQLWhereKeyValue<SQLQueryUpdate>( this, key );
	}

	@Override
	public SQLQueryUpdate whereMatches( Collection<String> valueKeys, Collection<Object> valueValues )
	{
		SQLWhereGroup<SQLQueryUpdate, SQLQueryUpdate> group = new SQLWhereGroup<SQLQueryUpdate, SQLQueryUpdate>( this, this );

		List<String> listKeys = new ArrayList<>( valueKeys );
		List<Object> listValues = new ArrayList<>( valueValues );

		for ( int i = 0; i < Math.min( listKeys.size(), listValues.size() ); i++ )
		{
			SQLWhereKeyValue<SQLWhereGroup<SQLQueryUpdate, SQLQueryUpdate>> groupElement = group.where( listKeys.get( i ) );
			groupElement.seperator( SQLWhereElementSep.AND );
			groupElement.matches( listValues.get( i ) );
		}

		group.parent();
		or();
		return this;
	}

	@Override
	public SQLQueryUpdate whereMatches( Map<String, Object> values )
	{
		SQLWhereGroup<SQLQueryUpdate, SQLQueryUpdate> group = new SQLWhereGroup<SQLQueryUpdate, SQLQueryUpdate>( this, this );

		for ( Entry<String, Object> val : values.entrySet() )
		{
			SQLWhereKeyValue<SQLWhereGroup<SQLQueryUpdate, SQLQueryUpdate>> groupElement = group.where( val.getKey() );
			groupElement.seperator( SQLWhereElementSep.AND );
			groupElement.matches( val.getValue() );
		}

		group.parent();
		or();
		return this;
	}

	@Override
	public SQLQueryUpdate whereMatches( String key, Object value )
	{
		return new SQLWhereKeyValue<SQLQueryUpdate>( this, key ).matches( value );
	}

	@Override
	public SQLQueryUpdate clone()
	{
		SQLQueryUpdate clone = new SQLQueryUpdate( sql, table );

		super.clone( clone );

		clone.currentSeparator = this.currentSeparator;
		clone.sqlValues.addAll( this.sqlValues );
		clone.elements.addAll( this.elements );
		clone.needsUpdate = this.needsUpdate;
		clone.values.putAll( this.values );
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

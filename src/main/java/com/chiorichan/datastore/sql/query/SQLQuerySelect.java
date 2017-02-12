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
import com.chiorichan.datastore.sql.skel.SQLSkelGroupBy;
import com.chiorichan.datastore.sql.skel.SQLSkelLimit;
import com.chiorichan.datastore.sql.skel.SQLSkelOrderBy;
import com.chiorichan.datastore.sql.skel.SQLSkelWhere;
import com.chiorichan.datastore.sql.skel.SQLWhereElement;
import com.chiorichan.datastore.sql.skel.SQLWhereElementSep;
import com.chiorichan.datastore.sql.skel.SQLWhereGroup;
import com.chiorichan.datastore.sql.skel.SQLWhereKeyValue;
import com.chiorichan.zutils.ZStrings;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class SQLQuerySelect extends SQLBase<SQLQuerySelect> implements SQLSkelWhere<SQLQuerySelect, SQLQuerySelect>, SQLSkelLimit<SQLQuerySelect>, SQLSkelOrderBy<SQLQuerySelect>, SQLSkelGroupBy<SQLQuerySelect>, Cloneable
{
	private SQLWhereElementSep currentSeparator = SQLWhereElementSep.NONE;
	private final List<SQLWhereElement> elements = new LinkedList<>();
	private final List<Object> sqlValues = new LinkedList<>();
	private final List<String> orderBy = new LinkedList<>();
	private final List<String> groupBy = new LinkedList<>();
	private final List<String> fields = new LinkedList<>();
	private boolean orderAscending = true;
	private boolean needsUpdate = true;
	private int offset = -1;
	private int limit = -1;
	private String table;

	public SQLQuerySelect( SQLWrapper sql, String table )
	{
		super( sql, true );
		this.table = table;
	}

	public SQLQuerySelect( SQLWrapper sql, String table, boolean autoExecute )
	{
		super( sql, autoExecute );
		this.table = table;
	}

	@Override
	public SQLQuerySelect and()
	{
		if ( elements.size() < 1 )
			currentSeparator = SQLWhereElementSep.NONE;
		else
			currentSeparator = SQLWhereElementSep.AND;
		return this;
	}

	@Override
	protected SQLQuerySelect execute0() throws SQLException
	{
		query( toSqlQuery(), false, sqlValues() );
		return this;
	}

	public SQLQuerySelect fields( Collection<String> fields )
	{
		this.fields.addAll( fields );
		updateExecution();
		needsUpdate = true;
		return this;
	}

	public SQLQuerySelect fields( String field )
	{
		fields.add( field );
		updateExecution();
		needsUpdate = true;
		return this;
	}

	public SQLQuerySelect fields( String... fields )
	{
		this.fields.addAll( Arrays.asList( fields ) );
		updateExecution();
		needsUpdate = true;
		return this;
	}

	@Override
	public SQLWhereGroup<SQLQuerySelect, SQLQuerySelect> group()
	{
		SQLWhereGroup<SQLQuerySelect, SQLQuerySelect> group = new SQLWhereGroup<SQLQuerySelect, SQLQuerySelect>( this, this );
		group.seperator( currentSeparator );
		elements.add( group );
		needsUpdate = true;
		or();
		return group;
	}

	@Override
	public SQLQuerySelect groupBy( Collection<String> columns )
	{
		groupBy.addAll( columns );
		updateExecution();
		needsUpdate = true;
		return this;
	}

	@Override
	public SQLQuerySelect groupBy( String... columns )
	{
		groupBy.addAll( Arrays.asList( columns ) );
		updateExecution();
		needsUpdate = true;
		return this;
	}

	@Override
	public SQLQuerySelect groupBy( String column )
	{
		groupBy.add( column );
		updateExecution();
		needsUpdate = true;
		return this;
	}

	@Override
	public int limit()
	{
		return limit;
	}

	@Override
	public SQLQuerySelect limit( int limit )
	{
		this.limit = limit;
		updateExecution();
		needsUpdate = true;
		return this;
	}

	@Override
	public SQLQuerySelect take( int take )
	{
		return this.limit( take );
	}

	@Override
	public SQLQuerySelect limit( int limit, int offset )
	{
		this.limit = limit;
		this.offset = offset;
		updateExecution();
		needsUpdate = true;
		return this;
	}

	@Override
	public int offset()
	{
		return offset;
	}

	@Override
	public SQLQuerySelect offset( int offset )
	{
		this.offset = offset;
		needsUpdate = true;
		return this;
	}

	@Override
	public SQLQuerySelect skip( int skip )
	{
		return this.offset( skip );
	}

	@Override
	public SQLQuerySelect or()
	{
		if ( elements.size() < 1 )
			currentSeparator = SQLWhereElementSep.NONE;
		else
			currentSeparator = SQLWhereElementSep.OR;
		return this;
	}

	@Override
	public SQLQuerySelect orderAsc()
	{
		orderAscending = true;
		return this;
	}

	@Override
	public SQLQuerySelect orderBy( Collection<String> columns )
	{
		orderBy.addAll( columns );
		needsUpdate = true;
		return this;
	}

	@Override
	public SQLQuerySelect orderBy( String... columns )
	{
		orderBy.addAll( Arrays.asList( columns ) );
		needsUpdate = true;
		return this;
	}

	@Override
	public SQLQuerySelect orderBy( String column )
	{
		orderBy.add( column );
		needsUpdate = true;
		return this;
	}

	@Override
	public SQLQuerySelect orderDesc()
	{
		orderAscending = false;
		return this;
	}

	@Override
	public int rowCount()
	{
		try
		{
			String query = toSqlQuery0( true );
			ResultSet rs = query( query, false, false, sqlValues() ).getResultSet();
			rs.next();
			return rs.getInt( 1 );
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
		return toSqlQuery0( false );
	}

	private String toSqlQuery0( boolean rowCount )
	{
		synchronized ( this )
		{
			List<String> segments = Lists.newLinkedList();

			segments.add( "SELECT" );

			if ( rowCount )
				segments.add( "COUNT(*)" );
			else if ( fields.size() == 0 )
				segments.add( "*" );
			else
				segments.add( Joiner.on( ", " ).join( ZStrings.wrap( fields, '`' ) ) );

			segments.add( "FROM" );

			segments.add( ZStrings.wrap( table(), '`' ) );

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

			if ( groupBy.size() > 0 )
				segments.add( "GROUP BY " + Joiner.on( ", " ).join( ZStrings.wrap( groupBy, '`' ) ) );

			if ( orderBy.size() > 0 )
				segments.add( "ORDER BY " + Joiner.on( ", " ).join( ZStrings.wrap( orderBy, '`' ) ) + ( orderAscending ? " ASC" : " DESC" ) );

			if ( limit() > 0 )
				segments.add( "LIMIT " + limit() );

			if ( offset() > 0 )
				segments.add( "OFFSET " + offset() );

			needsUpdate = false;

			return Joiner.on( " " ).join( segments ) + ";";
		}
	}

	@Override
	public SQLQuerySelect where( Map<String, Object> map )
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
	public SQLQuerySelect where( SQLWhereElement element )
	{
		element.seperator( currentSeparator );
		elements.add( element );
		needsUpdate = true;
		and();

		return this;
	}

	@Override
	public SQLWhereKeyValue<SQLQuerySelect> where( String key )
	{
		return new SQLWhereKeyValue<SQLQuerySelect>( this, key );
	}

	@Override
	public SQLQuerySelect whereMatches( Collection<String> valueKeys, Collection<Object> valueValues )
	{
		SQLWhereGroup<SQLQuerySelect, SQLQuerySelect> group = new SQLWhereGroup<SQLQuerySelect, SQLQuerySelect>( this, this );

		List<String> listKeys = new ArrayList<>( valueKeys );
		List<Object> listValues = new ArrayList<>( valueValues );

		for ( int i = 0; i < Math.min( listKeys.size(), listValues.size() ); i++ )
		{
			SQLWhereKeyValue<SQLWhereGroup<SQLQuerySelect, SQLQuerySelect>> groupElement = group.where( listKeys.get( i ) );
			groupElement.seperator( SQLWhereElementSep.AND );
			groupElement.matches( listValues.get( i ) );
		}

		group.parent();
		or();
		return this;
	}

	@Override
	public SQLQuerySelect whereMatches( Map<String, Object> values )
	{
		SQLWhereGroup<SQLQuerySelect, SQLQuerySelect> group = new SQLWhereGroup<SQLQuerySelect, SQLQuerySelect>( this, this );

		for ( Entry<String, Object> val : values.entrySet() )
		{
			SQLWhereKeyValue<SQLWhereGroup<SQLQuerySelect, SQLQuerySelect>> groupElement = group.where( val.getKey() );
			groupElement.seperator( SQLWhereElementSep.AND );
			groupElement.matches( val.getValue() );
		}

		group.parent();
		or();
		return this;
	}

	@Override
	public SQLQuerySelect whereMatches( String key, Object value )
	{
		return new SQLWhereKeyValue<SQLQuerySelect>( this, key ).matches( value );
	}

	// TODO Consider adding whereLessThan, whereMoreThan, whereLike, whereLikeWild, whereBetween, whereNotLike, whereNot, whereRegEx methods, unless this is lazy!

	@Override
	public SQLQuerySelect clone()
	{
		SQLQuerySelect clone = new SQLQuerySelect( sql, table );

		super.clone( clone );

		clone.elements.addAll( this.elements );
		clone.currentSeparator = this.currentSeparator;
		clone.orderBy.addAll( this.orderBy );
		clone.orderAscending = this.orderAscending;
		clone.groupBy.addAll( this.groupBy );
		clone.fields.addAll( this.fields );
		clone.sqlValues.addAll( this.sqlValues );
		clone.needsUpdate = this.needsUpdate;
		clone.limit = this.limit;
		clone.offset = this.offset;

		return clone;
	}
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package com.chiorichan.datastore.sql.skel;

import org.apache.commons.lang3.Validate;

/**
 *
 */
public final class SQLWhereKeyValue<T extends SQLSkelWhere<?, ?>> extends SQLWhereElement
{
	enum Operands
	{
		EQUAL( "=" ), NOT_EQUAL( "!=" ), LIKE( "LIKE" ), NOT_LIKE( "NOT LIKE" ), GREATER( ">" ), GREATEREQUAL( ">=" ), LESSER( "<" ), LESSEREQUAL( "<=" ), REGEXP( "REGEXP" );

		private String operator;

		Operands( String operator )
		{
			this.operator = operator;
		}

		String stringValue()
		{
			return operator;
		}
	}

	private final String key;
	private Operands operator = Operands.EQUAL;
	private Object value = "";
	private final T parent;

	public SQLWhereKeyValue( T parent, String key )
	{
		Validate.notNull( parent );
		this.key = key;
		this.parent = parent;
	}

	public T range( Object n1, Object n2 )
	{
		moreEqualThan( n1 );
		parent.and().where( key ).lessEqualThan( n2 );
		return parent;
	}

	public T between( Object n1, Object n2 )
	{
		moreThan( n1 );
		parent.and().where( key ).lessThan( n2 );
		return parent;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( !( obj instanceof SQLWhereKeyValue ) )
			throw new IllegalArgumentException( "Received a call to the equals() method for the SQLWhereKeyValue class! We could be wrong but since the object was not an instance of this class, we decided to alert you that if you were attempting to match a key and value, the correct method would be matches()." );
		return super.equals( obj );
	}

	protected String key()
	{
		return key;
	}

	public T lessEqualThan( Object n )
	{
		operator = Operands.LESSEREQUAL;
		value = n;
		parent.where( this );
		return parent;
	}

	public T lessThan( Object n )
	{
		operator = Operands.LESSER;
		value = n;
		parent.where( this );
		return parent;
	}

	public T like( String value )
	{
		operator = Operands.LIKE;
		this.value = value;
		parent.where( this );
		return parent;
	}

	/**
	 * Similar to {@link #like(String)}, except will wrap the value with wild card characters if none exist.
	 */
	public T likeWild( String value )
	{
		if ( !value.contains( "%" ) )
			value = "%" + value + "%";

		return like( value );
	}

	public T matches( Object value )
	{
		operator = Operands.EQUAL;
		this.value = value;
		parent.where( this );
		return parent;
	}

	public T moreEqualThan( Object n )
	{
		operator = Operands.GREATEREQUAL;
		value = n;
		parent.where( this );
		return parent;
	}

	public T moreThan( Object n )
	{
		operator = Operands.GREATER;
		value = n;
		parent.where( this );
		return parent;
	}

	public T not( Object value )
	{
		operator = Operands.NOT_EQUAL;
		this.value = value;
		parent.where( this );
		return parent;
	}

	public T notLike( String value )
	{
		operator = Operands.NOT_LIKE;
		this.value = value;
		parent.where( this );
		return parent;
	}

	protected Operands operand()
	{
		return operator;
	}

	public T regex( String value )
	{
		operator = Operands.REGEXP;
		this.value = value;
		parent.where( this );
		return parent;
	}

	@Override
	public String toSqlQuery()
	{
		return String.format( "`%s` %s %%s", key, operator.stringValue() );
	}

	@Override
	public String toString()
	{
		return toSqlQuery();
	}

	@Override
	public Object value()
	{
		return value;
	}
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.datastore.sql.skel;


/**
 * Provides the Skeleton Interface for SQL Queries implementing the Limit Methods
 */
public interface SQLSkelLimit<T>
{
	int limit();

	T limit( int limit, int offset );

	T limit( int limit );

	T take( int take );

	int offset();

	T offset( int offset );

	T skip( int skip );
}

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

import java.util.Map;

/**
 * Provides the Skeleton Interface for SQL Queries implementing the Values Methods
 */
public interface SQLSkelValues<T>
{
	T values( Map<String, Object> map );
	
	T value( String key, Object val );
	
	T values( String[] keys, Object[] valuesArray );
}

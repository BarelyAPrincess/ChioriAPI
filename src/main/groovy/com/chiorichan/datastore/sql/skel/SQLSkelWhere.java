/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.datastore.sql.skel;

import java.util.Collection;
import java.util.Map;

/**
 * Provides the Skeleton Interface for SQL Queries implementing the Where Methods
 */
public interface SQLSkelWhere<B extends SQLSkelWhere<?, ?>, P>
{
	B or();

	B and();

	SQLWhereGroup<B, P> group();

	SQLWhereKeyValue<B> where( String key );

	SQLWhereElementSep separator();

	B where( SQLWhereElement element );

	B where( Map<String, Object> map );

	B whereMatches( Map<String, Object> values );

	B whereMatches( String key, Object value );

	B whereMatches( Collection<String> valueKeys, Collection<Object> valueValues );
}

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

import java.util.Collection;

/**
 *
 */
public interface SQLSkelOrderBy<T>
{
	T orderBy( Collection<String> columns );

	T orderBy( String... columns );

	T orderBy( String column );

	T orderAsc();

	T orderDesc();

	T rand();

	T rand( boolean rand );
}

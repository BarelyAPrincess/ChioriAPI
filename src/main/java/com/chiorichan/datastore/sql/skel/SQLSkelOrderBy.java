/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
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
}

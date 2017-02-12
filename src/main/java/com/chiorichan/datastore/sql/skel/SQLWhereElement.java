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


/**
 * 
 */
public abstract class SQLWhereElement
{
	private SQLWhereElementSep separator = SQLWhereElementSep.NONE;
	
	public final SQLWhereElementSep seperator()
	{
		return separator;
	}
	
	public final void seperator( SQLWhereElementSep seperator )
	{
		this.separator = seperator;
	}
	
	public abstract String toSqlQuery();
	
	public Object value()
	{
		return null;
	}
}

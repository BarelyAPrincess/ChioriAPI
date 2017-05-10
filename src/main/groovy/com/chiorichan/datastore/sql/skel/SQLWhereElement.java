/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package com.chiorichan.datastore.sql.skel;

import java.util.stream.Stream;

/**
 * Skel for where subclasses
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

	public abstract Stream<Object> values();
}

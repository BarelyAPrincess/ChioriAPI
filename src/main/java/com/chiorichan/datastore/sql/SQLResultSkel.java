/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.datastore.sql;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 * Provides a SQL Result/Execute skeleton for {@link com.chiorichan.datastore.sql.SQLBase} and {@link com.chiorichan.datastore.sql.SQLExecute}
 */
public interface SQLResultSkel
{
	Map<String, Map<String, Object>> map() throws SQLException;

	Set<Map<String, Object>> set() throws SQLException;

	Map<String, Map<String, String>> stringMap() throws SQLException;

	Map<String, Object> row() throws SQLException;

	Map<String, Object> rowAbsolute( int row ) throws SQLException;

	Map<String, Object> rowFirst() throws SQLException;

	Map<String, Object> rowLast() throws SQLException;

	Map<String, String> stringRow() throws SQLException;

	Set<Map<String, String>> stringSet() throws SQLException;

	int rowCount() throws SQLException;

	String toSqlQuery() throws SQLException;
}

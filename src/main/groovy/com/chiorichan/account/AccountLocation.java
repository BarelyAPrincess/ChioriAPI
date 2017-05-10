/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.account;

import com.chiorichan.datastore.sql.SQLTable;

import java.io.File;
import java.util.Set;

public interface AccountLocation
{
	String getId();

	SQLTable getAccountTable();

	File getAccountDirectory();

	Set<String> getAccountFields();
}

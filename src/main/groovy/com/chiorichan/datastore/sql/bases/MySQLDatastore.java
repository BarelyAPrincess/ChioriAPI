/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.datastore.sql.bases;

import com.chiorichan.datastore.DatastoreManager;
import com.chiorichan.datastore.sql.SQLWrapper;
import com.chiorichan.lang.StartupException;

import java.net.ConnectException;
import java.sql.SQLException;


/**
 *
 */
public class MySQLDatastore extends SQLDatastore
{
	private String user;
	private String pass;
	private String connection;

	public MySQLDatastore( String db, String user, String pass, String host, String port ) throws StartupException
	{
		if ( host == null )
			host = "localhost";

		if ( port == null )
			port = "3306";

		try
		{
			Class.forName( "com.mysql.jdbc.Driver" );
		}
		catch ( ClassNotFoundException e )
		{
			throw new StartupException( "We could not locate the 'com.mysql.jdbc.Driver' library, be sure to have this library in your build path." );
		}

		this.user = user;
		this.pass = pass;
		connection = "jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&useUnicode=yes";

		try
		{
			sql = new SQLWrapper( this, connection, user, pass );
		}
		catch ( SQLException e )
		{
			if ( e.getCause() instanceof ConnectException )
				throw new StartupException( "We had a problem connecting to MySQL database '" + db + "' at host '" + host + ":" + port + "', exception: " + e.getCause().getMessage() );
			else
				throw new StartupException( e );
		}

		DatastoreManager.getLogger().info( "We successfully connected to the sql database using '" + connection + "'." );
	}

	public String getConnectionString()
	{
		return connection;
	}
}

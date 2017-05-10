/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.datastore.sql.bases;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;

import com.chiorichan.AppConfig;
import com.chiorichan.datastore.DatastoreManager;
import com.chiorichan.datastore.sql.SQLWrapper;
import com.chiorichan.lang.StartupException;
import com.chiorichan.utils.UtilIO;

/**
 *
 */
public class SQLiteDatastore extends SQLDatastore
{
	String connection;

	public SQLiteDatastore( String filename ) throws StartupException
	{
		try
		{
			Class.forName( "org.sqlite.JDBC" );
		}
		catch ( ClassNotFoundException e )
		{
			throw new StartupException( "We could not locate the 'org.sqlite.JDBC' library, be sure to have this library in your build path." );
		}

		File sqliteDb = UtilIO.isAbsolute( filename ) ? new File( filename ) : new File( AppConfig.get().getDirectory().getAbsolutePath(), filename );

		if ( !sqliteDb.exists() )
		{
			DatastoreManager.getLogger().warning( "The SQLite file '" + sqliteDb.getAbsolutePath() + "' did not exist, we will attempt to create a blank one now." );
			try
			{
				sqliteDb.createNewFile();
			}
			catch ( IOException e )
			{
				throw new StartupException( "We had a problem creating the SQLite file, the exact exception message was: " + e.getMessage(), e );
			}
		}

		connection = "jdbc:sqlite:" + sqliteDb.getAbsolutePath();

		try
		{
			sql = new SQLWrapper( this, connection );
		}
		catch ( SQLException e )
		{
			if ( e.getCause() instanceof ConnectException )
				throw new StartupException( "We had a problem connecting to SQLite file '" + sqliteDb.getAbsolutePath() + "', exception: " + e.getCause().getMessage() );
			else
				throw new StartupException( e );
		}

		DatastoreManager.getLogger().info( "We successfully connected to the sqLite database with connection string '" + connection + "'" );
	}
}

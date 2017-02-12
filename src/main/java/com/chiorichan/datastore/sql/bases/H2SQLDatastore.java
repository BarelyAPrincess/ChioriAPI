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

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;

import com.chiorichan.datastore.DatastoreManager;
import com.chiorichan.datastore.sql.SQLWrapper;
import com.chiorichan.lang.StartupException;

/**
 *
 */
public class H2SQLDatastore extends SQLDatastore
{
	String connection;

	public H2SQLDatastore( String filename ) throws StartupException
	{
		try
		{
			Class.forName( "org.h2.Driver" );
		}
		catch ( ClassNotFoundException e )
		{
			throw new StartupException( "We could not locate the 'org.h2.Driver' library, be sure to have this library in your build path." );
		}

		File h2Db = new File( filename );

		if ( !h2Db.exists() )
		{
			DatastoreManager.getLogger().warning( "The H2 file '" + h2Db.getAbsolutePath() + "' did not exist, we will attempt to create a blank one now." );
			try
			{
				h2Db.createNewFile();
			}
			catch ( IOException e )
			{
				throw new StartupException( "We had a problem creating the SQLite file, the exact exception message was: " + e.getMessage(), e );
			}
		}

		connection = "jdbc:h2:" + h2Db.getAbsolutePath();

		try
		{
			sql = new SQLWrapper( this, connection );
		}
		catch ( SQLException e )
		{
			if ( e.getCause() instanceof ConnectException )
				throw new StartupException( "We had a problem connecting to H2 file '" + filename + "', exception: " + e.getCause().getMessage() );
			else
				throw new StartupException( e );
		}

		DatastoreManager.getLogger().info( "We successfully connected to the H2 database using 'jdbc:h2:" + h2Db.getAbsolutePath() + "'" );
	}
}

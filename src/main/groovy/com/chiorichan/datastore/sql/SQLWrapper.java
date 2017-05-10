/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.datastore.sql;

import com.chiorichan.datastore.DatastoreManager;
import com.chiorichan.datastore.sql.bases.SQLDatastore;
import com.mysql.jdbc.CommunicationsException;
import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
import org.apache.commons.lang3.Validate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Wraps the SQL Connection
 */
public class SQLWrapper
{
	private SQLDatastore ds;
	private Connection sql;
	private String savedConnection, savedUser = null, savedPass = null;

	public SQLWrapper( SQLDatastore ds, Connection sql )
	{
		savedConnection = null;
		this.ds = ds;
		this.sql = sql;
	}

	public SQLWrapper( SQLDatastore ds, String connection ) throws SQLException
	{
		this.ds = ds;
		connect( connection );
	}

	public SQLWrapper( SQLDatastore ds, String connection, String user, String pass ) throws SQLException
	{
		this.ds = ds;
		connect( connection, user, pass );
	}

	void connect() throws SQLException
	{
		if ( savedUser != null && savedPass != null )
			connect( savedConnection, savedUser, savedPass );
		else
			connect( savedConnection );
	}

	void connect( String connection ) throws SQLException
	{
		if ( sql != null && !sql.isClosed() )
			sql.close();

		Validate.notNull( connection );

		savedConnection = connection;
		sql = DriverManager.getConnection( connection );
		sql.setAutoCommit( true );
	}

	void connect( String connection, String user, String pass ) throws SQLException
	{
		if ( sql != null && !sql.isClosed() )
			sql.close();

		Validate.notNull( connection );
		Validate.notNull( user );

		savedConnection = connection;
		savedUser = user;
		savedPass = pass;
		sql = DriverManager.getConnection( connection, user, pass );
		sql.setAutoCommit( true );
	}

	public SQLDatastore datastore()
	{
		return ds;
	}

	public Connection direct()
	{
		return sql;
	}

	public DatabaseMetaData getMetaData() throws SQLException
	{
		return sql.getMetaData();
	}

	public boolean isClosed() throws SQLException
	{
		return sql.isClosed();
	}

	public boolean isConnected()
	{
		if ( sql == null )
			return false;

		try
		{
			return !sql.isClosed();
		}
		catch ( SQLException e )
		{
			return false;
		}
	}

	PreparedStatement prepareStatement( String query ) throws SQLException
	{
		return prepareStatement( query, false );
	}

	PreparedStatement prepareStatement( String query, boolean retry ) throws SQLException
	{
		try
		{
			return sql.prepareStatement( query );
		}
		catch ( CommunicationsException | MySQLNonTransientConnectionException e )
		{
			if ( !retry && reconnect() )
				return prepareStatement( query, true );
			else
				throw e;
		}
	}

	PreparedStatement prepareStatement( String query, boolean retry, int resultSetType ) throws SQLException
	{
		try
		{
			return sql.prepareStatement( query, resultSetType );
		}
		catch ( CommunicationsException | MySQLNonTransientConnectionException e )
		{
			if ( !retry && reconnect() )
				return prepareStatement( query, true, resultSetType );
			else
				throw e;
		}
	}

	PreparedStatement prepareStatement( String query, boolean retry, int resultSetType, int resultSetConcurrency ) throws SQLException
	{
		try
		{
			return sql.prepareStatement( query, resultSetType, resultSetConcurrency );
		}
		catch ( CommunicationsException | MySQLNonTransientConnectionException e )
		{
			if ( !retry && reconnect() )
				return prepareStatement( query, true, resultSetType, resultSetConcurrency );
			else
				throw e;
		}
	}

	PreparedStatement prepareStatement( String query, boolean retry, int resultSetType, int resultSetConcurrency, int resultSetHoldability ) throws SQLException
	{
		try
		{
			return sql.prepareStatement( query, resultSetType, resultSetConcurrency, resultSetHoldability );
		}
		catch ( CommunicationsException | MySQLNonTransientConnectionException e )
		{
			if ( !retry && reconnect() )
				return prepareStatement( query, true, resultSetType, resultSetConcurrency, resultSetHoldability );
			else
				throw e;
		}
	}

	PreparedStatement prepareStatement( String query, int resultSetType ) throws SQLException
	{
		return prepareStatement( query, false, resultSetType );
	}

	PreparedStatement prepareStatement( String query, int resultSetType, int resultSetConcurrency ) throws SQLException
	{
		return prepareStatement( query, false, resultSetType, resultSetConcurrency );
	}

	PreparedStatement prepareStatement( String query, int resultSetType, int resultSetConcurrency, int resultSetHoldability ) throws SQLException
	{
		return prepareStatement( query, false, resultSetType, resultSetConcurrency, resultSetHoldability );
	}

	public boolean reconnect()
	{
		if ( isConnected() )
			return true;
		return reconnect0();
	}

	boolean reconnect0()
	{
		try
		{
			Validate.notNull( savedConnection );

			connect();
			DatastoreManager.getLogger().info( "We successfully connected to the sql database. Connection: " + savedConnection );
			return true;
		}
		catch ( Exception e )
		{
			DatastoreManager.getLogger().severe( "There was an error reconnecting. Connection: " + savedConnection, e );
		}
		return false;
	}

	public String getConnectionString()
	{
		return savedConnection;
	}
}

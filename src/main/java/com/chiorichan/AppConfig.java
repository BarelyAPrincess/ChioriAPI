/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com> All Right Reserved.
 */
package com.chiorichan;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;

import com.chiorichan.configuration.Configuration;
import com.chiorichan.configuration.ConfigurationOptions;
import com.chiorichan.configuration.ConfigurationSection;
import com.chiorichan.configuration.InvalidConfigurationException;
import com.chiorichan.configuration.file.YamlConfiguration;
import com.chiorichan.datastore.DatastoreManager;
import com.chiorichan.datastore.sql.bases.H2SQLDatastore;
import com.chiorichan.datastore.sql.bases.MySQLDatastore;
import com.chiorichan.datastore.sql.bases.SQLDatastore;
import com.chiorichan.datastore.sql.bases.SQLiteDatastore;
import com.chiorichan.lang.ReportingLevel;
import com.chiorichan.lang.StartupException;
import com.chiorichan.lang.UncaughtException;
import com.chiorichan.logger.Log;
import com.chiorichan.tasks.TaskManager;
import com.chiorichan.tasks.TaskRegistrar;
import com.chiorichan.tasks.Ticks;
import com.chiorichan.util.FileFunc;
import com.chiorichan.util.Versioning;

public class AppConfig implements Configuration, TaskRegistrar
{
	private static File lockFile;

	static
	{
		try
		{
			lockFile = new File( getApplicationJar() + ".lck" );

			// TODO check that the enclosed lock PID number is currently running
			if ( lockFile.exists() )
			{
				int pid = Integer.parseInt( FileUtils.readFileToString( lockFile ).trim() );

				try
				{
					if ( Versioning.isPIDRunning( pid ) )
						throw new StartupException( "We have detected the server jar is already running. Please terminate process ID " + pid + " or disregard this notice and try again." );
				}
				catch ( IOException e )
				{
					throw new StartupException( "We have detected the server jar is already running. We were unable to verify if the PID " + pid + " is still running." );
				}
			}

			FileUtils.writeStringToFile( lockFile, Versioning.getProcessID() );
			lockFile.deleteOnExit();
		}
		catch ( IOException e )
		{
			throw new StartupException( "We had a problem locking the running server jar", e );
		}

		lockFile.deleteOnExit();
	}

	/**
	 * @return The server jar file
	 */
	public static File getApplicationJar()
	{
		try
		{
			return new File( URLDecoder.decode( AppLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8" ) );
		}
		catch ( UnsupportedEncodingException e )
		{
			e.printStackTrace();
			return null;
		}
	}

	private String clientId;
	private SQLDatastore fwDatabase = null;
	private File loadedLocation = null;
	private YamlConfiguration yaml = new YamlConfiguration();
	private Map<String, File> directories = new HashMap<>();

	protected AppConfig()
	{

	}

	@Override
	public void addDefault( String path, Object value )
	{
		yaml.addDefault( path, value );
	}

	@Override
	public void addDefaults( Configuration defaults )
	{
		yaml.addDefaults( defaults );
	}

	@Override
	public void addDefaults( Map<String, Object> defaults )
	{
		yaml.addDefaults( defaults );
	}

	public void clearCache( File path, long keepHistory )
	{
		Validate.notNull( path );
		Validate.notNull( keepHistory );

		if ( !path.exists() || !path.isDirectory() )
			throw new IllegalArgumentException( "Path must exist and be a directory." );

		File[] files = path.listFiles();
		for ( File f : files )
			if ( f.isFile() && f.lastModified() < System.currentTimeMillis() - keepHistory * 24 * 60 * 60 )
				f.delete();
			else if ( f.isDirectory() )
				clearCache( f, keepHistory );
	}

	public void clearCache( long keepHistory )
	{
		try
		{
			clearCache( getDirectoryCache(), keepHistory );
		}
		catch ( IllegalStateException e )
		{
			// Ignore!
		}
	}

	@Override
	public boolean contains( String path )
	{
		return yaml.contains( path );
	}

	@Override
	public ConfigurationSection createSection( String path )
	{
		return yaml.createSection( path );
	}

	@Override
	public ConfigurationSection createSection( String path, Map<?, ?> map )
	{
		return yaml.createSection( path, map );
	}

	public File file()
	{
		return loadedLocation;
	}

	@Override
	public Object get( String path )
	{
		return yaml.get( path );
	}

	@Override
	public Object get( String path, Object def )
	{
		return yaml.get( path, def );
	}

	@Override
	public <T> List<T> getAsList( String path )
	{
		return yaml.getAsList( path );
	}

	@Override
	public <T> List<T> getAsList( String path, List<T> def )
	{
		return yaml.getAsList( path, def );
	}

	@Override
	public boolean getBoolean( String path )
	{
		return yaml.getBoolean( path );
	}

	@Override
	public boolean getBoolean( String path, boolean def )
	{
		return yaml.getBoolean( path, def );
	}

	@Override
	public List<Boolean> getBooleanList( String path )
	{
		return yaml.getBooleanList( path );
	}

	@Override
	public List<Byte> getByteList( String path )
	{
		return yaml.getByteList( path );
	}

	public String getClientId()
	{
		return clientId;
	}

	@Override
	public Color getColor( String path )
	{
		return yaml.getColor( path );
	}

	@Override
	public Color getColor( String path, Color def )
	{
		return yaml.getColor( path, def );
	}

	@Override
	public ConfigurationSection getConfigurationSection( String path )
	{
		return yaml.getConfigurationSection( path );
	}

	@Override
	public ConfigurationSection getConfigurationSection( String path, boolean create )
	{
		return yaml.getConfigurationSection( path, create );
	}

	@Override
	public String getCurrentPath()
	{
		return yaml.getCurrentPath();
	}

	public SQLDatastore getDatabase()
	{
		return fwDatabase;
	}

	public SQLDatastore getDatabaseWithException()
	{
		if ( fwDatabase == null )
			throw new IllegalStateException( "The Server Database is unconfigured. See config option 'server.database.type' in server config 'server.yaml'." );
		return fwDatabase;
	}

	@Override
	public Configuration getDefaults()
	{
		return yaml.getDefaults();
	}

	@Override
	public ConfigurationSection getDefaultSection()
	{
		return yaml.getDefaultSection();
	}

	public File getDirectory()
	{
		return getApplicationJar().getParentFile();
	}

	public File getDirectory( String configKey, String defPath )
	{
		return getDirectory( configKey, defPath, false, false );
	}

	public File getDirectory( String configKey, String defPath, boolean forceReload )
	{
		return getDirectory( configKey, defPath, forceReload, true );
	}

	public File getDirectory( String configKey, String defPath, boolean forceReload, boolean directoryCheck )
	{
		if ( !directories.containsKey( configKey ) || forceReload )
		{
			String dir;
			if ( AppLoader.options().has( configKey + "-dir" ) )
				dir = ( String ) AppLoader.options().valueOf( configKey + "-dir" );
			else if ( isString( "directories." + configKey ) )
				dir = getString( "directories." + configKey, defPath );
			else
			{
				set( "directories." + configKey, defPath );
				dir = defPath;
			}

			File file = FileFunc.isAbsolute( dir ) ? new File( dir ) : new File( getDirectory(), dir );

			if ( directoryCheck )
				if ( !FileFunc.setDirectoryAccess( file ) )
					throw new UncaughtException( ReportingLevel.E_ERROR, "This application experienced a problem setting read and write access to directory \"" + FileFunc.relPath( file ) + "\"! If the path is incorrect, check config option \"directories." + configKey + "\"." );

			directories.put( configKey, file );
		}

		return directories.get( configKey );
	}

	public File getDirectoryCache()
	{
		return getDirectory( "cache", "cache" );
	}

	public File getDirectoryCache( String subdir )
	{
		File file = new File( getDirectoryCache(), subdir );
		if ( !FileFunc.setDirectoryAccess( file ) )
			throw new UncaughtException( ReportingLevel.E_ERROR, "This application experienced a problem setting read and write access to directory \"" + FileFunc.relPath( file ) + "\"!" );
		return file;
	}

	public File getDirectoryLogs()
	{
		return getDirectory( "logs", "logs", false, true );
	}

	/**
	 * @return The plugins directory
	 */
	public File getDirectoryPlugins()
	{
		return getDirectory( "plugins", "plugins" );
	}

	public File getDirectoryUpdates()
	{
		return getDirectory( "updates", "plugins/updates" );
	}

	@Override
	public double getDouble( String path )
	{
		return yaml.getDouble( path );
	}

	@Override
	public double getDouble( String path, double def )
	{
		return yaml.getDouble( path, def );
	}

	@Override
	public List<Double> getDoubleList( String path )
	{
		return yaml.getDoubleList( path );
	}

	@Override
	public List<Float> getFloatList( String path )
	{
		return yaml.getFloatList( path );
	}

	@Override
	public int getInt( String path )
	{
		return yaml.getInt( path );
	}

	@Override
	public int getInt( String path, int def )
	{
		return yaml.getInt( path, def );
	}

	@Override
	public List<Integer> getIntegerList( String path )
	{
		return yaml.getIntegerList( path );
	}

	@Override
	public Set<String> getKeys()
	{
		return yaml.getKeys();
	}

	@Override
	public Set<String> getKeys( boolean deep )
	{
		return yaml.getKeys( deep );
	}

	@Override
	public <T> List<T> getList( String path )
	{
		return yaml.getList( path );
	}

	@Override
	public <T> List<T> getList( String path, List<T> def )
	{
		return yaml.getList( path, def );
	}

	@Override
	public long getLong( String path )
	{
		return yaml.getLong( path );
	}

	@Override
	public long getLong( String path, long def )
	{
		return yaml.getLong( path, def );
	}

	@Override
	public List<Long> getLongList( String path )
	{
		return yaml.getLongList( path );
	}

	@Override
	public List<Map<?, ?>> getMapList( String path )
	{
		return yaml.getMapList( path );
	}

	@Override
	public String getName()
	{
		return yaml.getName();
	}

	@Override
	public ConfigurationSection getParent()
	{
		return yaml.getParent();
	}

	@Override
	public Configuration getRoot()
	{
		return yaml.getRoot();
	}

	@Override
	public List<Short> getShortList( String path )
	{
		return yaml.getShortList( path );
	}

	@Override
	public String getString( String path )
	{
		return yaml.getString( path );
	}

	@Override
	public String getString( String path, String def )
	{
		return yaml.getString( path, def );
	}

	@Override
	public List<String> getStringList( String path )
	{
		return yaml.getStringList( path );
	}

	@Override
	public List<String> getStringList( String path, List<String> def )
	{
		return yaml.getStringList( path, def );
	}

	@Override
	public Map<String, Object> getValues( boolean deep )
	{
		return yaml.getValues( deep );
	}

	@Override
	public boolean has( String path )
	{
		return yaml.has( path );
	}

	public void initDatabase()
	{
		switch ( getString( "server.database.type", "sqlite" ).toLowerCase() )
		{
			case "sqlite":
			{
				fwDatabase = new SQLiteDatastore( getString( "server.database.dbfile", "server.db" ) );
				break;
			}
			case "mysql":
			{
				String host = getString( "server.database.host", "localhost" );
				String port = getString( "server.database.port", "3306" );
				String database = getString( "server.database.database", "chiorifw" );
				String username = getString( "server.database.username", "fwuser" );
				String password = getString( "server.database.password", "fwpass" );

				fwDatabase = new MySQLDatastore( database, username, password, host, port );
				break;
			}
			case "h2":
			{
				fwDatabase = new H2SQLDatastore( getString( "server.database.dbfile", "server.db" ) );
				break;
			}
			case "none":
			case "":
				Log.get( DatastoreManager.instance() ).warning( "The Server Database is unconfigured, some features maybe not function as expected. See config option 'server.database.type' in server config 'server.yaml'." );
				break;
			default:
				Log.get( DatastoreManager.instance() ).severe( "We are sorry, the Database Engine currently only supports mysql and sqlite but we found '" + getString( "server.database.type", "sqlite" ).toLowerCase() + "', please change 'server.database.type' to 'mysql' or 'sqlite' in server config 'server.yaml'" );
		}
	}

	@Override
	public boolean isBoolean( String path )
	{
		return yaml.isBoolean( path );
	}

	@Override
	public boolean isColor( String path )
	{
		return yaml.isColor( path );
	}

	public boolean isConfigLoaded()
	{
		return loadedLocation != null;
	}

	@Override
	public boolean isConfigurationSection( String path )
	{
		return yaml.isConfigurationSection( path );
	}

	@Override
	public boolean isDouble( String path )
	{
		return yaml.isDouble( path );
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	@Override
	public boolean isInt( String path )
	{
		return yaml.isInt( path );
	}

	@Override
	public boolean isList( String path )
	{
		return yaml.isList( path );
	}

	@Override
	public boolean isLong( String path )
	{
		return yaml.isLong( path );
	}

	@Override
	public boolean isSet( String path )
	{
		return yaml.isSet( path );
	}

	@Override
	public boolean isString( String path )
	{
		return yaml.isString( path );
	}

	/**
	 * Loads a new config file into the AppConfig
	 *
	 * @param location
	 *             The config file location
	 * @param resourcePath
	 *             The packaged jar file resource path
	 */
	protected void loadConfig( File location, String resourcePath )
	{
		if ( location == null )
			throw new StartupException( "The configuration file location is null, did you define the --config argument?" );

		yaml = YamlConfiguration.loadConfiguration( location );
		yaml.options().copyDefaults( true );
		yaml.setDefaults( YamlConfiguration.loadConfiguration( getClass().getClassLoader().getResourceAsStream( resourcePath ) ) );

		loadedLocation = location;
		directories.clear();
	}

	@Override
	public ConfigurationOptions options()
	{
		return yaml.options();
	}

	public void reload()
	{
		try
		{
			yaml.load( file() );
		}
		catch ( IOException | InvalidConfigurationException e )
		{
			Log.get().severe( e.getMessage() );
		}
	}

	public void save()
	{
		// TODO Targeted key path saves
		// TODO Save only changed values, so manual edits are not overridden

		TaskManager.instance().runTaskWithTimeout( this, Ticks.MINUTE, new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					if ( yaml != null )
						yaml.save( file() );
				}
				catch ( IOException ex )
				{
					Log.get().severe( "Could not save " + file(), ex );
				}
			}
		} );
	}

	@Override
	public void set( String path, Object value )
	{
		yaml.set( path, value );
	}

	@Override
	public void setDefaults( Configuration defaults )
	{
		yaml.setDefaults( defaults );
	}

	/**
	 * Should the server print a warning in console when the ticks are less then 20.
	 *
	 * @return boolean
	 */
	public boolean warnOnOverload()
	{
		return yaml.getBoolean( "settings.warn-on-overload" );
	}

	public YamlConfiguration yaml()
	{
		return yaml;
	}
}

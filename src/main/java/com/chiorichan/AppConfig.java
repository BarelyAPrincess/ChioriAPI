/**
 * The MIT License
 * <p>
 * Copyright (c) 2004-2015 Paul R. Holser, Jr.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * <p>
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com> All Right Reserved.
 */
package com.chiorichan;

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
import com.chiorichan.util.Application;
import com.chiorichan.util.FileFunc;
import com.chiorichan.util.ObjectFunc;
import com.chiorichan.util.Validation;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppConfig implements Configuration, TaskRegistrar
{
	private static final AppConfig instance = new AppConfig();
	protected static File lockFile;
	protected static File appDirectory = null;

	static
	{
		try
		{
			lockFile = AppLoader.getLockFile();

			// TODO check that the enclosed lock PID number is currently running
			if ( lockFile.exists() )
			{
				String pidraw = FileFunc.readFileToString( lockFile );

				if ( pidraw != null && pidraw.length() > 0 )
				{
					int pid = Integer.parseInt( pidraw );

					try
					{
						if ( pid != Integer.parseInt( Application.getProcessID() ) && Application.isPIDRunning( pid ) )
							throw new StartupException( "We have detected the server jar is already running. Please terminate PID " + pid + " or disregard this notice and try again." );
					}
					catch ( IOException e )
					{
						throw new StartupException( "We have detected the server jar is already running. We were unable to verify if the PID " + pid + " is still running." );
					}
				}
			}

			FileFunc.writeStringToFile( lockFile, Application.getProcessID() );
			lockFile.deleteOnExit();
		}
		catch ( IOException e )
		{
			throw new StartupException( "We had a problem locking the application jar", e );
		}

		lockFile.deleteOnExit();
	}

	public static AppConfig get()
	{
		if ( ObjectFunc.noLoopDetected( AppConfig.class, "loadConfig" ) && !instance.isConfigLoaded() )
			instance.loadConfig();
		return instance;
	}

	/**
	 * @return The server jar file
	 */
	public static File getApplicationJar()
	{
		try
		{
			File file = new File( URLDecoder.decode( AppLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8" ) );
			if ( file.isDirectory() || !file.getAbsolutePath().endsWith( ".jar" ) )
				return null;
			return file;
		}
		catch ( UnsupportedEncodingException e )
		{
			e.printStackTrace();
			return null;
		}
	}

	private String clientId;
	private SQLDatastore fwDatabase = null;
	protected File configFile = null;
	private YamlConfiguration yaml = null;
	private Map<String, File> directories = new HashMap<>();

	protected AppConfig()
	{

	}

	@Override
	public void addDefault( String path, Object value )
	{
		yamlCheck();
		yaml.addDefault( path, value );
	}

	@Override
	public void addDefaults( Configuration defaults )
	{
		yamlCheck();
		yaml.addDefaults( defaults );
	}

	@Override
	public void addDefaults( Map<String, Object> defaults )
	{
		yamlCheck();
		yaml.addDefaults( defaults );
	}

	public void clearCache( File path, long keepHistory )
	{
		Validation.notNull( path );
		Validation.notNull( keepHistory );

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
		yamlCheck();
		return yaml.contains( path );
	}

	@Override
	public ConfigurationSection createSection( String path )
	{
		yamlCheck();
		return yaml.createSection( path );
	}

	@Override
	public ConfigurationSection createSection( String path, Map<?, ?> map )
	{
		yamlCheck();
		return yaml.createSection( path, map );
	}

	public File file()
	{
		if ( configFile == null )
			configFile = new File( instance.getDirectory().getAbsolutePath(), "config.yaml" );
		return configFile;
	}

	@Override
	public Object get( String path )
	{
		yamlCheck();
		return yaml.get( path );
	}

	@Override
	public Object get( String path, Object def )
	{
		yamlCheck();
		return yaml.get( path, def );
	}

	@Override
	public <T> List<T> getAsList( String path )
	{
		yamlCheck();
		return yaml.getAsList( path );
	}

	@Override
	public <T> List<T> getAsList( String path, List<T> def )
	{
		yamlCheck();
		return yaml.getAsList( path, def );
	}

	@Override
	public boolean getBoolean( String path )
	{
		yamlCheck();
		return yaml.getBoolean( path );
	}

	@Override
	public boolean getBoolean( String path, boolean def )
	{
		yamlCheck();
		return yaml.getBoolean( path, def );
	}

	@Override
	public List<Boolean> getBooleanList( String path )
	{
		yamlCheck();
		return yaml.getBooleanList( path );
	}

	@Override
	public List<Byte> getByteList( String path )
	{
		yamlCheck();
		return yaml.getByteList( path );
	}

	public String getClientId()
	{
		return clientId;
	}

	@Override
	public Color getColor( String path )
	{
		yamlCheck();
		return yaml.getColor( path );
	}

	@Override
	public Color getColor( String path, Color def )
	{
		yamlCheck();
		return yaml.getColor( path, def );
	}

	@Override
	public ConfigurationSection getConfigurationSection( String path )
	{
		yamlCheck();
		return yaml.getConfigurationSection( path );
	}

	@Override
	public ConfigurationSection getConfigurationSection( String path, boolean create )
	{
		yamlCheck();
		return yaml.getConfigurationSection( path, create );
	}

	@Override
	public String getCurrentPath()
	{
		yamlCheck();
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
		yamlCheck();
		return yaml.getDefaults();
	}

	@Override
	public ConfigurationSection getDefaultSection()
	{
		yamlCheck();
		return yaml.getDefaultSection();
	}

	public File getDirectory()
	{
		if ( appDirectory != null ) // Was application directory set
			return appDirectory;
		if ( getApplicationJar() != null ) // Are we running from java jar file
			return getApplicationJar().getParentFile();
		return new File( "" ); // Return current working directory
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
			try
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

				File file = FileFunc.isAbsolute( dir ) ? new File( dir ) : new File( getDirectory().getAbsolutePath(), dir );

				if ( directoryCheck )
					if ( !FileFunc.setDirectoryAccess( file ) )
						throw new UncaughtException( ReportingLevel.E_ERROR, "This application experienced a problem setting read and write access to directory \"" + FileFunc.relPath( file ) + "\"! If the path is incorrect, check config option \"directories." + configKey + "\"." );

				directories.put( configKey, file );
			}
			catch ( NoClassDefFoundError e )
			{
				File file = FileFunc.isAbsolute( defPath ) ? new File( defPath ) : new File( getDirectory(), defPath );

				if ( directoryCheck )
					if ( !FileFunc.setDirectoryAccess( file ) )
						throw new UncaughtException( ReportingLevel.E_ERROR, "This application experienced a problem setting read and write access to directory \"" + FileFunc.relPath( file ) + "\"! If the path is incorrect, check config option \"directories." + configKey + "\"." );

				return file;

				// XXX Don't save so we can try again later
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
		yamlCheck();
		return yaml.getDouble( path );
	}

	@Override
	public double getDouble( String path, double def )
	{
		yamlCheck();
		return yaml.getDouble( path, def );
	}

	@Override
	public List<Double> getDoubleList( String path )
	{
		yamlCheck();
		return yaml.getDoubleList( path );
	}

	@Override
	public List<Float> getFloatList( String path )
	{
		yamlCheck();
		return yaml.getFloatList( path );
	}

	@Override
	public int getInt( String path )
	{
		yamlCheck();
		return yaml.getInt( path );
	}

	@Override
	public int getInt( String path, int def )
	{
		yamlCheck();
		return yaml.getInt( path, def );
	}

	@Override
	public List<Integer> getIntegerList( String path )
	{
		yamlCheck();
		return yaml.getIntegerList( path );
	}

	@Override
	public Set<String> getKeys()
	{
		yamlCheck();
		return yaml.getKeys();
	}

	@Override
	public Set<String> getKeys( boolean deep )
	{
		yamlCheck();
		return yaml.getKeys( deep );
	}

	@Override
	public <T> List<T> getList( String path )
	{
		yamlCheck();
		return yaml.getList( path );
	}

	@Override
	public <T> List<T> getList( String path, List<T> def )
	{
		yamlCheck();
		return yaml.getList( path, def );
	}

	@Override
	public long getLong( String path )
	{
		yamlCheck();
		return yaml.getLong( path );
	}

	@Override
	public long getLong( String path, long def )
	{
		yamlCheck();
		return yaml.getLong( path, def );
	}

	@Override
	public List<Long> getLongList( String path )
	{
		yamlCheck();
		return yaml.getLongList( path );
	}

	@Override
	public List<Map<?, ?>> getMapList( String path )
	{
		yamlCheck();
		return yaml.getMapList( path );
	}

	@Override
	public String getName()
	{
		yamlCheck();
		return yaml.getName();
	}

	@Override
	public ConfigurationSection getParent()
	{
		yamlCheck();
		return yaml.getParent();
	}

	@Override
	public Configuration getRoot()
	{
		yamlCheck();
		return yaml.getRoot();
	}

	@Override
	public List<Short> getShortList( String path )
	{
		yamlCheck();
		return yaml.getShortList( path );
	}

	@Override
	public String getString( String path )
	{
		yamlCheck();
		return yaml.getString( path );
	}

	@Override
	public String getString( String path, String def )
	{
		yamlCheck();
		return yaml.getString( path, def );
	}

	@Override
	public List<String> getStringList( String path )
	{
		yamlCheck();
		return yaml.getStringList( path );
	}

	@Override
	public List<String> getStringList( String path, List<String> def )
	{
		yamlCheck();
		return yaml.getStringList( path, def );
	}

	@Override
	public Map<String, Object> getValues( boolean deep )
	{
		yamlCheck();
		return yaml.getValues( deep );
	}

	@Override
	public boolean has( String path )
	{
		yamlCheck();
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
		yamlCheck();
		return yaml.isBoolean( path );
	}

	@Override
	public boolean isColor( String path )
	{
		yamlCheck();
		return yaml.isColor( path );
	}

	public boolean isConfigLoaded()
	{
		return yaml != null;
	}

	@Override
	public boolean isConfigurationSection( String path )
	{
		yamlCheck();
		return yaml.isConfigurationSection( path );
	}

	@Override
	public boolean isDouble( String path )
	{
		yamlCheck();
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
		yamlCheck();
		return yaml.isInt( path );
	}

	@Override
	public boolean isList( String path )
	{
		yamlCheck();
		return yaml.isList( path );
	}

	@Override
	public boolean isLong( String path )
	{
		yamlCheck();
		return yaml.isLong( path );
	}

	@Override
	public boolean isSet( String path )
	{
		yamlCheck();
		return yaml.isSet( path );
	}

	@Override
	public boolean isString( String path )
	{
		yamlCheck();
		return yaml.isString( path );
	}

	/**
	 * Loads a the config file into AppConfig
	 */
	protected void loadConfig()
	{
		try
		{
			file();

			yaml = YamlConfiguration.loadConfiguration( configFile );
			yaml.options().copyDefaults( true );
			yaml.setDefaults( YamlConfiguration.loadConfiguration( getClass().getClassLoader().getResourceAsStream( "com/chiorichan/config.yaml" ) ) );
			directories.clear();

			Log.get().info( String.format( "Loaded application configuration from %s", FileFunc.relPath( configFile ) ) );
		}
		catch ( NoClassDefFoundError e )
		{
			Log.get().severe( "Failed to load config: " + e.getMessage() );
		}
	}

	@Override
	public ConfigurationOptions options()
	{
		return yaml == null ? null : yaml.options();
	}

	public void reload()
	{
		if ( yaml == null )
			return;

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
		if ( yaml == null )
			return;

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
		yamlCheck();
		yaml.set( path, value );
	}

	@Override
	public void setDefaults( Configuration defaults )
	{
		yamlCheck();
		yaml.setDefaults( defaults );
	}

	/**
	 * Should the server print a warning in console when the ticks are less then 20.
	 *
	 * @return boolean
	 */
	public boolean warnOnOverload()
	{
		yamlCheck();
		return yaml.getBoolean( "settings.warn-on-overload" );
	}

	public YamlConfiguration yaml()
	{
		return yaml;
	}

	public void yamlCheck()
	{
		if ( yaml == null )
			throw new IllegalStateException( "The YAML configuration is not loaded." );
	}
}

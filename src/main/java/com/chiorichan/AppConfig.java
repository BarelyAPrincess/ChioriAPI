package com.chiorichan;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.chiorichan.logger.Log;
import com.chiorichan.tasks.TaskManager;
import com.chiorichan.tasks.Ticks;
import com.chiorichan.util.FileFunc;

public class AppConfig implements Configuration
{
	private static String clientId;
	private static File cacheDirectory;
	private static File tmpDirectory;
	private static File logDirectory;
	private static File lockFile;

	private SQLDatastore fwDatabase = null;

	private File loadedLocation = null;

	private YamlConfiguration yaml = new YamlConfiguration();

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
		clearCache( getTempFileDirectory(), keepHistory );
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

	protected File file()
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

	/**
	 * @return The server jar file
	 */
	public File getApplicationJar()
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

	public File getCacheDirectory()
	{
		if ( cacheDirectory == null )
			cacheDirectory = getDirectory( "cache", "temp" );
		return cacheDirectory;
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

	public File getDirectory( String configKey, String defPath )
	{
		String dir = AppLoader.options().has( configKey ) ? ( String ) AppLoader.options().valueOf( "plugins" ) : AppController.config().getString( "directories." + configKey, "plugins" );
		return FileFunc.isAbsolute( dir ) ? new File( dir ) : new File( getRootDirectory(), dir );
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

	public File getLogsFileDirectory()
	{
		if ( logDirectory == null )
			throw new IllegalStateException( "Logs directory appears to be null, was getLogsFileDirectory() called before the server finished inialization?" );

		return logDirectory;
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

	public File getRootDirectory()
	{
		return getApplicationJar().getParentFile();
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

	public File getTempFileDirectory()
	{
		if ( tmpDirectory == null )
			throw new IllegalStateException( "Temp directory appears to be null, was getTempFileDirectory() called before the server finished inialization?" );

		FileFunc.patchDirectory( tmpDirectory );

		return tmpDirectory;
	}

	public File getTempFileDirectory( String subdir )
	{
		File file = new File( getTempFileDirectory(), subdir );

		FileFunc.patchDirectory( file );

		return file;
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
		yaml = YamlConfiguration.loadConfiguration( location );
		loadedLocation = location;
	}

	public File logsDirectory()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurationOptions options()
	{
		return yaml.options();
	}

	public void reloadConfig()
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

	public void saveConfig()
	{
		// TODO Targeted key path saves
		// TODO Save only changed values, so manual edits are not overridden

		TaskManager.instance().runTaskWithTimeout( AppController.instance, Ticks.MINUTE, new Runnable()
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

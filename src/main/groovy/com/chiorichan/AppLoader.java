/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package com.chiorichan;

import com.chiorichan.account.AccountManager;
import com.chiorichan.configuration.ConfigurationSection;
import com.chiorichan.datastore.DatastoreManager;
import com.chiorichan.event.EventBus;
import com.chiorichan.event.Listener;
import com.chiorichan.event.application.RunlevelEvent;
import com.chiorichan.lang.ApplicationException;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.lang.ReportingLevel;
import com.chiorichan.lang.RunLevel;
import com.chiorichan.lang.StartupAbortException;
import com.chiorichan.lang.StartupException;
import com.chiorichan.lang.UncaughtException;
import com.chiorichan.logger.DefaultLogFormatter;
import com.chiorichan.logger.Log;
import com.chiorichan.permission.PermissionManager;
import com.chiorichan.permission.lang.PermissionBackendException;
import com.chiorichan.plugin.PluginManager;
import com.chiorichan.services.AppManager;
import com.chiorichan.tasks.TaskManager;
import com.chiorichan.tasks.TaskRegistrar;
import com.chiorichan.tasks.Worker;
import com.chiorichan.utils.UtilIO;
import com.chiorichan.utils.UtilObjects;
import com.chiorichan.utils.UtilSystem;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Provides a base AppLoader skeleton for you to extend or call directly using {@code AppLoader.init( Class<? extends AppAppController> loaderClass, String... args );}.
 */
public abstract class AppLoader implements Listener
{
	static
	{
		System.setProperty( "file.encoding", "utf-8" );
	}

	static Watchdog watchdog = null;
	private static OptionSet options;
	private static boolean isRunning;
	public static long startTime = System.currentTimeMillis();
	private static List<AppLoader> instances = new ArrayList<>();
	private AppController controller;

	public static List<AppLoader> instances()
	{
		if ( instances.size() == 0 )
			throw new StartupException( "There are no initialized application instances!" );
		return Collections.unmodifiableList( instances );
	}

	public static OptionSet options()
	{
		return options;
	}

	public static boolean isWatchdogRunning()
	{
		return watchdog != null;
	}

	private final RunlevelEvent runlevel = new RunlevelEvent();

	public AppLoader()
	{
		controller = new AppController( this );
	}

	public RunLevel runLevel()
	{
		return runlevel.getRunLevel();
	}

	public AppController controller()
	{
		return controller;
	}

	public static boolean isRunning()
	{
		return isRunning;
	}

	protected void runLevel( RunLevel level ) throws ApplicationException
	{
		runlevel.setRunLevel( level );
		onRunlevelChange( level );
	}

	public static void main( String... args ) throws Exception
	{
		parseArguments( args );
		init( SimpleLoader.class );
	}

	protected void start() throws ApplicationException
	{
		try
		{
			ConfigurationSection logs = AppConfig.get().getConfigurationSection( "logs.loggers", true );

			if ( logs != null )
				for ( String key : logs.getKeys( false ) )
				{
					ConfigurationSection logger = logs.getConfigurationSection( key );

					switch ( logger.getString( "type", "file" ) )
					{
						case "file":
							if ( logger.getBoolean( "enabled", true ) )
								Log.addFileHandler( key, logger.getBoolean( "color", false ), logger.getInt( "archiveLimit", 3 ), Level.parse( logger.getString( "level", "INFO" ).toUpperCase() ) );
							break;
						default:
							Log.get().warning( "We had no logger for type '" + logger.getString( "type" ) + "'" );
					}
				}

			ReportingLevel.enableErrorLevelOnly( ReportingLevel.parse( AppConfig.get().getString( "server.errorReporting", "E_ALL ~E_NOTICE ~E_STRICT ~E_DEPRECATED" ) ) );

			// AppManager.manager( TaskManager.class ).init();
			EventBus.instance().registerEvents( this, this );

			runLevel( RunLevel.INITIALIZED );

			AppController.primaryThread.start();
			AppManager.manager( PluginManager.class ).init();

			runLevel( RunLevel.INITIALIZATION );

			PluginManager.instance().loadPlugins();

			runLevel( RunLevel.STARTUP );

			Log.get().info( "Initializing the Datastore Subsystem..." );
			AppManager.manager( DatastoreManager.class ).init();

			Log.get().info( "Initializing the Database Subsystem..." );
			AppConfig.get().initDatabase();

			Log.get().info( "Initializing the Permission Manager..." );
			AppManager.manager( PermissionManager.class ).init();

			Log.get().info( "Initializing the Account Manager..." );
			AppManager.manager( AccountManager.class ).init();

			runLevel( RunLevel.POSTSTARTUP );
			runLevel( RunLevel.RUNNING );

			// XXX There seems to be a problem registering sync'd tasks before this point
		}
		catch ( ApplicationException | UncaughtException e )
		{
			throw e;
		}
		catch ( Throwable e )
		{
			throw new StartupException( "There was a problem initializing one or more of the application managers", e );
		}
	}

	protected static boolean parseArguments( String... args )
	{
		return parseArguments( null, args );
	}

	/**
	 * Parses the provided raw arguments array.
	 *
	 * @param cls  Class that provides the static populateOptionParser() method, i.e., AppLoader subclass. Set null to ignore.
	 * @param args The string array to be parsed
	 * @return True if and only if, loading can continue normally. Options such as --help will cause this method to return false.
	 */
	protected static boolean parseArguments( Class<?> cls, String... args )
	{
		if ( args == null )
			args = new String[0];

		OptionParser parser = new OptionParser()
		{
			{
				acceptsAll( Arrays.asList( "?", "h", "help" ), "Show the help" );
				accepts( "config", "File for chiori settings" ).withRequiredArg().ofType( File.class ).defaultsTo( new File( "config.yaml" ) ).describedAs( "Yaml file" );
				accepts( "plugins-dir", "Specify plugin directory" ).withRequiredArg().ofType( String.class );
				accepts( "updates-dir", "Specify updates directory" ).withRequiredArg().ofType( String.class );
				accepts( "cache-dir", "Specify cache directory" ).withRequiredArg().ofType( String.class );
				accepts( "logs-dir", "Specify logs directory" ).withRequiredArg().ofType( String.class );
				accepts( "app-dir", "Specify application directory" ).withRequiredArg().ofType( String.class );
				accepts( "query-disable", "Disable the internal TCP Server" );
				acceptsAll( Arrays.asList( "d", "date-format" ), "Format of the date to display in the console (for log entries)" ).withRequiredArg().ofType( SimpleDateFormat.class ).describedAs( "Log date format" );
				accepts( "nocolor", "Disables the console color formatting" );
				acceptsAll( Arrays.asList( "v", "version" ), "Show the Version" );
				accepts( "pid", "Specifies the application PID file. This file is created and removed by the server, be sure the local running user has access." ).withRequiredArg().ofType( File.class ).describedAs( "PID File" ).defaultsTo( new File( "/var/run/chiori/chiori.pid" ) );
				accepts( "child", "Watchdog Child Mode. DO NOT USE!" );
				accepts( "watchdog", "Launch the server with Watchdog protection, allows the server to restart itself. WARNING: May be buggy!" ).requiredIf( "child" ).withOptionalArg().ofType( String.class ).describedAs( "Child JVM launch arguments" ).defaultsTo( "" );

				accepts( "status", "Check daemon status" );
				accepts( "stop", "Stop daemon status" );
			}
		};

		if ( cls != null )
			try
			{
				Method m = cls.getMethod( "populateOptionParser", OptionParser.class );
				m.invoke( null, parser );
			}
			catch ( NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e )
			{
				if ( Versioning.isDevelopment() )
					Log.get( AppController.class.getName() ).dev( "The class \"" + cls.getName() + "\" has no static populateOptionParser() method!" );
			}

		try
		{
			options = parser.parse( args );
		}
		catch ( OptionException ex )
		{
			Log.get().severe( "Failed to parse arguments: " + ex.getLocalizedMessage() );
		}

		// Check if application is already running and stop if specified
		if ( options.has( "status" ) || options.has( "stop" ) )
			try
			{
				File lockFile = AppConfig.getLockFile();
				// File lockFile = getLockFile();

				// TODO check that the enclosed lock PID number is currently running
				if ( lockFile.exists() )
				{
					String pidraw = UtilIO.readFileToString( lockFile );

					if ( pidraw != null && pidraw.length() > 0 )
					{
						int pid = Integer.parseInt( pidraw );
						if ( UtilSystem.isPIDRunning( pid ) )
						{
							if ( options.has( "stop" ) )
							{
								if ( UtilSystem.terminatePID( pid ) )
									Log.get().info( EnumColor.GREEN + Versioning.getProduct() + " " + Versioning.getVersion() + " has been terminated! (PID " + pid + ")" );
								else
									Log.get().info( EnumColor.RED + "Failed to terminate PID " + pid + "!" );
								return false;
							}

							Log.get().info( EnumColor.GREEN + Versioning.getProduct() + " " + Versioning.getVersion() + " is running with PID " + pid + "!" );
							return false;
						}
					}
				}

				Log.get().info( EnumColor.RED + Versioning.getProduct() + " " + Versioning.getVersion() + " is not running!" );
				return false;
			}
			catch ( IOException e )
			{
				throw new StartupException( "We had a problem reading the application PID file", e );
			}

		if ( options.has( "config" ) )
			AppConfig.get().configFile = new File( ( String ) options.valueOf( "config" ) );

		Log.setConsoleFormatter( new DefaultLogFormatter( !options.has( "nocolor" ) ) );

		if ( options == null || options.has( "?" ) )
		{
			try
			{
				parser.printHelpOn( System.out );
			}
			catch ( Throwable ex )
			{
				Log.get().severe( ex );
			}
			return false;
		}
		if ( options.has( "v" ) )
		{
			Log.get().info( "Running " + Versioning.getProduct() + " version " + Versioning.getVersion() );
			return false;
		}

		if ( options.has( "app-dir" ) )
		{
			AppConfig.appDirectory = new File( ( String ) options.valueOf( "app-dir" ) );
			Log.get().info( "Using application directory " + AppConfig.appDirectory.getAbsolutePath() );
			if ( !AppConfig.appDirectory.exists() || !AppConfig.appDirectory.isDirectory() )
				throw new StartupException( "Application directory '" + AppConfig.appDirectory.getAbsolutePath() + "' does not exist or is not a directory!" );
		}

		return true;
	}

	protected static void init( Class<? extends AppLoader> loaderClass )
	{
		try
		{
			AppLoader instance = null;

			if ( loaderClass == null )
				loaderClass = SimpleLoader.class;

			try
			{
				if ( options.has( "watchdog" ) )
				{
					watchdog = new Watchdog();

					if ( options.has( "child" ) )
					{
						isRunning = true;
						watchdog.initChild();
					}
					else
						watchdog.initDaemon( ( String ) options.valueOf( "watchdog" ), options );
				}
				else
					isRunning = true;

				if ( isRunning )
				{
					AppManager.manager( TaskManager.class ).init();
					AppManager.manager( EventBus.class ).init();

					instance = UtilObjects.initClass( loaderClass );
					instances.add( instance );

					instance.start();
				}
			}
			catch ( StartupAbortException e )
			{
				if ( instance != null )
					instance.runLevel( RunLevel.SHUTDOWN );
			}
			catch ( Throwable t )
			{
				if ( instance != null )
					instance.runLevel( RunLevel.CRASHED );
				AppController.handleExceptions( false, t );
				AppController.stopApplication( "CRASHED" );
			}

			if ( isRunning && Log.get() != null )
				Log.get().info( EnumColor.GOLD + "" + EnumColor.NEGATIVE + "Finished Initializing " + Versioning.getProduct() + "! It took " + ( System.currentTimeMillis() - startTime ) + "ms!" );
			else if ( instance != null )
				instance.runLevel( RunLevel.DISPOSED );
		}
		catch ( UncaughtException | ApplicationException e )
		{
			AppController.handleExceptions( false, e );
			AppController.stopApplication( "CRASHED" );
		}
	}

	public RunLevel getLastRunLevel()
	{
		return runlevel.getLastRunLevel();
	}

	public RunLevel getRunLevel()
	{
		return runlevel.getRunLevel();
	}

	protected void reload0() throws ApplicationException
	{
		ReportingLevel.enableErrorLevelOnly( ReportingLevel.parse( AppConfig.get().getString( "server.errorReporting", "E_ALL ~E_NOTICE ~E_STRICT ~E_DEPRECATED" ) ) );

		PluginManager.instance().clearPlugins();
		// ModuleBus.getCommandMap().clearCommands();

		int pollCount = 0;

		// Wait for at most 2.5 seconds for plugins to close their threads
		while ( pollCount < 50 && TaskManager.instance().getActiveWorkers().size() > 0 )
		{
			try
			{
				Thread.sleep( 50 );
			}
			catch ( InterruptedException e )
			{
				// Ignore
			}
			pollCount++;
		}

		List<Worker> overdueWorkers = TaskManager.instance().getActiveWorkers();
		for ( Worker worker : overdueWorkers )
		{
			TaskRegistrar creator = worker.getOwner();
			String author = "<AuthorUnknown>";
			// if ( creator.getDescription().getAuthors().size() > 0 )
			// author = plugin.getDescription().getAuthors().get( 0 );
			Log.get().log( Level.SEVERE, String.format( "Nag author: '%s' of '%s' about the following: %s", author, creator.getName(), "This plugin is not properly shutting down its async tasks when it is being reloaded.  This may cause conflicts with the newly loaded version of the plugin" ) );
		}

		PluginManager.instance().loadPlugins();

		runLevel( RunLevel.RELOAD );

		try
		{
			PermissionManager.instance().reload();
		}
		catch ( PermissionBackendException e )
		{
			e.printStackTrace();
		}

		Log.get().info( "Reinitializing the Accounts Manager..." );
		AccountManager.instance().reload();

		runLevel( RunLevel.RUNNING );
	}

	public abstract void onRunlevelChange( RunLevel level ) throws ApplicationException;
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.chiorichan.account.AccountManager;
import com.chiorichan.configuration.ConfigurationSection;
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
import com.chiorichan.logger.LoggerOutputStream;
import com.chiorichan.permission.PermissionManager;
import com.chiorichan.permission.lang.PermissionBackendException;
import com.chiorichan.plugin.PluginManager;
import com.chiorichan.services.AppManager;
import com.chiorichan.tasks.TaskManager;
import com.chiorichan.tasks.TaskRegistrar;
import com.chiorichan.tasks.Worker;
import com.chiorichan.util.ObjectFunc;
import com.chiorichan.util.Versioning;

/**
 * Provides a base AppController skeleton for you to extend or call directly using {@code AppAppController.init( Class<? extends AppAppController> loaderClass, String... args );}.
 */
public abstract class AppLoader implements Listener
{
	static Watchdog watchdog = null;
	private static OptionSet options;
	private static boolean isRunning;
	public static long startTime = System.currentTimeMillis();
	private static List<AppLoader> instances = new ArrayList<>();
	private AppController controller;

	public static List<AppLoader> instances()
	{
		if ( instances.size() == 0 )
			throw new StartupException( "There are no initalized application instances!" );
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
		init( SimpleLoader.class, args );
	}

	protected void start() throws ApplicationException
	{
		try
		{
			ConfigurationSection logs = AppController.config().getConfigurationSection( "logs.loggers", true );

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

			ReportingLevel.enableErrorLevelOnly( ReportingLevel.parse( AppController.config().getString( "server.errorReporting", "E_ALL ~E_NOTICE ~E_STRICT ~E_DEPRECATED" ) ) );

			// AppManager.manager( TaskManager.class ).init();
			EventBus.instance().registerEvents( this, this );

			runLevel( RunLevel.INITIALIZED );

			AppController.primaryThread.start();
			AppManager.manager( PluginManager.class ).init();

			runLevel( RunLevel.INITIALIZATION );

			PluginManager.instance().loadPlugins();

			runLevel( RunLevel.STARTUP );
			runLevel( RunLevel.POSTSTARTUP );
			runLevel( RunLevel.RUNNING );

			// XXX There seems to be a problem registering sync'd tasks before this point
		}
		catch ( ApplicationException | StartupException e )
		{
			throw e;
		}
		catch ( Throwable e )
		{
			throw new StartupException( "There was a problem initializing one or more of the managers", e );
		}
	}

	public static String uptime()
	{
		Duration duration = new Duration( System.currentTimeMillis() - startTime );
		PeriodFormatter formatter = new PeriodFormatterBuilder().appendDays().appendSuffix( " Day(s) " ).appendHours().appendSuffix( " Hour(s) " ).appendMinutes().appendSuffix( " Minute(s) " ).appendSeconds().appendSuffix( " Second(s)" ).toFormatter();
		return formatter.print( duration.toPeriod() );
	}

	protected static void init( Class<? extends AppLoader> loaderClass, String... args )
	{
		try
		{
			System.setProperty( "file.encoding", "utf-8" );

			AppLoader instance = null;
			options = null;

			if ( loaderClass == null )
				loaderClass = SimpleLoader.class;

			try
			{
				OptionParser parser = new OptionParser()
				{
					{
						// TODO This needs refinement and an API
						acceptsAll( Arrays.asList( "?", "h", "help" ), "Show the help" );
						acceptsAll( Arrays.asList( "config" ), "File for chiori settings" ).withRequiredArg().ofType( File.class ).defaultsTo( new File( "server.yaml" ) ).describedAs( "Yml file" );
						acceptsAll( Arrays.asList( "plugins-dir" ), "Specify plugin directory" ).withRequiredArg().ofType( String.class );
						acceptsAll( Arrays.asList( "updates-dir" ), "Specify updates directory" ).withRequiredArg().ofType( String.class );
						acceptsAll( Arrays.asList( "cache-dir" ), "Specify cache directory" ).withRequiredArg().ofType( String.class );
						acceptsAll( Arrays.asList( "logs-dir" ), "Specify logs directory" ).withRequiredArg().ofType( String.class );
						acceptsAll( Arrays.asList( "query-disable" ), "Disable the internal TCP Server" );
						acceptsAll( Arrays.asList( "d", "date-format" ), "Format of the date to display in the console (for log entries)" ).withRequiredArg().ofType( SimpleDateFormat.class ).describedAs( "Log date format" );
						acceptsAll( Arrays.asList( "nocolor" ), "Disables the console color formatting" );
						acceptsAll( Arrays.asList( "v", "version" ), "Show the Version" );
						acceptsAll( Arrays.asList( "child" ), "Watchdog Child Mode. DO NOT USE!" );
						acceptsAll( Arrays.asList( "watchdog" ), "Launch the server with Watchdog protection, allows the server to restart itself. WARNING: May be buggy!" ).requiredIf( "child" ).withOptionalArg().ofType( String.class ).describedAs( "Child JVM launch arguments" ).defaultsTo( "" );
					}
				};

				try
				{
					Method m = loaderClass.getMethod( "populateOptionParser", OptionParser.class );
					m.invoke( null, parser );
				}
				catch ( NoSuchMethodException e )
				{
					if ( Versioning.isDevelopment() )
						Log.get( AppController.class.getName() ).dev( "The class \"" + loaderClass.getName() + "\" has no populateOptionParser() method!" );
				}

				try
				{
					options = parser.parse( args );
				}
				catch ( joptsimple.OptionException ex )
				{
					Log.get( AppController.class.getName() ).log( Level.SEVERE, ex.getLocalizedMessage() );
				}

				ConsoleHandler consoleHandler = new ConsoleHandler();
				consoleHandler.setFormatter( new DefaultLogFormatter( !options.has( "nocolor" ) ) );
				Log.addHandler( consoleHandler );

				System.setOut( new PrintStream( new LoggerOutputStream( Log.get( "SysOut" ), Level.INFO ), true ) );
				System.setErr( new PrintStream( new LoggerOutputStream( Log.get( "SysErr" ), Level.SEVERE ), true ) );

				if ( options == null || options.has( "?" ) )
					try
					{
						parser.printHelpOn( System.out );
					}
					catch ( IOException ex )
					{
						Logger.getLogger( AppController.class.getName() ).log( Level.SEVERE, null, ex );
					}
				else if ( options.has( "v" ) )
					System.out.println( "Running " + Versioning.getProduct() + " version " + Versioning.getVersion() );
				else if ( options.has( "watchdog" ) )
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

					instance = ObjectFunc.initClass( loaderClass );
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
				AppController.handleExceptions( t );
			}

			if ( isRunning && Log.get() != null )
				Log.get().info( EnumColor.GOLD + "" + EnumColor.NEGATIVE + "Finished Initalizing " + Versioning.getProduct() + "! It took " + ( System.currentTimeMillis() - startTime ) + "ms!" );
			else if ( instance != null )
				instance.runLevel( RunLevel.DISPOSED );
		}
		catch ( UncaughtException | ApplicationException e )
		{
			AppController.handleExceptions( e );
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
		ReportingLevel.enableErrorLevelOnly( ReportingLevel.parse( AppController.config().getString( "server.errorReporting", "E_ALL ~E_NOTICE ~E_STRICT ~E_DEPRECATED" ) ) );

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

		Log.get().info( "Reinitalizing the Accounts Manager..." );
		AccountManager.instance().reload();

		runLevel( RunLevel.RUNNING );
	}

	public abstract void onRunlevelChange( RunLevel level ) throws ApplicationException;
}

package com.chiorichan;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.chiorichan.account.AccountManager;
import com.chiorichan.event.EventBus;
import com.chiorichan.event.EventHandler;
import com.chiorichan.event.EventPriority;
import com.chiorichan.event.Listener;
import com.chiorichan.event.application.RunlevelEvent;
import com.chiorichan.lang.ApplicationException;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.lang.ReportingLevel;
import com.chiorichan.lang.RunLevel;
import com.chiorichan.lang.StartupAbortException;
import com.chiorichan.lang.StartupException;
import com.chiorichan.logger.Log;
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
 *
 * @author Chiori-chan
 */
public abstract class AppLoader implements Listener
{
	static Watchdog watchdog = null;
	private static OptionSet options;
	private static boolean isRunning;
	public static long startTime = System.currentTimeMillis();

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

	}

	public RunLevel runLevel()
	{
		return runlevel.getRunLevel();
	}

	public static boolean isRunning()
	{
		return isRunning;
	}

	protected void runLevel( RunLevel level )
	{
		runlevel.setRunLevel( level );
	}

	public static void main( String... args ) throws Exception
	{
		init( SimpleLoader.class, args );
	}

	protected void start() throws ApplicationException
	{
		try
		{
			ReportingLevel.enableErrorLevelOnly( ReportingLevel.parse( AppController.config().getString( "server.errorReporting", "E_ALL ~E_NOTICE ~E_STRICT ~E_DEPRECATED" ) ) );

			runLevel( RunLevel.INITIALIZED );

			AppController.primaryThread.start();

			AppManager.manager( EventBus.class ).init();
			EventBus.instance().registerEvents( this, this );

			runLevel( RunLevel.INITIALIZED );

			AppManager.manager( PluginManager.class ).init();

			runLevel( RunLevel.INITIALIZATION );

			PluginManager.instance().loadPlugins();

			runLevel( RunLevel.STARTUP );
			runLevel( RunLevel.POSTSTARTUP );

			Log.get().info( "Initalizing the Permission Subsystem..." );
			AppManager.manager( PermissionManager.class ).init();

			Log.get().info( "Initalizing the Account Subsystem..." );
			AppManager.manager( AccountManager.class ).init();

			runLevel( RunLevel.RUNNING );

			// XXX There seems to be a problem registering sync'd tasks before this point
		}
		catch ( ApplicationException e )
		{
			throw e;
		}
		catch ( Throwable e )
		{
			throw new StartupException( "There was a problem initializing one of the modular systems", e );
		}
	}

	public static String uptime()
	{
		Duration duration = new Duration( System.currentTimeMillis() - startTime );
		PeriodFormatter formatter = new PeriodFormatterBuilder().appendDays().appendSuffix( " Day(s) " ).appendHours().appendSuffix( " Hour(s) " ).appendMinutes().appendSuffix( " Minute(s) " ).appendSeconds().appendSuffix( " Second(s)" ).toFormatter();
		return formatter.print( duration.toPeriod() );
	}

	private static void init( Class<? extends AppLoader> loaderClass, String... args ) throws StartupException
	{
		System.setProperty( "file.encoding", "utf-8" );
		OptionSet options = null;
		AppLoader instance = null;

		if ( loaderClass == null )
			loaderClass = SimpleLoader.class;

		try
		{
			OptionParser parser = new OptionParser()
			{
				{
					// TODO This needs refinement and an API
					acceptsAll( Arrays.asList( "?", "h", "help" ), "Show the help" );
					acceptsAll( Arrays.asList( "c", "config", "b", "settings" ), "File for chiori settings" ).withRequiredArg().ofType( File.class ).defaultsTo( new File( "server.yaml" ) ).describedAs( "Yml file" );
					acceptsAll( Arrays.asList( "p", "plugins" ), "Plugin directory to use" ).withRequiredArg().ofType( String.class ).defaultsTo( "plugins" ).describedAs( "Plugin directory" );
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
				// Ignore!
			}

			try
			{
				options = parser.parse( args );
			}
			catch ( joptsimple.OptionException ex )
			{
				Logger.getLogger( AppController.class.getName() ).log( Level.SEVERE, ex.getLocalizedMessage() );
			}

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
				instance = ObjectFunc.initClass( loaderClass );
				instance.start();
			}
		}
		catch ( StartupAbortException e )
		{
			instance.runLevel( RunLevel.SHUTDOWN );
		}
		catch ( Throwable t )
		{
			instance.runLevel( RunLevel.CRASHED );
			AppController.handleExceptions( t );
		}

		if ( isRunning && Log.get() != null )
			Log.get().info( EnumColor.GOLD + "" + EnumColor.NEGATIVE + "Finished Initalizing " + Versioning.getProduct() + "! It took " + ( System.currentTimeMillis() - startTime ) + "ms!" );
		else
			instance.runLevel( RunLevel.DISPOSED );
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

	/**
	 * Event listener for runlevel changes.
	 */
	@EventHandler( priority = EventPriority.NORMAL )
	public abstract void onRunlevelChange( RunlevelEvent event ) throws ApplicationException;
}

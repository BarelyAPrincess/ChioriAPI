package com.chiorichan;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import joptsimple.OptionSet;

import com.chiorichan.account.AccountManager;
import com.chiorichan.event.EventBus;
import com.chiorichan.event.EventRegistrar;
import com.chiorichan.event.Listener;
import com.chiorichan.event.services.ServiceRegisterEvent;
import com.chiorichan.event.services.ServiceUnregisterEvent;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.lang.ExceptionReport;
import com.chiorichan.lang.IException;
import com.chiorichan.lang.ReportingLevel;
import com.chiorichan.lang.RunLevel;
import com.chiorichan.logger.DefaultLogFormatter;
import com.chiorichan.logger.Log;
import com.chiorichan.logger.LoggerOutputStream;
import com.chiorichan.permission.PermissionManager;
import com.chiorichan.plugin.PluginManager;
import com.chiorichan.services.ObjectContext;
import com.chiorichan.services.RegisteredServiceProvider;
import com.chiorichan.services.ServicePriority;
import com.chiorichan.services.ServiceProvider;
import com.chiorichan.tasks.TaskManager;
import com.chiorichan.tasks.TaskRegistrar;
import com.chiorichan.tasks.Timings;
import com.chiorichan.terminal.CommandDispatch;
import com.chiorichan.util.Versioning;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public abstract class AppController implements Runnable, EventRegistrar, TaskRegistrar, Listener
{
	public static final String BROADCAST_CHANNEL_ADMINISTRATIVE = "sys.admin";
	public static final String BROADCAST_CHANNEL_USERS = "sys.user";

	public static int lastFiveTick = -1;
	public static int currentTick = ( int ) ( System.currentTimeMillis() / 50 );

	public static Thread primaryThread;
	protected static AppController instance;
	protected static final ExecutorService pool = Executors.newCachedThreadPool();

	protected static final AppConfig config = new AppConfig();
	private static boolean isRunning = false;
	private static final Map<Class<?>, List<RegisteredServiceProvider<?, ServiceProvider>>> providers = new HashMap<>();

	private static String stopReason = null;

	private static boolean willRestart = false;

	public static AppConfig config()
	{
		if ( !config.isConfigLoaded() )
			config.loadConfig( new File( "config.yaml" ), "com/chiorichan/config.yaml" );
		return config;
	}

	/**
	 * Queries for a provider. This may return if no provider has been
	 * registered for a service. The highest priority provider is returned.
	 *
	 * @param <T>
	 *             The service interface
	 * @param service
	 *             The service interface
	 * @return provider or null
	 * @throws ClassCastException
	 *              if the registered service was not of type expected
	 */
	@SuppressWarnings( "unchecked" )
	public static <T extends ServiceProvider> T getService( Class<?> service ) throws ClassCastException
	{
		synchronized ( providers )
		{
			List<RegisteredServiceProvider<?, ServiceProvider>> registered = providers.get( service );

			if ( registered == null )
				for ( Class<?> clz : providers.keySet() )
					if ( clz.isAssignableFrom( service ) )
						registered = providers.get( clz );

			if ( registered == null )
				return null;

			// This should not be null!
			return ( T ) registered.get( 0 ).getProvider();
		}
	}

	@SuppressWarnings( "unchecked" )
	public static <T extends ServiceProvider> T getService( Class<?> service, ServicePriority priority ) throws ClassCastException
	{
		synchronized ( providers )
		{
			for ( Class<?> clz : providers.keySet() )
				if ( clz.isAssignableFrom( service ) )
					for ( RegisteredServiceProvider<?, ServiceProvider> provider : providers.get( clz ) )
						if ( provider.getPriority().equals( priority ) )
							return ( T ) provider.getProvider();
		}
		return null;
	}

	@SuppressWarnings( "unchecked" )
	public static <T extends ServiceProvider> List<T> getServiceList( Class<?> service ) throws ClassCastException
	{
		return new ArrayList<T>()
		{
			{
				synchronized ( providers )
				{
					for ( Class<?> clz : providers.keySet() )
						if ( clz.isAssignableFrom( service ) )
							for ( RegisteredServiceProvider<?, ServiceProvider> provider : providers.get( clz ) )
								add( ( T ) provider.getProvider() );
				}
			}
		};
	}



	public static void handleExceptions( Throwable... ts )
	{
		ExceptionReport report = new ExceptionReport();
		for ( Throwable t : ts )
			if ( t instanceof IException )
				report.addException( ( IException ) t );
			else
				report.addException( ReportingLevel.E_ERROR, t );

		if ( report.hasNonIgnorableExceptions() )
		{
			ExceptionReport.printExceptions( report.getNotIgnorableExceptions() );
			stopApplication( "WE ENCOUNTERED AN UNEXPECTED EXCEPTION (" + AppLoader.uptime() + "ms)!" );

			// TODO Pass that this was a crash
		}
	}

	public final static boolean isPrimaryThread()
	{
		return Thread.currentThread().equals( primaryThread );
	}

	public static boolean isRunning()
	{
		return isRunning;
	}

	public static void registerRunnable( Runnable runnable )
	{
		if ( runnable != null )
			pool.execute( runnable );
	}

	/**
	 * Register a provider of a service.
	 *
	 * @param <T>
	 *             Provider
	 * @param service
	 *             service class
	 * @param provider
	 *             provider to register
	 * @param context
	 *             context with the provider
	 * @param priority
	 *             priority of the provider
	 */
	@SuppressWarnings( "unchecked" )
	public static <T, P extends ServiceProvider> void registerService( Class<T> service, P provider, ObjectContext context, ServicePriority priority )
	{
		RegisteredServiceProvider<T, P> registeredProvider = null;
		synchronized ( providers )
		{
			List<RegisteredServiceProvider<?, ServiceProvider>> registered = providers.get( service );
			if ( registered == null )
			{
				registered = new ArrayList<RegisteredServiceProvider<?, ServiceProvider>>();
				providers.put( service, registered );
			}

			registeredProvider = new RegisteredServiceProvider<T, P>( service, provider, context, priority );

			// Insert the provider into the collection, much more efficient big O than sort
			int position = Collections.binarySearch( registered, registeredProvider );
			if ( position < 0 )
				registered.add( - ( position + 1 ), ( RegisteredServiceProvider<?, ServiceProvider> ) registeredProvider );
			else
				registered.add( position, ( RegisteredServiceProvider<?, ServiceProvider> ) registeredProvider );

		}
		EventBus.instance().callEvent( new ServiceRegisterEvent<T>( registeredProvider ) );
	}

	public static void reloadApplication( String reason )
	{
		if ( !AppLoader.isWatchdogRunning() )
		{
			Log.get().highlight( "Server can not be restarted without Watchdog running." );
			return;
		}

		if ( reason == null )
			Log.get().highlight( "Server is restarting, be back soon... :D" );
		else if ( !reason.isEmpty() )
			Log.get().highlight( reason );

		stopReason = reason;
		willRestart = true;
		isRunning = false;
	}

	public static void stopApplication( String reason )
	{
		// Log.get().warning( EnumColor.RED + "" + EnumColor.NEGATIVE + "CHECK THE LOGS AND TRY AGAIN " );

		if ( reason == null )
			Log.get().highlight( "Stopping... Goodbye!" );
		else if ( !reason.isEmpty() )
			Log.get().highlight( "Stopping for Reason: " + reason );

		stopReason = reason;
		willRestart = false;
		isRunning = false;
	}

	private OptionSet options;
	public boolean useColors = true;

	public AppLoader loader;

	protected AppController()
	{
		instance = this;

		primaryThread = new Thread( this, "Server Thread" );
		primaryThread.setPriority( Thread.MAX_PRIORITY );

		if ( options.has( "nocolor" ) )
			useColors = false;

		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter( new DefaultLogFormatter() );
		Log.addHandler( consoleHandler );

		System.setOut( new PrintStream( new LoggerOutputStream( Log.get( "SysOut" ).getLogger(), Level.INFO ), true ) );
		System.setErr( new PrintStream( new LoggerOutputStream( Log.get( "SysErr" ).getLogger(), Level.SEVERE ), true ) );
	}

	private void finalShutdown()
	{
		Object timing = new Object();
		Timings.start( timing );

		loader.runLevel( RunLevel.SHUTDOWN );

		pool.shutdown();

		Log.get().info( "Shutting Down Plugin Manager..." );
		PluginManager.instance().shutdown();

		Log.get().info( "Shutting Down Permission Manager..." );
		PermissionManager.instance().saveData();

		Log.get().info( "Shutting Down Account Manager..." );
		AccountManager.instance().shutdown( stopReason );

		Log.get().info( "Shutting Down Task Manager..." );
		TaskManager.instance().shutdown();

		Log.get().info( "Saving Configuration..." );
		AppController.config().saveConfig();

		Log.get().info( "Clearing Excess Cache..." );
		long keepHistory = config().getLong( "advanced.cache.keepHistory", 30L );
		config().clearCache( keepHistory );

		loader.runLevel( RunLevel.DISPOSED );

		Log.get().info( EnumColor.GOLD + "" + EnumColor.NEGATIVE + "Shutdown Completed! It took " + Timings.finish( timing ) + "ms!" );

		if ( willRestart )
			System.exit( 99 );
		else
			System.exit( 0 );
	}

	/**
	 * Get a list of known services. A service is known if it has registered
	 * providers for it.
	 *
	 * @return a copy of the set of known services
	 */
	public Set<Class<?>> getKnownServices()
	{
		synchronized ( providers )
		{
			return ImmutableSet.<Class<?>> copyOf( providers.keySet() );
		}
	}

	@Override
	public String getName()
	{
		return Versioning.getProduct() + " " + Versioning.getVersion();
	}

	/**
	 * Queries for a provider registration. This may return if no provider
	 * has been registered for a service.
	 *
	 * @param <T>
	 *             The service interface
	 * @param service
	 *             The service interface
	 * @return provider registration or null
	 */
	@SuppressWarnings( "unchecked" )
	public <T> RegisteredServiceProvider<T, ServiceProvider> getServiceRegistration( Class<T> service )
	{
		synchronized ( providers )
		{
			List<RegisteredServiceProvider<?, ServiceProvider>> registered = providers.get( service );

			if ( registered == null )
				return null;

			// This should not be null!
			return ( RegisteredServiceProvider<T, ServiceProvider> ) registered.get( 0 );
		}
	}

	/**
	 * Get registrations of providers for a service. The returned list is
	 * an unmodifiable copy.
	 *
	 * @param <T>
	 *             The service interface
	 * @param service
	 *             The service interface
	 * @return a copy of the list of registrations
	 */
	@SuppressWarnings( "unchecked" )
	public <T> List<RegisteredServiceProvider<T, ServiceProvider>> getServiceRegistrations( Class<T> service )
	{
		ImmutableList.Builder<RegisteredServiceProvider<T, ServiceProvider>> ret;
		synchronized ( providers )
		{
			List<RegisteredServiceProvider<?, ServiceProvider>> registered = providers.get( service );

			if ( registered == null )
				return ImmutableList.<RegisteredServiceProvider<T, ServiceProvider>> of();

			ret = ImmutableList.<RegisteredServiceProvider<T, ServiceProvider>> builder();

			for ( RegisteredServiceProvider<?, ServiceProvider> provider : registered )
				ret.add( ( RegisteredServiceProvider<T, ServiceProvider> ) provider );

		}
		return ret.build();
	}

	/**
	 * Get registrations of providers for a context.
	 *
	 * @param context
	 *             The context
	 * @return provider registration or null
	 */
	public List<RegisteredServiceProvider<?, ServiceProvider>> getServiceRegistrations( ObjectContext context )
	{
		ImmutableList.Builder<RegisteredServiceProvider<?, ServiceProvider>> ret = ImmutableList.<RegisteredServiceProvider<?, ServiceProvider>> builder();
		synchronized ( providers )
		{
			for ( List<RegisteredServiceProvider<?, ServiceProvider>> registered : providers.values() )
				for ( RegisteredServiceProvider<?, ServiceProvider> provider : registered )
					if ( provider.getObjectContext().equals( context ) )
						ret.add( provider );
		}
		return ret.build();
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	@Override
	public void run()
	{
		try
		{
			isRunning = true;
			long i = System.currentTimeMillis();

			long q = 0L;
			long j = 0L;
			for ( ;; )
			{
				long k = System.currentTimeMillis();
				long l = k - i;

				if ( l > 2000L && i - q >= 15000L )
				{
					if ( config().warnOnOverload() )
						Log.get().warning( "Can't keep up! Did the system time change, or is the server overloaded?" );
					l = 2000L;
					q = i;
				}

				if ( l < 0L )
				{
					Log.get().warning( "Time ran backwards! Did the system time change?" );
					l = 0L;
				}

				j += l;
				i = k;

				while ( j > 50L )
				{
					currentTick = ( int ) ( System.currentTimeMillis() / 50 );
					j -= 50L;

					CommandDispatch.handleCommands();
					TaskManager.instance().heartbeat( currentTick );
				}

				if ( !isRunning() )
					break;
				Thread.sleep( 1L );
			}
		}
		catch ( Throwable t )
		{
			handleExceptions( t );
		}
		finally
		{
			finalShutdown();
		}
	}

	/**
	 * Returns whether a provider has been registered for a service.
	 *
	 * @param <T>
	 *             service
	 * @param service
	 *             service to check
	 * @return true if and only if there are registered providers
	 */
	public <T> boolean serviceIsProvidedFor( Class<T> service )
	{
		synchronized ( providers )
		{
			return providers.containsKey( service );
		}
	}

	/**
	 * Unregister a particular provider for a particular service.
	 *
	 * @param service
	 *             The service interface
	 * @param provider
	 *             The service provider implementation
	 */
	@SuppressWarnings( {"rawtypes", "unchecked"} )
	public void serviceUnregister( Class<?> service, ServiceProvider provider )
	{
		ArrayList<ServiceUnregisterEvent<?>> unregisteredEvents = new ArrayList<ServiceUnregisterEvent<?>>();
		synchronized ( providers )
		{
			Iterator<Map.Entry<Class<?>, List<RegisteredServiceProvider<?, ServiceProvider>>>> it = providers.entrySet().iterator();

			try
			{
				while ( it.hasNext() )
				{
					Map.Entry<Class<?>, List<RegisteredServiceProvider<?, ServiceProvider>>> entry = it.next();

					// We want a particular service
					if ( entry.getKey() != service )
						continue;

					Iterator<RegisteredServiceProvider<?, ServiceProvider>> it2 = entry.getValue().iterator();

					try
					{
						// Removed entries that are from this context

						while ( it2.hasNext() )
						{
							RegisteredServiceProvider<?, ServiceProvider> registered = it2.next();

							if ( registered.getProvider() == provider )
							{
								it2.remove();
								unregisteredEvents.add( new ServiceUnregisterEvent( registered ) );
							}
						}
					}
					catch ( NoSuchElementException e )
					{ // Why does Java suck
					}

					// Get rid of the empty list
					if ( entry.getValue().size() == 0 )
						it.remove();
				}
			}
			catch ( NoSuchElementException e )
			{
			}
		}
		for ( ServiceUnregisterEvent<?> event : unregisteredEvents )
			EventBus.instance().callEvent( event );
	}

	/**
	 * Unregister a particular provider.
	 *
	 * @param provider
	 *             The service provider implementation
	 */
	@SuppressWarnings( {"rawtypes", "unchecked"} )
	public void serviceUnregister( Object provider )
	{
		ArrayList<ServiceUnregisterEvent<?>> unregisteredEvents = new ArrayList<ServiceUnregisterEvent<?>>();
		synchronized ( providers )
		{
			Iterator<Map.Entry<Class<?>, List<RegisteredServiceProvider<?, ServiceProvider>>>> it = providers.entrySet().iterator();

			try
			{
				while ( it.hasNext() )
				{
					Map.Entry<Class<?>, List<RegisteredServiceProvider<?, ServiceProvider>>> entry = it.next();
					Iterator<RegisteredServiceProvider<?, ServiceProvider>> it2 = entry.getValue().iterator();

					try
					{
						// Removed entries that are from this context

						while ( it2.hasNext() )
						{
							RegisteredServiceProvider<?, ServiceProvider> registered = it2.next();

							if ( registered.getProvider().equals( provider ) )
							{
								it2.remove();
								unregisteredEvents.add( new ServiceUnregisterEvent( registered ) );
							}
						}
					}
					catch ( NoSuchElementException e )
					{ // Why does Java suck
					}

					// Get rid of the empty list
					if ( entry.getValue().size() == 0 )
						it.remove();
				}
			}
			catch ( NoSuchElementException e )
			{
			}
		}
		for ( ServiceUnregisterEvent<?> event : unregisteredEvents )
			EventBus.instance().callEvent( event );
	}

	/**
	 * Unregister all the providers registered by a particular context.
	 *
	 * @param context
	 *             The context
	 */
	@SuppressWarnings( {"rawtypes", "unchecked"} )
	public void serviceUnregisterAll( Object context )
	{
		ArrayList<ServiceUnregisterEvent<?>> unregisteredEvents = new ArrayList<>();
		synchronized ( providers )
		{
			Iterator<Map.Entry<Class<?>, List<RegisteredServiceProvider<?, ServiceProvider>>>> it = providers.entrySet().iterator();

			try
			{
				while ( it.hasNext() )
				{
					Map.Entry<Class<?>, List<RegisteredServiceProvider<?, ServiceProvider>>> entry = it.next();
					Iterator<RegisteredServiceProvider<?, ServiceProvider>> it2 = entry.getValue().iterator();

					try
					{
						// Removed entries that are from this context
						while ( it2.hasNext() )
						{
							RegisteredServiceProvider<?, ServiceProvider> registered = it2.next();

							if ( registered.getObjectContext().equals( context ) )
							{
								it2.remove();
								unregisteredEvents.add( new ServiceUnregisterEvent( registered ) );
							}
						}
					}
					catch ( NoSuchElementException e )
					{ // Why does Java suck
					}

					// Get rid of the empty list
					if ( entry.getValue().size() == 0 )
						it.remove();
				}
			}
			catch ( NoSuchElementException e )
			{
			}
		}
		for ( ServiceUnregisterEvent<?> event : unregisteredEvents )
			EventBus.instance().callEvent( event );
	}

	public void setLoader( AppLoader loader )
	{
		this.loader = loader;
	}

	public void setParams( OptionSet options )
	{
		this.options = options;
	}
}

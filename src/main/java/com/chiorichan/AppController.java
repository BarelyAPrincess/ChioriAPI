/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com> All Right Reserved.
 */
package com.chiorichan;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.chiorichan.account.AccountManager;
import com.chiorichan.event.EventRegistrar;
import com.chiorichan.event.Listener;
import com.chiorichan.lang.ApplicationException;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.lang.ExceptionReport;
import com.chiorichan.lang.IException;
import com.chiorichan.lang.ReportingLevel;
import com.chiorichan.lang.RunLevel;
import com.chiorichan.lang.StartupAbortException;
import com.chiorichan.logger.Log;
import com.chiorichan.permission.PermissionManager;
import com.chiorichan.plugin.PluginManager;
import com.chiorichan.tasks.TaskManager;
import com.chiorichan.tasks.TaskRegistrar;
import com.chiorichan.tasks.Timings;
import com.chiorichan.terminal.CommandDispatch;
import com.chiorichan.util.Versioning;

public final class AppController implements Runnable, EventRegistrar, TaskRegistrar, Listener
{
	public static final String BROADCAST_CHANNEL_ADMINISTRATIVE = "sys.admin";
	public static final String BROADCAST_CHANNEL_USERS = "sys.user";
	protected static final ExecutorService pool = Executors.newCachedThreadPool();
	public static int currentTick = ( int ) ( System.currentTimeMillis() / 50 );
	private static boolean willRestart = false;
	private static boolean isRunning = false;
	private static String stopReason = null;
	protected static AppController instance;
	public static int lastFiveTick = -1;
	public static Thread primaryThread;

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
			List<IException> list = Arrays.asList( report.getNotIgnorableExceptions() );

			Log.get().severe( "Encountered " + list.size() + " exception(s):" );
			for ( IException e : list )
				Log.get().severe( e.getClass() + ": " + e.getMessage(), e instanceof Throwable && Versioning.isDevelopment() ? ( Throwable ) e : null );

			stopApplication( "CRASHED" );

			// TODO Pass that this was a crash
		}
	}

	public static boolean isPrimaryThread()
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

	public static void reloadApplication( String reason )
	{
		if ( !AppLoader.isWatchdogRunning() )
		{
			Log.get().notice( "Server can not be restarted without Watchdog running." );
			return;
		}

		if ( reason == null )
			Log.get().notice( "Server is restarting, be back soon... :D" );
		else if ( !reason.isEmpty() )
			Log.get().notice( reason );

		stopReason = reason;
		willRestart = true;
		isRunning = false;
	}

	public static void restartApplication( String reason )
	{
		if ( reason == null )
			Log.get().notice( "Restarting!" );
		else if ( !reason.isEmpty() )
			Log.get().notice( "Restarting for Reason: " + reason );

		stopReason = reason;
		willRestart = true;

		if ( !isRunning )
			// A shutdown was requested but the server never reached the running state!
			throw new StartupAbortException();

		isRunning = false;
	}

	public static void stopApplication( String reason )
	{
		if ( reason == null )
			Log.get().notice( "Stopping... Goodbye!" );
		else if ( !reason.isEmpty() )
			Log.get().notice( "Stopping for Reason: " + reason );

		stopReason = reason;
		willRestart = false;

		// if ( !isRunning )
		// A shutdown was requested but the server never reached the running state!
		// throw new StartupAbortException();

		isRunning = false;
	}

	public final AppLoader loader;

	protected AppController( AppLoader loader )
	{
		this.loader = loader;
		instance = this;

		primaryThread = new Thread( this, "Server Thread" );
		primaryThread.setPriority( Thread.MAX_PRIORITY );
	}

	private void finalShutdown() throws ApplicationException
	{
		Object timing = new Object();
		Timings.start( timing );

		loader.runLevel( RunLevel.SHUTDOWN );

		pool.shutdown();

		Log.get().info( "Shutting Down Plugin Manager..." );
		if ( PluginManager.instanceWithoutException() != null )
			PluginManager.instanceWithoutException().shutdown();

		Log.get().info( "Shutting Down Permission Manager..." );
		if ( PermissionManager.instanceWithoutException() != null )
			PermissionManager.instanceWithoutException().saveData();

		Log.get().info( "Shutting Down Account Manager..." );
		if ( AccountManager.instanceWithoutException() != null )
			AccountManager.instanceWithoutException().shutdown( stopReason );

		Log.get().info( "Shutting Down Task Manager..." );
		if ( TaskManager.instanceWithoutException() != null )
			TaskManager.instanceWithoutException().shutdown();

		Log.get().info( "Saving Configuration..." );
		AppConfig.get().save();

		Log.get().info( "Clearing Excess Cache..." );
		long keepHistory = AppConfig.get().getLong( "advanced.cache.keepHistory", 30L );
		AppConfig.get().clearCache( keepHistory );

		loader.runLevel( RunLevel.DISPOSED );

		Log.get().info( EnumColor.GOLD + "" + EnumColor.NEGATIVE + "Shutdown Completed! It took " + Timings.finish( timing ) + "ms!" );

		if ( willRestart )
			System.exit( 99 );
		else
			System.exit( 0 );
	}

	@Override
	public String getName()
	{
		return Versioning.getProduct() + " " + Versioning.getVersion();
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
					if ( AppConfig.get().warnOnOverload() )
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
			try
			{
				finalShutdown();
			}
			catch ( ApplicationException e )
			{
				handleExceptions( e );
			}
		}
	}
}

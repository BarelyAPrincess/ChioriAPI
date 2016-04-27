/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.lang3.Validate;

import com.chiorichan.AppController;
import com.chiorichan.lang.ApplicationException;
import com.chiorichan.lang.AuthorNagException;
import com.chiorichan.lang.DeprecatedDetail;
import com.chiorichan.lang.ReportingLevel;
import com.chiorichan.logger.Log;
import com.chiorichan.logger.LogSource;
import com.chiorichan.plugin.PluginBase;
import com.chiorichan.plugin.PluginManager;
import com.chiorichan.services.AppManager;
import com.chiorichan.services.ObjectContext;
import com.chiorichan.services.ServiceManager;
import com.google.common.collect.Maps;

public class EventBus implements ServiceManager, LogSource
{
	private static Map<Class<? extends AbstractEvent>, EventHandlers> handlers = Maps.newConcurrentMap();

	public static Log getLogger()
	{
		return Log.get( instance() );
	}

	public static EventBus instance()
	{
		return AppManager.manager( EventBus.class ).instance();
	}

	private boolean useTimings = false;

	private Object lock = new Object();

	public EventBus()
	{
		this( AppController.config().getBoolean( "plugins.useTimings" ) );
	}

	public EventBus( boolean useTimings )
	{
		this.useTimings = useTimings;
	}

	/**
	 * Calls an event with the given details.<br>
	 * This method only synchronizes when the event is not asynchronous.
	 *
	 * @param event
	 *             Event details
	 */
	public <T extends AbstractEvent> T callEvent( T event )
	{
		try
		{
			if ( event.isAsynchronous() )
			{
				if ( Thread.holdsLock( lock ) )
					throw new IllegalStateException( event.getEventName() + " cannot be triggered asynchronously from inside synchronized code." );
				if ( AppController.isPrimaryThread() )
					throw new IllegalStateException( event.getEventName() + " cannot be triggered asynchronously from primary server thread." );
				fireEvent( event );
			}
			else
				synchronized ( lock )
				{
					fireEvent( event );
				}
		}
		catch ( EventException ex )
		{

		}

		return event;
	}

	/**
	 * Calls an event with the given details.<br>
	 * This method only synchronizes when the event is not asynchronous.
	 *
	 * @param event
	 *             Event details
	 * @throws EventException
	 */
	public <T extends AbstractEvent> T callEventWithException( T event ) throws EventException
	{
		if ( event.isAsynchronous() )
		{
			if ( Thread.holdsLock( lock ) )
				throw new IllegalStateException( event.getEventName() + " cannot be triggered asynchronously from inside synchronized code." );
			if ( AppController.isPrimaryThread() )
				throw new IllegalStateException( event.getEventName() + " cannot be triggered asynchronously from primary server thread." );
			fireEvent( event );
		}
		else
			synchronized ( lock )
			{
				fireEvent( event );
			}

		return event;
	}

	public Map<Class<? extends AbstractEvent>, Set<RegisteredListener>> createRegisteredListeners( Listener listener, final ObjectContext context )
	{
		Validate.notNull( context, "Context can not be null" );
		Validate.notNull( listener, "Listener can not be null" );

		Map<Class<? extends AbstractEvent>, Set<RegisteredListener>> ret = new HashMap<Class<? extends AbstractEvent>, Set<RegisteredListener>>();
		Set<Method> methods;
		try
		{
			Method[] publicMethods = listener.getClass().getMethods();
			methods = new HashSet<Method>( publicMethods.length, Float.MAX_VALUE );
			for ( Method method : publicMethods )
				methods.add( method );
			for ( Method method : listener.getClass().getDeclaredMethods() )
				methods.add( method );
		}
		catch ( NoClassDefFoundError e )
		{
			getLogger().severe( String.format( "Plugin %s has failed to register events for %s because %s does not exist.", context.getFullName(), listener.getClass(), e.getMessage() ) );
			return ret;
		}

		for ( final Method method : methods )
		{
			final EventHandler eh = method.getAnnotation( EventHandler.class );
			if ( eh == null )
				continue;
			final Class<?> checkClass;
			if ( method.getParameterTypes().length != 1 || !AbstractEvent.class.isAssignableFrom( checkClass = method.getParameterTypes()[0] ) )
			{
				getLogger().severe( context.getFullName() + " attempted to register an invalid EventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass() );
				continue;
			}
			final Class<? extends AbstractEvent> eventClass = checkClass.asSubclass( AbstractEvent.class );
			method.setAccessible( true );
			Set<RegisteredListener> eventSet = ret.get( eventClass );
			if ( eventSet == null )
			{
				eventSet = new HashSet<RegisteredListener>();
				ret.put( eventClass, eventSet );
			}

			if ( ReportingLevel.E_DEPRECATED.isEnabled() )
				for ( Class<?> clazz = eventClass; AbstractEvent.class.isAssignableFrom( clazz ); clazz = clazz.getSuperclass() )
				{
					if ( clazz.isAnnotationPresent( DeprecatedDetail.class ) )
					{
						DeprecatedDetail deprecated = clazz.getAnnotation( DeprecatedDetail.class );

						PluginManager.getLogger().warning( String.format( "The creator '%s' has registered a listener for %s on method '%s', but the event is Deprecated for reason '%s'; please notify the authors %s.", context.getFullName(), clazz.getName(), method.toGenericString(), deprecated.reason(), Arrays.toString( context.getAuthors().toArray() ) ) );
						break;
					}

					if ( clazz.isAnnotationPresent( Deprecated.class ) )
					{
						PluginManager.getLogger().warning( String.format( "The creator '%s' has registered a listener for %s on method '%s', but the event is Deprecated! Please notify the authors %s.", context.getFullName(), clazz.getName(), method.toGenericString(), Arrays.toString( context.getAuthors().toArray() ) ) );
						break;
					}
				}

			EventExecutor executor = new EventExecutor()
			{
				@Override
				public void execute( Listener listener, AbstractEvent event ) throws EventException
				{
					try
					{
						if ( !eventClass.isAssignableFrom( event.getClass() ) )
							return;
						method.invoke( listener, event );
					}
					catch ( InvocationTargetException ex )
					{
						throw new EventException( ex.getCause() );
					}
					catch ( Throwable t )
					{
						throw new EventException( t );
					}
				}
			};
			if ( useTimings )
				eventSet.add( new TimedRegisteredListener( listener, executor, eh.priority(), context, eh.ignoreCancelled() ) );
			else
				eventSet.add( new RegisteredListener( listener, executor, eh.priority(), context, eh.ignoreCancelled() ) );
		}
		return ret;
	}

	private void fireEvent( AbstractEvent event ) throws EventException
	{
		for ( RegisteredListener registration : getEventListeners( event.getClass() ) )
		{
			if ( !registration.getContext().isEnabled() )
				continue;

			try
			{
				registration.callEvent( event );
			}
			catch ( AuthorNagException ex )
			{
				if ( registration.getContext().getSource() instanceof PluginBase )
				{
					PluginBase creator = ( PluginBase ) registration.getContext().getSource();

					if ( creator.isNaggable() )
					{
						creator.setNaggable( false );
						getLogger().log( Level.SEVERE, String.format( "Nag author(s): '%s' of '%s' about the following: %s", creator.getDescription().getAuthors(), creator.getDescription().getFullName(), ex.getMessage() ) );
					}
				}
			}
			catch ( EventException ex )
			{
				if ( ex.getCause() == null )
				{
					ex.printStackTrace();
					getLogger().log( Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getContext().getName() + "\nEvent Exception Reason: " + ex.getMessage() );
				}
				else
				{
					ex.getCause().printStackTrace();
					getLogger().log( Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getContext().getName() + "\nEvent Exception Reason: " + ex.getCause().getMessage() );
				}
				throw ex;
			}
			catch ( Throwable ex )
			{
				getLogger().log( Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getContext().getName(), ex );
			}
		}

		if ( event instanceof SelfHandling )
			( ( SelfHandling ) event ).handle();
	}

	private EventHandlers getEventListeners( Class<? extends AbstractEvent> event )
	{
		EventHandlers eventHandlers = handlers.get( event );

		if ( eventHandlers == null )
		{
			eventHandlers = new EventHandlers();
			handlers.put( event, eventHandlers );
		}

		return eventHandlers;
	}

	@Override
	public String getLoggerId()
	{
		return null;
	}

	@Override
	public void init() throws ApplicationException
	{

	}

	public void registerEvent( Class<? extends AbstractEvent> event, Listener listener, EventPriority priority, EventExecutor executor, Object object )
	{
		registerEvent( event, listener, priority, executor, new ObjectContext( object ), false );
	}

	/**
	 * Registers the given event to the specified listener using a directly passed EventExecutor
	 *
	 * @param event
	 *             Event class to register
	 * @param listener
	 *             Listener to register
	 * @param priority
	 *             Priority of this event
	 * @param executor
	 *             EventExecutor to register
	 * @param object
	 *             Source of event registration
	 * @param ignoreCancelled
	 *             Do not call executor if event was already cancelled
	 */
	public void registerEvent( Class<? extends AbstractEvent> event, Listener listener, EventPriority priority, EventExecutor executor, Object object, boolean ignoreCancelled )
	{
		registerEvent( event, listener, priority, executor, new ObjectContext( object ), ignoreCancelled );
	}

	public void registerEvent( Class<? extends AbstractEvent> event, Listener listener, EventPriority priority, EventExecutor executor, ObjectContext context )
	{
		registerEvent( event, listener, priority, executor, context, false );
	}

	/**
	 * Registers the given event to the specified listener using a directly passed EventExecutor
	 *
	 * @param event
	 *             Event class to register
	 * @param listener
	 *             Listener to register
	 * @param priority
	 *             Priority of this event
	 * @param executor
	 *             EventExecutor to register
	 * @param context
	 *             The Object Context
	 * @param ignoreCancelled
	 *             Do not call executor if event was already cancelled
	 */
	public void registerEvent( Class<? extends AbstractEvent> event, Listener listener, EventPriority priority, EventExecutor executor, ObjectContext context, boolean ignoreCancelled )
	{
		Validate.notNull( listener, "Listener cannot be null" );
		Validate.notNull( priority, "Priority cannot be null" );
		Validate.notNull( executor, "Executor cannot be null" );
		Validate.notNull( context, "Creator cannot be null" );

		if ( useTimings )
			getEventListeners( event ).register( new TimedRegisteredListener( listener, executor, priority, context, ignoreCancelled ) );
		else
			getEventListeners( event ).register( new RegisteredListener( listener, executor, priority, context, ignoreCancelled ) );
	}

	public void registerEvents( Listener listener, Object source )
	{
		registerEvents( listener, new ObjectContext( source ) );
	}

	public void registerEvents( Listener listener, ObjectContext context )
	{
		for ( Map.Entry<Class<? extends AbstractEvent>, Set<RegisteredListener>> entry : createRegisteredListeners( listener, context ).entrySet() )
			getEventListeners( entry.getKey() ).registerAll( entry.getValue() );
	}

	public void unregisterEvents( EventRegistrar creator )
	{
		EventHandlers.unregisterAll( creator );
	}

	public void unregisterEvents( Listener listener )
	{
		EventHandlers.unregisterAll( listener );
	}

	public boolean useTimings()
	{
		return useTimings;
	}

	/**
	 * Sets whether or not per event timing code should be used
	 *
	 * @param use
	 *             True if per event timing code should be used
	 */
	public void useTimings( boolean use )
	{
		useTimings = use;
	}
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.services;

import com.chiorichan.event.EventBus;
import com.chiorichan.event.services.ServiceRegisterEvent;
import com.chiorichan.event.services.ServiceUnregisterEvent;
import com.chiorichan.zutils.ZObjects;
import com.chiorichan.lang.ApplicationException;
import com.chiorichan.logger.Log;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class AppManager<T extends ServiceManager>
{
	private static final List<AppManager<?>> managers = new ArrayList<>();
	private static final Map<Class<?>, List<RegisteredServiceProvider<?, ServiceProvider>>> providers = new HashMap<>();

	/**
	 * Queries for a provider. This may return if no provider has been registered for a service. The highest priority provider is returned.
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

	@SuppressWarnings( "unchecked" )
	public static <M extends ServiceManager> AppManager<M> manager( Class<M> clz )
	{
		for ( AppManager<?> mgr : managers )
			if ( mgr.getManagerClass().equals( clz ) )
				return ( AppManager<M> ) mgr;
		return new AppManager<M>( clz );
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
				registered.add( -( position + 1 ), ( RegisteredServiceProvider<?, ServiceProvider> ) registeredProvider );
			else
				registered.add( position, ( RegisteredServiceProvider<?, ServiceProvider> ) registeredProvider );

		}
		EventBus.instance().callEvent( new ServiceRegisterEvent<T>( registeredProvider ) );
	}

	private final Class<T> managerClass;

	private T instance = null;

	private AppManager( Class<T> managerClass )
	{
		this.managerClass = managerClass;
		managers.add( this );
	}

	/**
	 * Get a list of known services. A service is known if it has registered providers for it.
	 *
	 * @return a copy of the set of known services
	 */
	public Set<Class<?>> getKnownServices()
	{
		synchronized ( providers )
		{
			return ImmutableSet.copyOf( providers.keySet() );
		}
	}

	public Log getLogger()
	{
		return instance == null ? Log.get( getName() ) : Log.get( instance() );
	}

	public Class<T> getManagerClass()
	{
		return managerClass;
	}

	public String getName()
	{
		return managerClass.getSimpleName();
	}

	/**
	 * Queries for a provider registration. This may return if no provider has been registered for a service.
	 *
	 * @param <C>
	 *             The service interface
	 * @param service
	 *             The service interface
	 * @return provider registration or null
	 */
	@SuppressWarnings( "unchecked" )
	public <C> RegisteredServiceProvider<C, ServiceProvider> getServiceRegistration( Class<C> service )
	{
		synchronized ( providers )
		{
			List<RegisteredServiceProvider<?, ServiceProvider>> registered = providers.get( service );

			if ( registered == null )
				return null;

			// This should not be null!
			return ( RegisteredServiceProvider<C, ServiceProvider> ) registered.get( 0 );
		}
	}

	/**
	 * Get registrations of providers for a service. The returned list is an unmodifiable copy.
	 *
	 * @param <C>
	 *             The service interface
	 * @param service
	 *             The service interface
	 * @return a copy of the list of registrations
	 */
	@SuppressWarnings( "unchecked" )
	public <C> List<RegisteredServiceProvider<C, ServiceProvider>> getServiceRegistrations( Class<C> service )
	{
		ImmutableList.Builder<RegisteredServiceProvider<C, ServiceProvider>> ret;
		synchronized ( providers )
		{
			List<RegisteredServiceProvider<?, ServiceProvider>> registered = providers.get( service );

			if ( registered == null )
				return ImmutableList.of();

			ret = ImmutableList.builder();

			for ( RegisteredServiceProvider<?, ServiceProvider> provider : registered )
				ret.add( ( RegisteredServiceProvider<C, ServiceProvider> ) provider );

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
		ImmutableList.Builder<RegisteredServiceProvider<?, ServiceProvider>> ret = ImmutableList.builder();
		synchronized ( providers )
		{
			for ( List<RegisteredServiceProvider<?, ServiceProvider>> registered : providers.values() )
				for ( RegisteredServiceProvider<?, ServiceProvider> provider : registered )
					if ( provider.getObjectContext().equals( context ) )
						ret.add( provider );
		}
		return ret.build();
	}

	public void init( Object... args ) throws ApplicationException
	{
		// TODO Check if it's permitted!

		if ( instance != null )
			throw new IllegalStateException( "The " + getName() + " has already been initialized!" );

		instance = ZObjects.initClass( managerClass, args );
		instance.init();
	}

	public T instance()
	{
		if ( instance == null )
			throw new IllegalStateException( "The " + getName() + " has not been initialized!" );
		return instance;
	}

	public T instanceWithoutException()
	{
		return instance;
	}

	public boolean isInitialized()
	{
		return instance != null;
	}

	/**
	 * Returns whether a provider has been registered for a service.
	 *
	 * @param <C>
	 *             service
	 * @param service
	 *             service to check
	 * @return true if and only if there are registered providers
	 */
	public <C> boolean serviceIsProvidedFor( Class<C> service )
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
				// Ignore
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
				// Ignore
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
				// Ignore
			}
		}
		for ( ServiceUnregisterEvent<?> event : unregisteredEvents )
			EventBus.instance().callEvent( event );
	}
}

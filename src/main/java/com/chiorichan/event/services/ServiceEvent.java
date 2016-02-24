package com.chiorichan.event.services;

import com.chiorichan.event.application.ApplicationEvent;
import com.chiorichan.services.RegisteredServiceProvider;
import com.chiorichan.services.ServiceProvider;

/**
 * An event relating to a registered service. This is called in a {@link com.chiorichan.services.ServicesManager}
 */
public abstract class ServiceEvent<T> extends ApplicationEvent
{
	private final RegisteredServiceProvider<T, ? extends ServiceProvider> provider;

	public ServiceEvent( final RegisteredServiceProvider<T, ? extends ServiceProvider> provider )
	{
		this.provider = provider;
	}

	public RegisteredServiceProvider<T, ? extends ServiceProvider> getProvider()
	{
		return provider;
	}
}

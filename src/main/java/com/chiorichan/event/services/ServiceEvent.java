/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.event.services;

import com.chiorichan.event.application.ApplicationEvent;
import com.chiorichan.services.RegisteredServiceProvider;
import com.chiorichan.services.ServiceProvider;

/**
 * An event relating to a registered service. This is called in a {@link com.chiorichan.services.AppManager}
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

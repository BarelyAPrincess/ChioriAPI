/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
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

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.event.services;

import com.chiorichan.services.RegisteredServiceProvider;
import com.chiorichan.services.ServiceProvider;

/**
 * This event is called when a service is registered.
 * <p>
 * Warning: The order in which register and unregister events are called should not be relied upon.
 */
public class ServiceRegisterEvent<T> extends ServiceEvent<T>
{
	public ServiceRegisterEvent( RegisteredServiceProvider<T, ? extends ServiceProvider> registeredProvider )
	{
		super( registeredProvider );
	}
}

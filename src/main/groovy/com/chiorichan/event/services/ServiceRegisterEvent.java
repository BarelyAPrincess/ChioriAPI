/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
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

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.account;

import com.chiorichan.account.event.AccountLoadEvent;
import com.chiorichan.event.EventBus;
import com.chiorichan.event.EventRegistrar;
import com.chiorichan.logger.LogSource;
import com.chiorichan.services.ServiceManager;
import com.chiorichan.services.ServiceProvider;
import com.chiorichan.tasks.TaskRegistrar;

/**
 * Handles the task and events between the {@link EventBus} and {@link AccountManager}
 */
public abstract class AccountEvents implements EventRegistrar, TaskRegistrar, ServiceProvider, ServiceManager, LogSource
{
	void fireAccountLoad( AccountMeta meta )
	{
		EventBus.instance().callEvent( new AccountLoadEvent( meta ) );
	}

	// TODO Add more account events
}

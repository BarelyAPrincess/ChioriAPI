/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.event.plugin;

import com.chiorichan.event.EventRegistrar;

/**
 * Called when a plugin is disabled.
 */
public class PluginDisableEvent extends PluginEvent
{
	public PluginDisableEvent( final EventRegistrar plugin )
	{
		super( plugin );
	}
}

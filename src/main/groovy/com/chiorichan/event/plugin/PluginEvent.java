/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.event.plugin;

import com.chiorichan.event.EventRegistrar;
import com.chiorichan.event.application.ApplicationEvent;

/**
 * Used for plugin enable and disable events
 */
public abstract class PluginEvent extends ApplicationEvent
{
	private final EventRegistrar plugin;
	
	public PluginEvent( final EventRegistrar plugin )
	{
		this.plugin = plugin;
	}
	
	/**
	 * Gets the plugin involved in this event
	 * 
	 * @return Plugin for this event
	 */
	public EventRegistrar getPlugin()
	{
		return plugin;
	}
}

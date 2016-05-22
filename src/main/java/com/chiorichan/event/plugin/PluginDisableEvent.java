/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
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

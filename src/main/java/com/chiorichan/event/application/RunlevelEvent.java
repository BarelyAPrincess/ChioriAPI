/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2015 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.event.application;

import com.chiorichan.event.EventBus;
import com.chiorichan.lang.RunLevel;
import com.chiorichan.logger.Log;

public class RunlevelEvent extends ApplicationEvent
{
	protected static RunLevel previousLevel;
	protected static RunLevel currentLevel;

	public RunlevelEvent()
	{
		currentLevel = RunLevel.INITIALIZATION;
	}

	public RunlevelEvent( RunLevel level )
	{
		currentLevel = level;
	}

	public RunLevel getLastRunLevel()
	{
		return previousLevel;
	}

	public RunLevel getRunLevel()
	{
		return currentLevel;
	}

	public void setRunLevel( RunLevel level )
	{
		previousLevel = currentLevel;
		currentLevel = level;

		Log.get().fine( "Application Runlevel has been changed to '" + level.name() + "'" );

		EventBus.INSTANCE.callEvent( this );
	}
}

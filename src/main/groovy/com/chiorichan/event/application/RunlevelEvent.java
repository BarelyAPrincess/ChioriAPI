/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.event.application;

import com.chiorichan.AppController;
import com.chiorichan.event.EventBus;
import com.chiorichan.event.EventException;
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

		try
		{
			EventBus.instance().callEventWithException( this );
		}
		catch ( EventException e )
		{
			AppController.handleExceptions( e );
		}
	}
}

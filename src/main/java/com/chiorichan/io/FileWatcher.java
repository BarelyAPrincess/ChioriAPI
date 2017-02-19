package com.chiorichan.io;

import com.chiorichan.tasks.Task;
import com.chiorichan.tasks.TaskManager;
import com.chiorichan.tasks.TaskRegistrar;
import com.chiorichan.tasks.Ticks;
import com.chiorichan.tasks.Timings;

import java.io.File;

public abstract class FileWatcher implements TaskRegistrar, Runnable
{
	protected final File fileToWatch;
	private long lastModified = 0;
	private long cycleCoolDown = 1;
	private long lastCheck;
	private Task task;

	public FileWatcher( File fileToWatch )
	{
		this.fileToWatch = fileToWatch;

		task = TaskManager.instance().runTask( this, this );
	}

	@Override
	public final void run()
	{
		boolean changesDetected = false;
		lastCheck = Timings.epoch();

		if ( fileToWatch.exists() )
		{
			long newLastModified = fileToWatch.lastModified();
			changesDetected = newLastModified > lastModified;

			if ( changesDetected )
			{
				lastModified = newLastModified;
				cycleCoolDown = 1;
				readChanges();
			}
		}

		if ( !changesDetected && Ticks.SECOND_5 * cycleCoolDown < Ticks.MINUTE_15 )
			cycleCoolDown++;

		task = TaskManager.instance().runTaskLater( this, Ticks.SECOND_5 * cycleCoolDown, this );
	}

	/**
	 * Resets the cool down period and runs the task sooner
	 */
	public final void reviveTask()
	{
		if ( cycleCoolDown == 1 )
			return;

		cycleCoolDown = 1;
		task.cancel();

		task = TaskManager.instance().runTaskLater( this, Timings.epoch() - lastCheck >= 5 ? 0L : Ticks.SECOND_5 - ( Ticks.SECOND * ( Timings.epoch() - lastCheck ) ), this );
	}

	public abstract void readChanges();


	@Override
	public final boolean isEnabled()
	{
		return true;
	}

	@Override
	public String getName()
	{
		return "FileWatcher";
	}
}

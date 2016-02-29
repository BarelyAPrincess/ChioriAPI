/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.tasks;


/**
 * This class is provided as an easy way to handle scheduling tasks.
 */
public abstract class RunnableTask implements Runnable
{
	private int taskId = -1;

	/**
	 * Attempts to cancel this task.
	 *
	 * @throws IllegalStateException
	 *              if task was not scheduled yet
	 */
	public synchronized void cancel() throws IllegalStateException
	{
		TaskManager.instance().cancelTask( getTaskId() );
	}

	private void checkState()
	{
		if ( taskId != -1 )
			throw new IllegalStateException( "Already scheduled as " + taskId );
	}

	/**
	 * Gets the task id for this runnable.
	 *
	 * @return the task id that this runnable was scheduled as
	 * @throws IllegalStateException
	 *              if task was not scheduled yet
	 */
	public synchronized int getTaskId() throws IllegalStateException
	{
		final int id = taskId;
		if ( id == -1 )
			throw new IllegalStateException( "Not scheduled yet" );
		return id;
	}

	/**
	 * Schedules this in the Chiori scheduler to run on next tick.
	 *
	 * @param creator
	 *             the reference to the plugin scheduling task
	 * @return a ChioriTask that contains the id number
	 * @throws IllegalArgumentException
	 *              if plugin is null
	 * @throws IllegalStateException
	 *              if this was already scheduled
	 * @see TaskManager#runTask(TaskRegistrar, Runnable)
	 */
	public synchronized ITask runTask( TaskRegistrar creator ) throws IllegalArgumentException, IllegalStateException
	{
		checkState();
		return setupId( TaskManager.instance().runTask( creator, this ) );
	}

	/**
	 * <b>Asynchronous tasks should never access any API in Main. Great care should be taken to assure the thread-safety
	 * of asynchronous tasks.</b> <br>
	 * <br>
	 * Schedules this in the Chiori scheduler to run asynchronously.
	 *
	 * @param creator
	 *             the reference to the plugin scheduling task
	 * @return a ChioriTask that contains the id number
	 * @throws IllegalArgumentException
	 *              if plugin is null
	 * @throws IllegalStateException
	 *              if this was already scheduled
	 * @see TaskManager#runTaskAsynchronously(TaskRegistrar, Runnable)
	 */
	public synchronized ITask runTaskAsynchronously( TaskRegistrar creator ) throws IllegalArgumentException, IllegalStateException
	{
		checkState();
		return setupId( TaskManager.instance().runTaskAsynchronously( creator, this ) );
	}

	/**
	 * Schedules this to run after the specified number of server ticks.
	 *
	 * @param creator
	 *             the reference to the plugin scheduling task
	 * @param delay
	 *             the ticks to wait before running the task
	 * @return a ChioriTask that contains the id number
	 * @throws IllegalArgumentException
	 *              if plugin is null
	 * @throws IllegalStateException
	 *              if this was already scheduled
	 * @see TaskManager#runTaskLater(TaskRegistrar, long, Runnable)
	 */
	public synchronized ITask runTaskLater( TaskRegistrar creator, long delay ) throws IllegalArgumentException, IllegalStateException
	{
		checkState();
		return setupId( TaskManager.instance().runTaskLater( creator, delay, this ) );
	}

	/**
	 * <b>Asynchronous tasks should never access any API in Main. Great care should be taken to assure the thread-safety
	 * of asynchronous tasks.</b> <br>
	 * <br>
	 * Schedules this to run asynchronously after the specified number of server ticks.
	 *
	 * @param creator
	 *             the reference to the plugin scheduling task
	 * @param delay
	 *             the ticks to wait before running the task
	 * @return a ChioriTask that contains the id number
	 * @throws IllegalArgumentException
	 *              if plugin is null
	 * @throws IllegalStateException
	 *              if this was already scheduled
	 * @see TaskManager#runTaskLaterAsynchronously(TaskRegistrar, long, Runnable)
	 */
	public synchronized ITask runTaskLaterAsynchronously( TaskRegistrar creator, long delay ) throws IllegalArgumentException, IllegalStateException
	{
		checkState();
		return setupId( TaskManager.instance().runTaskLaterAsynchronously( creator, delay, this ) );
	}

	/**
	 * Schedules this to repeatedly run until cancelled, starting after the specified number of server ticks.
	 *
	 * @param creator
	 *             the reference to the plugin scheduling task
	 * @param delay
	 *             the ticks to wait before running the task
	 * @param period
	 *             the ticks to wait between runs
	 * @return a ChioriTask that contains the id number
	 * @throws IllegalArgumentException
	 *              if plugin is null
	 * @throws IllegalStateException
	 *              if this was already scheduled
	 * @see TaskManager#runTaskTimer(TaskRegistrar, long, long, Runnable)
	 */
	public synchronized ITask runTaskTimer( TaskRegistrar creator, long delay, long period ) throws IllegalArgumentException, IllegalStateException
	{
		checkState();
		return setupId( TaskManager.instance().runTaskTimer( creator, delay, period, this ) );
	}

	/**
	 * <b>Asynchronous tasks should never access any API in Main. Great care should be taken to assure the thread-safety
	 * of asynchronous tasks.</b> <br>
	 * <br>
	 * Schedules this to repeatedly run asynchronously until cancelled, starting after the specified number of server
	 * ticks.
	 *
	 * @param creator
	 *             the reference to the plugin scheduling task
	 * @param delay
	 *             the ticks to wait before running the task for the first time
	 * @param period
	 *             the ticks to wait between runs
	 * @return a ChioriTask that contains the id number
	 * @throws IllegalArgumentException
	 *              if plugin is null
	 * @throws IllegalStateException
	 *              if this was already scheduled
	 * @see TaskManager#runTaskTimerAsynchronously(TaskRegistrar, long, long, Runnable)
	 */
	public synchronized ITask runTaskTimerAsynchronously( TaskRegistrar creator, long delay, long period ) throws IllegalArgumentException, IllegalStateException
	{
		checkState();
		return setupId( TaskManager.instance().runTaskTimerAsynchronously( creator, delay, period, this ) );
	}

	private ITask setupId( final ITask task )
	{
		this.taskId = task.getTaskId();
		return task;
	}
}

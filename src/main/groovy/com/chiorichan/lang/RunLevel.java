/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.lang;

public enum RunLevel
{
	/**
	 * Indicates the application has not done anything YET!
	 */
	INITIALIZATION,
	/**
	 * Indicates the application has begun startup procedures
	 */
	STARTUP,
	/**
	 * Indicates the application has started all and any networking
	 */
	POSTSTARTUP,
	/**
	 * Indicates the application has initialized all manager bases
	 */
	INITIALIZED,
	/**
	 * Indicates the application has completed all required startup procedures and started the main thread tick
	 */
	RUNNING,
	/**
	 * Indicates the application is reloading
	 */
	RELOAD,
	/**
	 * Indicates the application is preparing to shutdown
	 */
	DISPOSED,
	/**
	 * Indicates the application has crashed
	 */
	CRASHED,
	/**
	 * Indicates the application is shutting down but the final state could be either CRASHED or DISPOSED
	 */
	SHUTDOWN
}

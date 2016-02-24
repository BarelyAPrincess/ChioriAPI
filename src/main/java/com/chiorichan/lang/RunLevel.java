/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2015 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
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
	DISPOSED;
}

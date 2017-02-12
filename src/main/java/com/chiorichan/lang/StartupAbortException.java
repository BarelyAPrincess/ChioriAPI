/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.lang;

/**
 * Used to gracefully abort a server startup, e.g., by user interaction.
 */
public class StartupAbortException extends StartupException
{
	private static final long serialVersionUID = -4937198089020390887L;

	public StartupAbortException()
	{
		super( "STARTUP ABORTED!" );
	}
}

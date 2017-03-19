/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.account.event;

import com.chiorichan.account.AccountMeta;

/**
 * Called when a User leaves a server
 */
public class AccountLogoutEvent extends AccountEvent
{
	private String quitMessage;
	
	public AccountLogoutEvent( final AccountMeta acct, final String quitMessage )
	{
		super( acct );
		this.quitMessage = quitMessage;
	}
	
	/**
	 * Gets the quit message to send to all online Users
	 * 
	 * @return string quit message
	 */
	public String getLeaveMessage()
	{
		return quitMessage;
	}
	
	/**
	 * Sets the quit message to send to all online Users
	 * 
	 * @param quitMessage
	 *            quit message
	 */
	public void setLeaveMessage( String quitMessage )
	{
		this.quitMessage = quitMessage;
	}
}

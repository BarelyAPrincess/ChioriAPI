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

import com.chiorichan.account.AccountContext;
import com.chiorichan.account.lang.AccountDescriptiveReason;
import com.chiorichan.event.Conditional;
import com.chiorichan.event.EventException;
import com.chiorichan.event.RegisteredListener;

/**
 * Used to lookup accounts
 */
public class AccountLookupEvent extends AccountEvent implements Conditional
{
	private AccountContext context = null;
	private AccountDescriptiveReason reason = AccountDescriptiveReason.DEFAULT;
	private Throwable cause = null;
	private String acctId;
	
	public AccountLookupEvent( String acctId )
	{
		this.acctId = acctId;
	}
	
	@Override
	public boolean conditional( RegisteredListener context ) throws EventException
	{
		if ( cause != null )
			reason = AccountDescriptiveReason.INTERNAL_ERROR;
		
		return !reason.getReportingLevel().isSuccess() && reason.getReportingLevel().isIgnorable();
	}
	
	public String getAcctId()
	{
		return acctId;
	}
	
	public Throwable getCause()
	{
		return cause;
	}
	
	public AccountContext getContext()
	{
		return context;
	}
	
	public AccountDescriptiveReason getDescriptiveReason()
	{
		return reason;
	}
	
	public boolean hasCause()
	{
		return cause != null;
	}
	
	public AccountLookupEvent setCause( Throwable cause )
	{
		this.cause = cause;
		return this;
	}
	
	public AccountLookupEvent setResult( AccountContext context, AccountDescriptiveReason reason )
	{
		this.context = context;
		this.reason = reason;
		return this;
	}
}

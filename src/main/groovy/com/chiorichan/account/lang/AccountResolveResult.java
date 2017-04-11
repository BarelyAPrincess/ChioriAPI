/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.account.lang;

import com.chiorichan.account.AccountContext;

public class AccountResolveResult
{
	private AccountContext context = null;
	private AccountDescriptiveReason reason = AccountDescriptiveReason.DEFAULT;
	private Throwable cause = null;

	public AccountResolveResult( AccountContext context, AccountDescriptiveReason reason )
	{
		this.context = context;
		this.reason = reason;
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
		return cause != null ? AccountDescriptiveReason.INTERNAL_ERROR : reason;
	}

	public boolean hasCause()
	{
		return cause != null;
	}

	public AccountResolveResult setCause( Throwable cause )
	{
		this.cause = cause;
		return this;
	}
}

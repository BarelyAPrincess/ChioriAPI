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

import org.apache.commons.lang3.Validate;

import com.chiorichan.account.AccountManager;
import com.chiorichan.account.AccountMeta;
import com.chiorichan.lang.ReportingLevel;

/**
 * Typically provides the results of an attempted account login
 */
public class AccountResult
{
	private AccountDescriptiveReason reason = AccountDescriptiveReason.DEFAULT;
	private String acctId;
	private AccountMeta acct;
	private Throwable cause;

	public AccountResult( AccountMeta acct )
	{
		Validate.notNull( acct );

		acctId = acct.getId();
		this.acct = acct;
	}

	public AccountResult( AccountMeta acct, AccountDescriptiveReason reason )
	{
		Validate.notNull( acct );
		Validate.notNull( reason );

		acctId = acct.getId();
		this.reason = reason;
		this.acct = acct;
	}

	public AccountResult( String acctId )
	{
		Validate.notNull( acctId );

		this.acctId = acctId;
		acct = null;
	}

	public AccountResult( String acctId, AccountDescriptiveReason reason )
	{
		Validate.notNull( acctId );
		Validate.notNull( reason );

		this.acctId = acctId;
		this.reason = reason;
		acct = null;
	}

	/**
	 * @return AccountException using the currently set LoginDescriptiveReason
	 */
	public AccountException exception()
	{
		return new AccountException( reason, this );
	}

	/**
	 * @param reason
	 *             The new LoginDescriptiveReason
	 * @return AccountException using the provided LoginDescriptiveReason
	 */
	public AccountException exception( AccountDescriptiveReason reason )
	{
		this.reason = reason;
		return new AccountException( reason, this );
	}

	/**
	 * @param reason
	 *             Custom reason
	 * @param level
	 *             The desired ReportingLevel
	 * @return AccountException using a custom LoginDescriptiveReason
	 */
	public AccountException exception( String reason, ReportingLevel level )
	{
		this.reason = new AccountDescriptiveReason( reason, level );
		return new AccountException( this.reason, this );
	}

	public AccountMeta getAccount()
	{
		if ( acct == null )
			try
			{
				acct = AccountManager.instance().getAccountWithException( acctId );
			}
			catch ( AccountException e )
			{
				e.getReason();
				cause = e;
				return null;
			}

		return acct;
	}

	public AccountMeta getAccountWithException() throws AccountException
	{
		if ( acct == null )
			try
			{
				acct = AccountManager.instance().getAccountWithException( acctId );
			}
			catch ( AccountException e )
			{
				e.getReason();
				cause = e;
				throw e;
			}

		return acct;
	}

	public String getAcctId()
	{
		return acctId;
	}

	public Throwable getCause()
	{
		return cause;
	}

	public AccountDescriptiveReason getDescriptiveReason()
	{
		return reason;
	}

	public String getFormattedMessage()
	{
		String ip = acct != null && acct.instance().getIpAddresses().size() > 0 ? acct.instance().getIpAddresses().toArray( new String[0] )[0] : null;
		return String.format( reason.getMessage(), acct == null ? acctId : acct.getDisplayName(), ip );
	}

	public ReportingLevel getLevel()
	{
		return reason.getReportingLevel();
	}

	public String getMessage()
	{
		return reason.getMessage();
	}

	public boolean hasCause()
	{
		return cause != null;
	}

	public boolean isIgnorable()
	{
		return reason.getReportingLevel().isIgnorable();
	}

	public boolean isSuccess()
	{
		return reason.getReportingLevel() == ReportingLevel.L_SUCCESS;
	}

	public AccountResult setAccount( AccountMeta acct )
	{
		this.acct = acct;
		return this;
	}

	public AccountResult setCause( Throwable cause )
	{
		this.cause = cause;
		return this;
	}

	public AccountResult setError( ReportingLevel level )
	{
		reason = new AccountDescriptiveReason( reason.getMessage(), level );
		return this;
	}

	public AccountResult setMessage( String msg )
	{
		reason = new AccountDescriptiveReason( msg, reason.getReportingLevel() );
		return this;
	}

	public AccountResult setReason( AccountDescriptiveReason reason )
	{
		this.reason = reason;
		return this;
	}

	@Override
	public String toString()
	{
		return String.format( "AccountResult{msg=%s,reportingLevel=%s}", getFormattedMessage(), getLevel() );
	}
}
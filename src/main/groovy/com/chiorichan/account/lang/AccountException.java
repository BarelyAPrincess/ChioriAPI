/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package com.chiorichan.account.lang;

import com.chiorichan.account.AccountMeta;
import com.chiorichan.lang.ApplicationException;
import com.chiorichan.lang.ExceptionContext;
import com.chiorichan.lang.ExceptionReport;
import com.chiorichan.lang.ReportingLevel;
import com.chiorichan.utils.UtilObjects;

/**
 * Used to pass login errors to the requester.
 */
public class AccountException extends ApplicationException
{
	private static final long serialVersionUID = 5522301956671473324L;

	private AccountDescriptiveReason reason;
	private AccountResult result;

	public AccountException( AccountDescriptiveReason reason, AccountMeta meta )
	{
		this( reason, meta.getLocId(), meta.getId() );
	}

	public AccountException( AccountResult result )
	{
		super( ReportingLevel.L_ERROR, result.getMessage() );

		UtilObjects.notNull( result );

		if ( result.getCause() != null )
			initCause( result.getCause() );

		this.reason = result.getDescriptiveReason();
		this.result = result;
	}

	public AccountException( AccountDescriptiveReason reason, AccountResult result )
	{
		super( ReportingLevel.L_ERROR, reason.getMessage() );

		UtilObjects.notNull( result );

		if ( result.getCause() != null )
			initCause( result.getCause() );

		result.setReason( reason );
		this.reason = reason;
		this.result = result;
	}

	public AccountException( AccountDescriptiveReason reason, String locId, String acctId )
	{
		super( ReportingLevel.L_ERROR, reason.getMessage() );

		if ( reason == AccountDescriptiveReason.INTERNAL_ERROR )
			throw new IllegalArgumentException( "Wrong Constructor: AccountDescriptiveReason was an INTERNAL_ERROR but no cause was specified." );

		this.reason = reason;

		result = new AccountResult( locId, acctId, reason );
	}

	public AccountException( AccountDescriptiveReason reason, Throwable cause, AccountMeta meta )
	{
		this( reason, cause, meta.getLocId(), meta.getId() );
	}

	public AccountException( AccountDescriptiveReason reason, Throwable cause, AccountResult result )
	{
		super( ReportingLevel.L_ERROR, cause );

		UtilObjects.notNull( reason );
		UtilObjects.notNull( cause );
		UtilObjects.notNull( result );

		result.setCause( cause );

		this.reason = reason;
		this.result = result;
	}

	public AccountException( AccountDescriptiveReason reason, Throwable cause, String locId, String acctId )
	{
		this( reason, cause, new AccountResult( locId, acctId ) );
	}

	public AccountException( Throwable cause, AccountMeta meta )
	{
		this( cause, meta.getLocId(), meta.getId() );
	}

	public AccountException( Throwable cause, AccountResult result )
	{
		super( ReportingLevel.L_ERROR, cause );

		UtilObjects.notNull( cause );
		UtilObjects.notNull( result );

		result.setCause( cause );

		reason = new AccountDescriptiveReason( cause.getMessage(), ReportingLevel.L_ERROR );
		this.result = result;
	}

	public AccountException( Throwable cause, String locId, String acctId )
	{
		this( cause, new AccountResult( locId, acctId ) );
	}

	public AccountMeta getAccount()
	{
		return result.getAccount();
	}

	public String getAcctId()
	{
		return result.getAcctId();
	}

	public AccountDescriptiveReason getReason()
	{
		return reason;
	}

	public AccountResult getResult()
	{
		if ( !result.isIgnorable() )
			result = result.setCause( this );

		return result;
	}

	@Override
	public Throwable getCause()
	{
		return super.getCause();// super.getCause() == null ? result.getCause() : super.getCause();
	}

	public boolean hasCause()
	{
		return getCause() != null;
	}

	@Override
	public ReportingLevel handle( ExceptionReport report, ExceptionContext context )
	{
		return null;
	}
}

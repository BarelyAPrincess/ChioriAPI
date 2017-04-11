/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.event.account;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import com.chiorichan.account.AccountManager;
import com.chiorichan.account.AccountMeta;
import com.chiorichan.account.AccountType;
import com.chiorichan.account.Kickable;
import com.chiorichan.account.lang.AccountDescriptiveReason;
import com.chiorichan.account.lang.AccountResult;
import com.chiorichan.event.Cancellable;
import com.chiorichan.event.EventBus;
import com.chiorichan.event.SelfHandling;
import com.google.common.collect.Sets;

/**
 * Called when a User gets kicked from the server
 */
public class KickEvent extends AccountEvent implements Cancellable, SelfHandling
{
	public static KickEvent kick( AccountMeta kicker, Collection<Kickable> kickables )
	{
		Validate.notNull( kickables );
		return new KickEvent().setKicker( kicker ).setKickables( kickables );
	}

	public static KickEvent kick( AccountMeta kicker, Kickable... kickables )
	{
		return new KickEvent().setKicker( kicker ).setKickables( Arrays.asList( kickables ) );
	}

	private String leaveMessage;
	private String kickReason;
	private final Set<Kickable> kickables = Sets.newHashSet();

	private AccountResult result = new AccountResult( AccountType.ACCOUNT_NONE );

	private Boolean cancel = false;

	private KickEvent()
	{

	}

	public KickEvent addKickables( Collection<Kickable> kickables )
	{
		Validate.notNull( kickables );
		this.kickables.addAll( kickables );
		return this;
	}

	public AccountResult fire()
	{
		EventBus.instance().callEvent( this );
		return result;
	}

	/**
	 * Gets the leave message send to all online Users
	 *
	 * @return string kick reason
	 */
	public String getLeaveMessage()
	{
		return leaveMessage;
	}

	/**
	 * Gets the reason why the User is getting kicked
	 *
	 * @return string kick reason
	 */
	public String getReason()
	{
		return kickReason;
	}

	public AccountResult getResult()
	{
		return result;
	}

	@Override
	public void handle()
	{
		if ( isCancelled() )
			return;

		Set<String> kicked = new HashSet<>();

		for ( Kickable kickable : kickables )
			if ( !kicked.contains( kickable.getId() ) )
			{
				kicked.add( kickable.getId() );
				AccountResult outcome = kickable.kick( kickReason );
				if ( !outcome.isSuccess() )
					AccountManager.getLogger().warning( String.format( "We failed to kick `%s` with reason `%s`", kickable.getId(), outcome.getMessage() ) );
			}

		result.setReason( AccountDescriptiveReason.SUCCESS );
	}

	@Override
	public boolean isCancelled()
	{
		return cancel;
	}

	@Override
	public void setCancelled( boolean cancel )
	{
		this.cancel = cancel;
		if ( cancel )
			result.setReason( AccountDescriptiveReason.CANCELLED_BY_EVENT );
	}

	public KickEvent setKickables( Collection<Kickable> kickables )
	{
		Validate.notNull( kickables );
		this.kickables.clear();
		addKickables( kickables );
		return this;
	}

	private KickEvent setKicker( AccountMeta kicker )
	{
		result.setAccount( kicker );
		return this;
	}

	/**
	 * Sets the leave message send to all online Users
	 *
	 * @param leaveMessage
	 *             leave message
	 */
	public KickEvent setLeaveMessage( String leaveMessage )
	{
		this.leaveMessage = leaveMessage;
		return this;
	}

	/**
	 * Sets the reason why the User is getting kicked
	 *
	 * @param kickReason
	 *             kick reason
	 */
	public KickEvent setReason( String kickReason )
	{
		this.kickReason = kickReason;
		return this;
	}
}

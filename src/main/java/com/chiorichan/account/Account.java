/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.account;

import com.chiorichan.account.lang.AccountException;
import com.chiorichan.messaging.MessageChannel;
import com.chiorichan.messaging.MessageSender;
import com.chiorichan.permission.PermissibleEntity;
import com.chiorichan.services.ProviderChild;

public interface Account extends MessageSender, MessageChannel, ProviderChild
{
	/**
	 * Returns the exact instance of AccountMeta
	 *
	 * @return {@link AccountMeta} instance of this Account
	 */
	AccountMeta meta();

	/**
	 * Returns the exact instance of AccountMeta
	 *
	 * @return {@link AccountInstance} instance of this Account
	 * @throws AccountException
	 */
	AccountInstance instance();

	/**
	 * Returns the AcctId for this Account
	 *
	 * @return The AcctId
	 */
	@Override
	String getId();

	/**
	 * Returns the {@link AccountLocation} associated with this account
	 *
	 * @return The associated {@link AccountLocation}
	 */
	public AccountLocation getLocation();

	/**
	 * Compiles a human readable display name, e.g., John Smith
	 *
	 * @return A human readable display name
	 */
	@Override
	String getDisplayName();

	/**
	 * Returns the PermissibleEntity for this Account
	 *
	 * @return The PermissibleEntity
	 */
	@Override
	PermissibleEntity getEntity();

	boolean isInitialized();
}

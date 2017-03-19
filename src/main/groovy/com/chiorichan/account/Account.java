/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.account;

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
	AccountLocation getLocation();

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

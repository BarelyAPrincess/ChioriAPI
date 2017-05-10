/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.messaging;

import com.chiorichan.permission.PermissibleEntity;

/**
 * Represents entities with the ability to send messages thru the {@link MessageDispatch}
 */
public interface MessageSender
{
	String getDisplayName();
	
	String getId();
	
	PermissibleEntity getPermissibleEntity();
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan;

import com.chiorichan.event.EventHandler;
import com.chiorichan.event.EventPriority;
import com.chiorichan.event.application.RunlevelEvent;
import com.chiorichan.lang.ApplicationException;

public class SimpleLoader extends AppLoader
{
	@Override
	@EventHandler( priority = EventPriority.NORMAL )
	public void onRunlevelChange( RunlevelEvent event ) throws ApplicationException
	{

	}
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.services;

import com.chiorichan.lang.ApplicationException;
import com.chiorichan.logger.LogSource;

public interface ServiceManager extends LogSource
{
	void init() throws ApplicationException;
}

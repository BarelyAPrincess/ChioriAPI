/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Used for when multiple exceptions were thrown
 */
public class MultipleException extends Exception
{
	private static final long serialVersionUID = -659541886519281396L;

	private final List<IException> exceptions = new ArrayList<>();

	public MultipleException( List<IException> exceptions )
	{
		for ( IException e : exceptions )
			if ( ! ( e instanceof Throwable ) )
				throw new IllegalArgumentException( "IException must be implemented on Throwables only, this is a serious programming bug!" );

		this.exceptions.addAll( exceptions );
	}

	public List<IException> getExceptions()
	{
		return Collections.unmodifiableList( exceptions );
	}
}

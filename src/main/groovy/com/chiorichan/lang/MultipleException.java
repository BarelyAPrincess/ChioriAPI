/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
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

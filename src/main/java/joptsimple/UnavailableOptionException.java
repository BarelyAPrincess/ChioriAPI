/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple;

import java.util.List;

/**
 * Thrown when options marked as allowed are specified on the command line, but the options they depend upon are
 * present/not present.
 */
class UnavailableOptionException extends OptionException
{
	private static final long serialVersionUID = -1L;

	UnavailableOptionException( List<? extends OptionSpec<?>> forbiddenOptions )
	{
		super( forbiddenOptions );
	}

	@Override
	Object[] messageArguments()
	{
		return new Object[] {multipleOptionString()};
	}
}

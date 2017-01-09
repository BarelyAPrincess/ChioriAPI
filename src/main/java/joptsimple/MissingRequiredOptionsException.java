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
 * Thrown when options marked as required are not specified on the command line.
 *
 * @author <a href="https://github.com/TC1">Emils Solmanis</a>
 */
class MissingRequiredOptionsException extends OptionException
{
	private static final long serialVersionUID = -1L;

	protected MissingRequiredOptionsException( List<? extends OptionSpec<?>> missingRequiredOptions )
	{
		super( missingRequiredOptions );
	}

	@Override
	Object[] messageArguments()
	{
		return new Object[] {multipleOptionString()};
	}
}

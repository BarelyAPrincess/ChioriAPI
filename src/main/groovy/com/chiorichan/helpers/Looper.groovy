/*
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.helpers

/**
 * Provides do... until/while ability to groovy scripts
 * Which as of Jan 2014 was not implemented into groovy*/
public class Looper
{
	private Closure code

	static Looper go( Closure code )
	{
		new Looper( code: code )
	}

	void until( Closure test )
	{
		code()
		while ( !test() )
		{
			code()
		}
	}

	void while0( Closure test )
	{
		code()
		while ( test() )
		{
			code()
		}
	}
}

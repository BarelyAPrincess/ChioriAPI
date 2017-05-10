/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.helpers;

public enum ANIState
{
	Always, Never, Ignore;

	public static ANIState parse( String value, ANIState def )
	{
		try
		{
			return parse( value );
		}
		catch ( IllegalArgumentException e )
		{
			return def;
		}
	}

	public static ANIState parse( String value )
	{
		if ( value == null || value.length() == 0 || value.equalsIgnoreCase( "ignore" ) )
			return Ignore;
		if ( value.equalsIgnoreCase( "disallow" ) || value.equalsIgnoreCase( "deny" ) || value.equalsIgnoreCase( "disabled" ) || value.equalsIgnoreCase( "never" ) )
			return Never;
		if ( value.equalsIgnoreCase( "force" ) || value.equalsIgnoreCase( "allow" ) || value.equalsIgnoreCase( "enabled" ) || value.equalsIgnoreCase( "always" ) )
			return Always;
		throw new IllegalArgumentException( String.format( "Value %s is invalid, the available options are Ignore, Disallow, Deny, Disabled, Never, Force, Allow, Enabled, Always.", value ) );
	}
}

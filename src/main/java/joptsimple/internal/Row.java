/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple.internal;

/**
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class Row
{
	final String option;
	final String description;

	Row( String option, String description )
	{
		this.option = option;
		this.description = description;
	}

	@Override
	public boolean equals( Object that )
	{
		if ( that == this )
			return true;
		if ( that == null || !getClass().equals( that.getClass() ) )
			return false;

		Row other = ( Row ) that;
		return option.equals( other.option ) && description.equals( other.description );
	}

	@Override
	public int hashCode()
	{
		return option.hashCode() ^ description.hashCode();
	}
}

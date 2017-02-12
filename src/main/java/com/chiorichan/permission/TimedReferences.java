/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.permission;

import com.chiorichan.tasks.Timings;

public class TimedReferences extends References
{
	int lifeTime;
	
	public TimedReferences( int lifeTime )
	{
		if ( lifeTime < 1 )
			this.lifeTime = -1;
		else
			this.lifeTime = Timings.epoch() + lifeTime;
	}
	
	@Override
	public TimedReferences add( References refs )
	{
		super.add( refs );
		return this;
	}
	
	@Override
	public TimedReferences add( String... refs )
	{
		super.add( refs );
		return this;
	}
	
	public boolean isExpired()
	{
		return lifeTime > 0 && ( lifeTime - Timings.epoch() < 0 );
	}
	
	@Override
	public TimedReferences remove( References refs )
	{
		super.remove( refs );
		return this;
	}
	
	@Override
	public TimedReferences remove( String... refs )
	{
		super.remove( refs );
		return this;
	}
}

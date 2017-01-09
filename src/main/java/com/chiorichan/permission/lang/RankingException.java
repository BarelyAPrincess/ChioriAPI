/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.permission.lang;

import com.chiorichan.permission.PermissibleEntity;

public class RankingException extends PermissionException
{
	private static final long serialVersionUID = -328357153481259189L;
	
	protected PermissibleEntity target = null;
	protected PermissibleEntity promoter = null;
	
	public RankingException( String message, PermissibleEntity target, PermissibleEntity promoter )
	{
		super( message );
		this.target = target;
		this.promoter = promoter;
	}
	
	public PermissibleEntity getTarget()
	{
		return target;
	}
	
	public PermissibleEntity getPromoter()
	{
		return promoter;
	}
}

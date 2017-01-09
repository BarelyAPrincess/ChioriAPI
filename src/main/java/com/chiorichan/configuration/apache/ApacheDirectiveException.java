/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.configuration.apache;

public class ApacheDirectiveException extends Exception
{
	private final ApacheDirective directive;

	public ApacheDirectiveException( String reason )
	{
		super( reason );
		directive = null;
	}

	public ApacheDirectiveException( String reason, ApacheDirective directive )
	{
		super( reason );
		this.directive = directive;
	}

	public int getLineNumber()
	{
		return directive != null ? directive.lineNum : -1;
	}

	public String getSource()
	{
		return directive != null ? directive.source : null;
	}
}

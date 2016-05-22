/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
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

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

/**
 * A specification for an option that does not accept arguments.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class NoArgumentOptionSpec extends AbstractOptionSpec<Void>
{
	NoArgumentOptionSpec( List<String> options, String description )
	{
		super( options, description );
	}

	NoArgumentOptionSpec( String option )
	{
		this( singletonList( option ), "" );
	}

	@Override
	public boolean acceptsArguments()
	{
		return false;
	}

	@Override
	public String argumentDescription()
	{
		return "";
	}

	@Override
	public String argumentTypeIndicator()
	{
		return "";
	}

	@Override
	protected Void convert( String argument )
	{
		return null;
	}

	@Override
	public List<Void> defaultValues()
	{
		return emptyList();
	}

	@Override
	void handleOption( OptionParser parser, ArgumentList arguments, OptionSet detectedOptions, String detectedArgument )
	{

		detectedOptions.add( this );
	}

	@Override
	public boolean isRequired()
	{
		return false;
	}

	@Override
	public boolean requiresArgument()
	{
		return false;
	}
}

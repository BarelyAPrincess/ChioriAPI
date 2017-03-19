/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple;

import static java.util.Collections.singletonList;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;
import static joptsimple.internal.Strings.EMPTY;

import java.util.ArrayList;
import java.util.List;

import joptsimple.internal.Reflection;
import joptsimple.internal.ReflectionException;

/**
 * @param <V>
 *             represents the type of the arguments this option accepts
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public abstract class AbstractOptionSpec<V> implements OptionSpec<V>, OptionDescriptor
{
	private final List<String> options = new ArrayList<>();
	private final String description;
	private boolean forHelp;

	AbstractOptionSpec( List<String> options, String description )
	{
		arrangeOptions( options );

		this.description = description;
	}

	AbstractOptionSpec( String option )
	{
		this( singletonList( option ), EMPTY );
	}

	protected String argumentTypeIndicatorFrom( ValueConverter<V> converter )
	{
		if ( converter == null )
			return null;

		String pattern = converter.valuePattern();
		return pattern == null ? converter.valueType().getName() : pattern;
	}

	private void arrangeOptions( List<String> unarranged )
	{
		if ( unarranged.size() == 1 )
		{
			options.addAll( unarranged );
			return;
		}

		List<String> shortOptions = new ArrayList<>();
		List<String> longOptions = new ArrayList<>();

		for ( String each : unarranged )
			if ( each.length() == 1 )
				shortOptions.add( each );
			else
				longOptions.add( each );

		sort( shortOptions );
		sort( longOptions );

		options.addAll( shortOptions );
		options.addAll( longOptions );
	}

	protected abstract V convert( String argument );

	protected V convertWith( ValueConverter<V> converter, String argument )
	{
		try
		{
			return Reflection.convertWith( converter, argument );
		}
		catch ( ReflectionException | ValueConversionException ex )
		{
			throw new OptionArgumentConversionException( this, argument, ex );
		}
	}

	@Override
	public String description()
	{
		return description;
	}

	@Override
	public boolean equals( Object that )
	{
		if ( ! ( that instanceof AbstractOptionSpec<?> ) )
			return false;

		AbstractOptionSpec<?> other = ( AbstractOptionSpec<?> ) that;
		return options.equals( other.options );
	}

	public final AbstractOptionSpec<V> forHelp()
	{
		forHelp = true;
		return this;
	}

	abstract void handleOption( OptionParser parser, ArgumentList arguments, OptionSet detectedOptions, String detectedArgument );

	@Override
	public int hashCode()
	{
		return options.hashCode();
	}

	@Override
	public final boolean isForHelp()
	{
		return forHelp;
	}

	@Override
	public final List<String> options()
	{
		return unmodifiableList( options );
	}

	@Override
	public boolean representsNonOptions()
	{
		return false;
	}

	@Override
	public String toString()
	{
		return options.toString();
	}

	@Override
	public final V value( OptionSet detectedOptions )
	{
		return detectedOptions.valueOf( this );
	}

	@Override
	public final List<V> values( OptionSet detectedOptions )
	{
		return detectedOptions.valuesOf( this );
	}
}

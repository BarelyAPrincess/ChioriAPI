/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

/**
 * Handles the context sources for new objects, e.g., Plugins, EventRegistration, and TaskCreation
 */
public class ObjectContext
{
	public enum SourceFlags
	{

	}

	private final Object source;
	private final String name;
	private final List<SourceFlags> flags;

	public ObjectContext( Object source, SourceFlags... flags )
	{
		this.source = source;
		this.flags = Arrays.asList( flags );

		name = ClassUtils.getSimpleName( source.getClass() );

		/*
		 * if ( source instanceof EventRegistrar )
		 * if ( !creator.isEnabled() )
		 * throw new IllegalCreatorAccessException( "EventCreator attempted to register " + listener + " while not enabled" );
		 *
		 * if ( source instanceof TaskRegistrar )
		 * {
		 *
		 * }
		 *
		 * if ( source instanceof Plugin )
		 * {
		 *
		 * }
		 */
	}

	public List<String> getAuthors()
	{
		// TODO Implement this
		return Arrays.asList( "Chiori-chan" );
	}

	public List<SourceFlags> getFlags()
	{
		return Collections.unmodifiableList( flags );
	}

	public String getFullName()
	{
		return getName();
	}

	public String getName()
	{
		return name;
	}

	public Object getSource()
	{
		return source;
	}

	public boolean isEnabled()
	{
		// Implement this!

		return true;
	}
}

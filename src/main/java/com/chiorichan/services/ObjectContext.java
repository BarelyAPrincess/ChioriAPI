/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
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
		return Arrays.asList( new String[] {"Chiori-chan"} );
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

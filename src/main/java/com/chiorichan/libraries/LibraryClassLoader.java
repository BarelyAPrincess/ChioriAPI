/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.libraries;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * Acts as the classloader for downloaded Maven Libraries
 */

@SuppressWarnings( {"unchecked", "rawtypes"} )
public class LibraryClassLoader
{
	private static final Class[] parameters = new Class[] {URL.class};

	public static void addPath( File f ) throws IOException
	{
		addPath( f.toURI().toURL() );
	}

	public static void addPath( String s ) throws IOException
	{
		addPath( new File( s ) );
	}

	public static void addPath( URL u ) throws IOException
	{
		URLClassLoader sysloader = ( URLClassLoader ) ClassLoader.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;

		try
		{
			Method method = sysclass.getDeclaredMethod( "addURL", parameters );
			method.setAccessible( true );
			method.invoke( sysloader, new Object[] {u} );
		}
		catch ( Throwable t )
		{
			throw new IOException( String.format( "Error, could not add path '%s' to system classloader", u.toString() ), t );
		}

	}

	public static boolean pathLoaded( File f ) throws MalformedURLException
	{
		return pathLoaded( f.toURI().toURL() );
	}

	public static boolean pathLoaded( String s ) throws MalformedURLException
	{
		return pathLoaded( new File( s ) );
	}

	public static boolean pathLoaded( URL u )
	{
		URLClassLoader sysloader = ( URLClassLoader ) ClassLoader.getSystemClassLoader();
		return Arrays.asList( sysloader.getURLs() ).contains( u );
	}
}

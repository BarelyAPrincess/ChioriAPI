/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.libraries;

import com.chiorichan.AppConfig;
import com.chiorichan.DeployWrapper;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.lang.ReportingLevel;
import com.chiorichan.lang.UncaughtException;
import com.chiorichan.logger.Log;
import com.chiorichan.logger.LogSource;
import com.chiorichan.utils.UtilHttp;
import com.chiorichan.utils.UtilIO;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used as a helper class for retrieving files from the central maven repository
 */
public class Libraries implements LibrarySource, LogSource
{
	public static final String BASE_MAVEN_URL = "http://jcenter.bintray.com/";
	public static final String BASE_MAVEN_URL_ALT = "http://search.maven.org/remotecontent?filepath=";
	public static final File INCLUDES_DIR;
	public static final File LIBRARY_DIR;
	public static Map<String, MavenReference> loadedLibraries = new HashMap<>();

	public static final Libraries SELF = new Libraries();

	static
	{
		LIBRARY_DIR = DeployWrapper.isDeployment() ? new File( "libraries" ) : AppConfig.get().getDirectory( "lib", "libraries" );
		INCLUDES_DIR = new File( LIBRARY_DIR, "local" );

		if ( !UtilIO.setDirectoryAccess( LIBRARY_DIR ) )
			throw new UncaughtException( ReportingLevel.E_ERROR, "This application experienced a problem setting read and write access to directory \"" + UtilIO.relPath( LIBRARY_DIR ) + "\"!" );

		if ( !UtilIO.setDirectoryAccess( INCLUDES_DIR ) )
			throw new UncaughtException( ReportingLevel.E_ERROR, "This application experienced a problem setting read and write access to directory \"" + UtilIO.relPath( INCLUDES_DIR ) + "\"!" );

		addLoaded( "org.fusesource.jansi:jansi:1.11" );
		addLoaded( "net.sf.jopt-simple:jopt-simple:4.7" );
		addLoaded( "org.codehaus.groovy:groovy-all:2.3.6" );
		addLoaded( "io.netty:netty-all:5.0.0.Alpha1" );
		addLoaded( "mysql:mysql-connector-java:5.1.32" );
		addLoaded( "org.xerial:sqlite-jdbc:3.6.16" );
		addLoaded( "com.google.guava:guava:17.0" );
		addLoaded( "org.apache.commons:commons-lang3:3.3.2" );
		addLoaded( "commons-io:commons-io:2.4" );
		addLoaded( "commons-net:commons-net:3.3" );
		addLoaded( "commons-codec:commons-codec:1.9" );
		addLoaded( "org.yaml:snakeyaml:1.13" );
		addLoaded( "com.google.javascript:closure-compiler:r2388" );
		addLoaded( "org.mozilla:rhino:1.7R4" );
		addLoaded( "com.asual.lesscss:lesscss-engine:1.3.0" );
		addLoaded( "joda-time:joda-time:2.7" );
		addLoaded( "com.googlecode.libphonenumber:libphonenumber:7.0.4" );
		addLoaded( "com.google.code.gson:gson:2.3" );
		addLoaded( "org.apache.httpcomponents:fluent-hc:4.3.5" );

		// Scans the 'libraries/local' folder for jar files that need to loaded into the classpath
		for ( File f : INCLUDES_DIR.listFiles( new FilenameFilter()
		{
			@Override
			public boolean accept( File dir, String name )
			{
				return name.toLowerCase().endsWith( "jar" );
			}
		} ) )
			loadLibrary( f );
	}

	public static void addLoaded( String library )
	{
		try
		{
			MavenReference ref = new MavenReference( "builtin", library );
			loadedLibraries.put( ref.getKey(), ref );
		}
		catch ( IllegalArgumentException e )
		{
			// Do Nothing
		}
	}

	public static File getLibraryDir()
	{
		return LIBRARY_DIR;
	}

	public static List<MavenReference> getLoadedLibraries()
	{
		return new ArrayList<MavenReference>( loadedLibraries.values() );
	}

	public static List<MavenReference> getLoadedLibrariesBySource( LibrarySource source )
	{
		List<MavenReference> references = new ArrayList<>();

		for ( MavenReference ref : loadedLibraries.values() )
			if ( ref.getSource() == source )
				references.add( ref );

		return references;
	}

	public static MavenReference getReferenceByGroup( String group )
	{
		Validate.notNull( group );
		for ( MavenReference ref : loadedLibraries.values() )
			if ( group.equalsIgnoreCase( ref.getGroup() ) )
				return ref;
		return null;
	}

	public static MavenReference getReferenceByName( String name )
	{
		Validate.notNull( name );
		for ( MavenReference ref : loadedLibraries.values() )
			if ( name.equalsIgnoreCase( ref.getName() ) )
				return ref;
		return null;
	}

	public static boolean isLoaded( MavenReference lib )
	{
		return loadedLibraries.containsKey( lib.getKey() );
	}

	public static boolean loadLibrary( File lib )
	{
		if ( lib == null || !lib.exists() )
			return false;

		Log.get( SELF ).info( ( Log.useColor() ? EnumColor.GRAY : "" ) + "Loading the library `" + lib.getName() + "`" );

		try
		{
			LibraryClassLoader.addPath( lib );
		}
		catch ( Throwable t )
		{
			t.printStackTrace();
			return false;
		}

		try
		{
			UtilIO.extractNatives( lib, lib.getParentFile() );
		}
		catch ( IOException e )
		{
			Log.get( SELF ).severe( "We had a problem trying to extract native libraries from jar file '" + lib.getAbsolutePath() + "'", e );
		}

		return true;
	}

	public static boolean loadLibrary( MavenReference lib )
	{
		String urlJar = lib.mavenUrl( "jar" );
		String urlPom = lib.mavenUrl( "pom" );

		File mavenLocalJar = lib.jarFile();
		File mavenLocalPom = lib.pomFile();

		if ( urlJar == null || urlJar.isEmpty() || urlPom == null || urlPom.isEmpty() )
			return false;

		try
		{
			if ( !mavenLocalPom.exists() || !mavenLocalJar.exists() )
			{
				Log.get( SELF ).info( ( Log.useColor() ? EnumColor.GOLD : "" ) + "Downloading the library `" + lib.toString() + "` from url `" + urlJar + "`... Please Wait!" );

				// Try download from JCenter Bintray Maven Repository
				try
				{
					UtilHttp.downloadFile( urlPom, mavenLocalPom );
					UtilHttp.downloadFile( urlJar, mavenLocalJar );
				}
				catch ( IOException e )
				{
					// Try download from alternative Maven Central Repository
					String urlJarAlt = lib.mavenUrlAlt( "jar" );
					String urlPomAlt = lib.mavenUrlAlt( "pom" );

					Log.get( SELF ).warning( "Primary download location failed, trying secondary location `" + urlJarAlt + "`... Please Wait!" );

					try
					{
						UtilHttp.downloadFile( urlPomAlt, mavenLocalPom );
						UtilHttp.downloadFile( urlJarAlt, mavenLocalJar );
					}
					catch ( IOException ee )
					{
						Log.get( SELF ).severe( "Primary and secondary download location have FAILED!" );
						return false;
					}
				}
			}

			Log.get( SELF ).info( ( Log.useColor() ? EnumColor.DARK_GRAY : "" ) + "Loading the library `" + lib.toString() + "` from file `" + mavenLocalJar + "`..." );

			LibraryClassLoader.addPath( mavenLocalJar );
		}
		catch ( Throwable t )
		{
			t.printStackTrace();
			return false;
		}

		loadedLibraries.put( lib.getKey(), lib );
		try
		{
			UtilIO.extractNatives( lib.jarFile(), lib.baseDir() );
		}
		catch ( IOException e )
		{
			Log.get( SELF ).severe( "We had a problem trying to extract native libraries from jar file '" + lib.jarFile() + "'", e );
		}

		return true;
	}

	private Libraries()
	{

	}

	@Override
	public String getLoggerId()
	{
		return "LibMgr";
	}

	@Override
	public String getName()
	{
		return "builtin";
	}
}

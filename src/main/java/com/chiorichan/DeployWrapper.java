/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.chiorichan.configuration.file.YamlConfiguration;
import com.chiorichan.libraries.Libraries;
import com.chiorichan.libraries.MavenReference;
import com.chiorichan.logger.Log;
import com.chiorichan.util.Versioning;

/**
 * Provides a wrapper that downloads and maintains application version and libraries.
 *
 * XXX It's imperative that this class is using ABSOLUTELY NO THIRD-PARTY LIBRARIES as this class is loaded before the classpath is set.
 */
public class DeployWrapper
{
	public static YamlConfiguration yaml;
	private static boolean isDeployment = false;

	private static void err( String msg )
	{
		Log.get().severe( msg );
		System.exit( 1 );
	}

	public static boolean isDeployment()
	{
		return isDeployment;
	}

	@SuppressWarnings( "unchecked" )
	private static void launchApplication()
	{
		String mainClass = yaml.getString( "deploy.main-class" );

		if ( mainClass == null || mainClass.length() == 0 )
			err( "Deployment failed! The main-class in 'deploy.yaml' is invalid." );

		try
		{
			Class<?> cls = Class.forName( mainClass );
			if ( !AppLoader.class.isAssignableFrom( cls ) )
				throw new IllegalStateException( "Main-class " + mainClass + " does not extend AppLoader class!" );
			AppLoader.init( ( Class<? extends AppLoader> ) cls );
		}
		catch ( Throwable t )
		{
			err( "Deployment failed! We excountered an exception while trying to initalize the main-class: " + t.getMessage() );
		}
	}

	private static void loadDeploymentConfig()
	{
		isDeployment = true;

		InputStream is = null;
		try
		{
			is = AppLoader.class.getClassLoader().getResourceAsStream( "deploy.yaml" );
			if ( is == null )
				err( "Deployment failed! Application is missing the 'deploy.yaml' file." );
			yaml = YamlConfiguration.loadConfiguration( is );
		}
		finally
		{
			try
			{
				if ( is != null )
					is.close();
			}
			catch ( IOException e )
			{
			}
		}
	}

	public static void main( String... args ) throws Exception
	{
		try
		{
			if ( AppLoader.parseArguments( args ) )
			{
				loadDeploymentConfig();

				Log.get().info( "Starting deployment of " + Versioning.getProduct() + " (" + Versioning.getVersion() + ")" );
				Log.get().info( "Loading deployment libraries: " + Libraries.LIBRARY_DIR.getAbsolutePath() );

				// Fake it being loaded as it's internally included
				Libraries.addLoaded( "org.yaml:snakeyaml:1.17" );
				Libraries.addLoaded( "net.sf.jopt-simple:jopt-simple:5.0.1" );
				Libraries.addLoaded( "org.fusesource.jansi:jansi:1.11" );

				Libraries.loadLibrary( new MavenReference( "builtin", "mysql:mysql-connector-java:5.1.32" ) );
				Libraries.loadLibrary( new MavenReference( "builtin", "org.xerial:sqlite-jdbc:3.8.11.2" ) );
				Libraries.loadLibrary( new MavenReference( "builtin", "com.h2database:h2:1.4.187" ) );
				Libraries.loadLibrary( new MavenReference( "builtin", "com.google.guava:guava:18.0" ) );
				Libraries.loadLibrary( new MavenReference( "builtin", "org.apache.commons:commons-lang3:3.3.2" ) );
				Libraries.loadLibrary( new MavenReference( "builtin", "commons-io:commons-io:2.4" ) );
				Libraries.loadLibrary( new MavenReference( "builtin", "commons-net:commons-net:3.3" ) );
				Libraries.loadLibrary( new MavenReference( "builtin", "commons-codec:commons-codec:1.9" ) );
				Libraries.loadLibrary( new MavenReference( "builtin", "joda-time:joda-time:2.7" ) );
				Libraries.loadLibrary( new MavenReference( "builtin", "org.ocpsoft.prettytime:prettytime:3.2.5.Final" ) );
				Libraries.loadLibrary( new MavenReference( "builtin", "com.googlecode.libphonenumber:libphonenumber:7.0.4" ) );
				Libraries.loadLibrary( new MavenReference( "builtin", "com.google.code.gson:gson:2.3" ) );
				Libraries.loadLibrary( new MavenReference( "builtin", "org.apache.httpcomponents:fluent-hc:4.3.5" ) );

				List<String> libs = yaml.getAsList( "deploy.libraries" );
				if ( libs != null )
				{
					Log.get().info( "Loading deployment libraries defined in deploy.yaml!" );
					for ( String lib : libs )
						Libraries.loadLibrary( new MavenReference( "builtin", lib ) );
				}

				Log.get().info( "Finished downloading deployment libraries, now launching application!" );

				launchApplication();
			}
		}
		catch ( Throwable t )
		{
			t.printStackTrace();
		}
	}
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan;

import com.chiorichan.configuration.types.yaml.YamlConfiguration;
import com.chiorichan.utils.UtilIO;
import com.chiorichan.libraries.Libraries;
import com.chiorichan.libraries.MavenReference;
import com.chiorichan.logger.Log;

import java.io.InputStream;
import java.util.List;

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
			UtilIO.closeQuietly( is );
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

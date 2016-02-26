/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.configuration.apache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class ApacheConfiguration extends ApacheSection
{
	public ApacheConfiguration()
	{

	}

	public ApacheConfiguration( File source ) throws IOException
	{
		if ( source.exists() )
			if ( source.isFile() )
				appendWithFile( source );
			else
				appendWithDir( source );
	}

	public ApacheConfiguration( String text ) throws IOException
	{
		appendRaw( text, "<source unknown>" );
	}

	public ApacheConfiguration appendWithDir( File dir ) throws IOException
	{
		if ( dir.exists() && dir.isDirectory() )
		{
			File htaccessFile = new File( dir, ".htaccess" );
			if ( htaccessFile.exists() && htaccessFile.isFile() )
				appendWithFile( htaccessFile );

			htaccessFile = new File( dir, "htaccess" );
			if ( htaccessFile.exists() && htaccessFile.isFile() )
				appendWithFile( htaccessFile );
		}

		return this;
	}

	public ApacheConfiguration appendWithFile( File file ) throws FileNotFoundException
	{
		if ( file.exists() && file.isFile() )
			try ( BufferedReader br = new BufferedReader( new FileReader( file ) ) )
			{
				appendRaw( br, file.getAbsolutePath() );
				IOUtils.closeQuietly( br );
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}

		return this;
	}
}

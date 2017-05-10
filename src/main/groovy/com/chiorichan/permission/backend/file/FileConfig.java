/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.permission.backend.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.chiorichan.configuration.types.yaml.YamlConfiguration;
import com.chiorichan.permission.PermissionManager;

public class FileConfig extends YamlConfiguration
{
	protected File file;
	
	public FileConfig( File file )
	{
		super();
		
		this.file = file;
		
		reload();
	}
	
	public File getFile()
	{
		return file;
	}
	
	public void reload()
	{
		
		try
		{
			this.load( file );
		}
		catch ( FileNotFoundException e )
		{
			// do nothing
		}
		catch ( Throwable e )
		{
			throw new IllegalStateException( "Error loading permissions file", e );
		}
	}
	
	public void save()
	{
		try
		{
			this.save( file );
		}
		catch ( IOException e )
		{
			PermissionManager.getLogger().severe( "Error during saving permissions file: " + e.getMessage() );
		}
	}
}

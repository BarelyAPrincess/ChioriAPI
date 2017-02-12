/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.datastore.file;

import com.chiorichan.configuration.ConfigurationSection;
import com.chiorichan.configuration.types.yaml.YamlConfiguration;
import com.chiorichan.datastore.Datastore;
import com.chiorichan.zutils.ZIO;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * References multiple YAML files at once
 */
public class FileDatastore extends Datastore
{
	public static FileDatastore loadDirectory( File dir, String regexPattern )
	{
		List<File> files = ZIO.recursiveFiles( dir, StringUtils.countMatches( regexPattern, "/" ), regexPattern );
		return new FileDatastore( files );
	}

	public static FileDatastore loadFile( File file )
	{
		return new FileDatastore( Arrays.asList( file ) );
	}

	private String path;

	private final Map<File, YamlConfiguration> yamls = Maps.newLinkedHashMap();

	private int index;

	private FileDatastore( List<File> files )
	{
		path = "";
		for ( File file : files )
			if ( file != null && file.exists() )
				yamls.put( file, YamlConfiguration.loadConfiguration( file ) );
	}

	private FileDatastore( String path, Map<File, YamlConfiguration> yamls, int index )
	{
		this.path = path;
		this.yamls.putAll( yamls );
		this.index = index;
	}

	public Set<Entry<File, YamlConfiguration>> asEntrySet()
	{
		return yamls.entrySet();
	}

	public Collection<YamlConfiguration> asList()
	{
		return Collections.unmodifiableCollection( yamls.values() );
	}

	public Map<File, YamlConfiguration> asMap()
	{
		return Collections.unmodifiableMap( yamls );
	}

	public File directFile()
	{
		if ( index < 0 || index > yamls.size() - 1 )
			throw new IndexOutOfBoundsException( "Index is out of bounds" );

		return yamls.keySet().toArray( new File[0] )[index];
	}

	public YamlConfiguration directYaml()
	{
		if ( index < 0 || index > yamls.size() - 1 )
			throw new IndexOutOfBoundsException( "Index is out of bounds" );

		return yamls.values().toArray( new YamlConfiguration[0] )[index];
	}

	public void first()
	{
		index = 0;
	}

	public FileDatastore getChild( String child )
	{
		return new FileDatastore( path + "/" + child, yamls, index );
	}

	public String getString( String key )
	{
		ConfigurationSection section = section();
		return section == null ? null : section.getString( key );
	}

	public boolean hasNext()
	{
		return index < yamls.size();
	}

	public void last()
	{
		index = yamls.size() - 1;
	}

	public void next()
	{
		index++;
	}

	public void previous()
	{
		index--;
	}

	private ConfigurationSection section()
	{
		return directYaml().getConfigurationSection( path );
	}
}

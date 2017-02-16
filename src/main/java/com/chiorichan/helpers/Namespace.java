/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package com.chiorichan.helpers;

import com.chiorichan.zutils.ZObjects;
import com.chiorichan.zutils.ZStrings;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Namespace extends NamespaceBase<Namespace> implements Cloneable
{
	public static Namespace parseString( String namespace )
	{
		return parseString( namespace, null );
	}

	public static Namespace parseStringRegex( String namespace, String regex )
	{
		if ( namespace == null )
			namespace = "";
		if ( regex == null || regex.length() == 0 )
			regex = "\\.";
		return new Namespace( Splitter.on( Pattern.compile( regex ) ).splitToList( namespace ) );
	}

	public static Namespace parseString( String namespace, String separator )
	{
		if ( namespace == null )
			namespace = "";
		if ( separator == null || separator.length() == 0 )
			separator = ".";
		return new Namespace( Splitter.on( separator ).splitToList( namespace ) );
	}

	public Namespace( String[] nodes )
	{
		super( nodes );
	}

	public Namespace( List<String> nodes )
	{
		super( nodes );
	}

	public Namespace()
	{
		super();
	}

	@Override
	protected Namespace create( String[] nodes )
	{
		return new Namespace( nodes );
	}
}

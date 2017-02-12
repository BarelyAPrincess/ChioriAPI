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

public class Namespace implements Cloneable
{
	protected static Pattern rangeExpression = Pattern.compile( "(0-9+)-(0-9+)" );

	protected String[] nodes;

	protected Namespace( String... nodes )
	{
		this.nodes = ZStrings.toLowerCase( nodes );
	}

	protected Namespace( List<String> nodes )
	{
		this.nodes = ZStrings.toLowerCase( nodes.toArray( new String[0] ) );
	}

	protected Namespace()
	{
		this.nodes = new String[0];
	}

	public static Namespace create( String... nodes )
	{
		return new Namespace( nodes );
	}

	public static Namespace create()
	{
		return new Namespace();
	}

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

	private String[] splitString( String str )
	{
		return splitString( str, null );
	}

	private String[] splitString( String str, String separator )
	{
		if ( ZObjects.isEmpty( separator ) )
			separator = ".";
		return Splitter.on( separator ).omitEmptyStrings().splitToList( str ).toArray( new String[0] );
	}

	public Namespace prepend( String... nodes )
	{
		if ( nodes.length == 0 )
			throw new IllegalArgumentException( "Nodes are empty" );
		if ( nodes.length == 1 )
			nodes = splitString( nodes[0] );
		this.nodes = ArrayUtils.addAll( nodes, this.nodes );
		return this;
	}

	public Namespace prependNew( String... nodes )
	{
		if ( nodes.length == 0 )
			throw new IllegalArgumentException( "Nodes are empty" );
		if ( nodes.length == 1 )
			nodes = splitString( nodes[0] );
		return new Namespace( ArrayUtils.addAll( nodes, this.nodes ) );
	}

	public Namespace append( String... nodes )
	{
		if ( nodes.length == 0 )
			throw new IllegalArgumentException( "Nodes are empty" );
		if ( nodes.length == 1 )
			nodes = splitString( nodes[0] );
		this.nodes = ArrayUtils.addAll( this.nodes, nodes );
		return this;
	}

	public Namespace appendNew( String... nodes )
	{
		if ( nodes.length == 0 )
			throw new IllegalArgumentException( "Nodes are empty" );
		if ( nodes.length == 1 )
			nodes = splitString( nodes[0] );
		return new Namespace( ArrayUtils.addAll( this.nodes, nodes ) );
	}

	/**
	 * Checks is namespace only contains valid characters.
	 *
	 * @return True if namespace contains only valid characters
	 */
	public boolean containsOnlyValidChars()
	{
		for ( String n : nodes )
			if ( !n.matches( "[a-z0-9_]*" ) )
				return false;
		return true;
	}

	public boolean containsRegex()
	{
		for ( String s : nodes )
			if ( s.contains( "*" ) || s.matches( ".*[0-9]+-[0-9]+.*" ) )
				return true;
		return false;
	}

	public Namespace replaceNew( String literal, String replacement )
	{
		return new Namespace( Arrays.stream( nodes ).map( s -> s.replace( literal, replacement ) ).collect( Collectors.toList() ).toArray( new String[0] ) );
	}

	public Namespace replace( String literal, String replacement )
	{
		nodes = Arrays.stream( nodes ).map( s -> s.replace( literal, replacement ) ).collect( Collectors.toList() ).toArray( new String[0] );
		return this;
	}

	/**
	 * Filters out invalid characters from namespace.
	 *
	 * @return The fixed PermissionNamespace.
	 */
	public Namespace fixInvalidChars()
	{
		String[] result = new String[nodes.length];
		for ( int i = 0; i < nodes.length; i++ )
			result[i] = nodes[i].replaceAll( "[^a-z0-9_]", "" );
		return new Namespace( result );
	}

	public String getFirst()
	{
		return getNode( 0 );
	}

	public String getLast()
	{
		return getNode( getNodeCount() - 1 );
	}

	public String getLocalName()
	{
		return nodes[nodes.length - 1];
	}

	public String getString()
	{
		return getString( "." );
	}

	public String getString( String separator )
	{
		return getString( separator, false );
	}

	/**
	 * Converts Namespace to a String
	 *
	 * @param separator The node separator
	 * @param escape    Shall we escape separator characters in node names
	 * @return The converted String
	 */
	public String getString( String separator, boolean escape )
	{
		if ( escape )
			return Joiner.on( separator ).join( Arrays.stream( nodes ).map( n -> n.replace( separator, "\\" + separator ) ).collect( Collectors.toList() ) );
		return Joiner.on( separator ).join( nodes );
	}

	public String getNode( int inx )
	{
		try
		{
			return nodes[inx];
		}
		catch ( IndexOutOfBoundsException e )
		{
			return null;
		}
	}

	public int getNodeCount()
	{
		return nodes.length;
	}

	public String[] getNodes()
	{
		return nodes;
	}

	public String getNodeWithException( int inx )
	{
		return nodes[inx];
	}

	public String getParent()
	{
		if ( nodes.length <= 1 )
			return "";

		return Joiner.on( "." ).join( Arrays.copyOf( nodes, nodes.length - 1 ) );
	}

	public Namespace getParentNamespace()
	{
		return getParentNamespace( 1 );
	}

	public Namespace getParentNamespace( int depth )
	{
		return getNodeCount() >= depth ? subNamespace( 0, getNodeCount() - depth ) : create();
	}

	public String getRootName()
	{
		return nodes[0];
	}

	public boolean matches( String perm )
	{
		/*
		 * We are not going to try and match a permission if it contains regex.
		 * This means someone must have gotten their strings backward.
		 */
		if ( perm.contains( "*" ) || perm.matches( ".*[0-9]+-[0-9]+.*" ) )
			return false;

		return prepareRegexp().matcher( perm ).matches();
	}

	public int matchPercentage( String namespace )
	{
		return matchPercentage( namespace, "." );
	}

	public int matchPercentage( String namespace, String separator )
	{
		ZObjects.notEmpty( namespace );

		String[] dest = Splitter.on( separator ).splitToList( namespace.toLowerCase() ).toArray( new String[0] );

		int total = 0;
		int perNode = 99 / nodes.length;

		for ( int i = 0; i < Math.min( nodes.length, dest.length ); i++ )
			if ( nodes[i].equals( dest[i] ) )
				total += perNode;
			else
				break;

		if ( nodes.length == dest.length )
			total += 1;

		return total;
	}

	/**
	 * Prepares a namespace for parsing via RegEx
	 *
	 * @return The fully RegEx ready string
	 */
	public Pattern prepareRegexp()
	{
		String regexpOrig = Joiner.on( "\\." ).join( nodes );
		String regexp = regexpOrig.replace( "*", "(.*)" );

		try
		{
			Matcher rangeMatcher = rangeExpression.matcher( regexp );
			while ( rangeMatcher.find() )
			{
				StringBuilder range = new StringBuilder();
				int from = Integer.parseInt( rangeMatcher.group( 1 ) );
				int to = Integer.parseInt( rangeMatcher.group( 2 ) );

				range.append( "(" );

				for ( int i = Math.min( from, to ); i <= Math.max( from, to ); i++ )
				{
					range.append( i );
					if ( i < Math.max( from, to ) )
						range.append( "|" );
				}

				range.append( ")" );

				regexp = regexp.replace( rangeMatcher.group( 0 ), range.toString() );
			}
		}
		catch ( Throwable e )
		{
			// Ignore
		}

		try
		{
			return Pattern.compile( regexp, Pattern.CASE_INSENSITIVE );
		}
		catch ( PatternSyntaxException e )
		{
			return Pattern.compile( Pattern.quote( regexpOrig.replace( "*", "(.*)" ) ), Pattern.CASE_INSENSITIVE );
		}
	}

	public Namespace reverseOrderNew()
	{
		List<String> tmpNodes = Arrays.asList( nodes );
		Collections.reverse( tmpNodes );
		return new Namespace( tmpNodes );
	}

	public Namespace reverseOrder()
	{
		List<String> tmpNodes = Arrays.asList( nodes );
		Collections.reverse( tmpNodes );
		nodes = tmpNodes.toArray( new String[0] );
		return this;
	}

	public Namespace subNamespace( int start )
	{
		return subNamespace( start, getNodeCount() );
	}

	public Namespace subNamespace( int start, int end )
	{
		return new Namespace( subNodes( start, end ) );
	}

	public String[] subNodes( int start )
	{
		return subNodes( start, getNodeCount() );
	}

	public String[] subNodes( int start, int end )
	{
		if ( start < 0 )
			throw new IllegalArgumentException( "Start can't be less than 0" );
		if ( start > nodes.length )
			throw new IllegalArgumentException( "Start can't be more than length " + nodes.length );
		if ( end > nodes.length )
			throw new IllegalArgumentException( "End can't be more than node count" );

		return Arrays.copyOfRange( nodes, start, end );
	}

	@Override
	public String toString()
	{
		return getString();
	}

	@Override
	public Namespace clone()
	{
		return new Namespace( nodes );
	}

	public boolean isEmpty()
	{
		return nodes.length == 0;
	}

	public Namespace merge( Namespace ns )
	{
		return new Namespace( Stream.of( nodes, ns.nodes ).flatMap( Stream::of ).toArray( String[]::new ) );
	}
}

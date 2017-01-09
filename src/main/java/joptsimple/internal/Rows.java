/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple.internal;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static joptsimple.internal.Strings.LINE_SEPARATOR;
import static joptsimple.internal.Strings.repeat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class Rows
{
	private final int overallWidth;
	private final int columnSeparatorWidth;
	private final List<Row> rows = new ArrayList<>();

	private int widthOfWidestOption;
	private int widthOfWidestDescription;

	public Rows( int overallWidth, int columnSeparatorWidth )
	{
		this.overallWidth = overallWidth;
		this.columnSeparatorWidth = columnSeparatorWidth;
	}

	private void add( Row row )
	{
		rows.add( row );
		widthOfWidestOption = max( widthOfWidestOption, row.option.length() );
		widthOfWidestDescription = max( widthOfWidestDescription, row.description.length() );
	}

	public void add( String option, String description )
	{
		add( new Row( option, description ) );
	}

	private int descriptionWidth()
	{
		return min( overallWidth - optionWidth() - columnSeparatorWidth, widthOfWidestDescription );
	}

	public void fitToWidth()
	{
		Columns columns = new Columns( optionWidth(), descriptionWidth() );

		List<Row> fitted = new ArrayList<>();
		for ( Row each : rows )
			fitted.addAll( columns.fit( each ) );

		reset();

		for ( Row each : fitted )
			add( each );
	}

	private int optionWidth()
	{
		return min( ( overallWidth - columnSeparatorWidth ) / 2, widthOfWidestOption );
	}

	private StringBuilder pad( StringBuilder buffer, String s, int length )
	{
		buffer.append( s ).append( repeat( ' ', length - s.length() ) );
		return buffer;
	}

	public String render()
	{
		StringBuilder buffer = new StringBuilder();

		for ( Row each : rows )
		{
			pad( buffer, each.option, optionWidth() ).append( repeat( ' ', columnSeparatorWidth ) );
			pad( buffer, each.description, descriptionWidth() ).append( LINE_SEPARATOR );
		}

		return buffer.toString();
	}

	public void reset()
	{
		rows.clear();
		widthOfWidestOption = 0;
		widthOfWidestDescription = 0;
	}
}

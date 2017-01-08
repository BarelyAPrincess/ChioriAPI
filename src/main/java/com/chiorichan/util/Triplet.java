package com.chiorichan.util;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.io.Serializable;

/**
 * <p>A triplet consisting of three elements.</p>
 * <p>It refers to the elements as 'left', 'middle' and 'right'.</p>
 *
 * @param <L> the left element type
 * @param <M> the middle element type
 * @param <R> the right element type
 */
public class Triplet<L, M, R> implements Comparable<Triplet<L, M, R>>, Serializable
{
	private static final long serialVersionUID = 1L;

	private L left;
	private M middle;
	private R right;

	public Triplet( L left, M middle, R right )
	{
		this.left = left;
		this.middle = middle;
		this.right = right;
	}

	public L getStart()
	{
		return left;
	}

	public L getLeft()
	{
		return left;
	}

	public M getMiddle()
	{
		return middle;
	}

	public R getRight()
	{
		return right;
	}

	public R getEnd()
	{
		return right;
	}

	/**
	 * <p>Compares the triplet based on the left element, followed by the middle element,
	 * finally the right element.
	 * The types must be {@code Comparable}.</p>
	 *
	 * @param other the other triplet, not null
	 * @return negative if this is less, zero if equal, positive if greater
	 */
	@Override
	public int compareTo( final Triplet<L, M, R> other )
	{
		return new CompareToBuilder().append( left, other.left ).append( middle, other.middle ).append( right, other.right ).toComparison();
	}

	/**
	 * <p>Compares this triplet to another based on the three elements.</p>
	 *
	 * @param obj the object to compare to, null returns false
	 * @return true if the elements of the triplet are equal
	 */
	@Override
	public boolean equals( final Object obj )
	{
		if ( obj == this )
		{
			return true;
		}
		if ( obj instanceof Triplet )
		{
			final Triplet<?, ?, ?> other = ( Triplet ) obj;
			return ObjectUtils.equals( left, other.left ) && ObjectUtils.equals( middle, other.middle ) && ObjectUtils.equals( right, other.right );
		}
		return false;
	}

	/**
	 * <p>Returns a suitable hash code.</p>
	 *
	 * @return the hash code
	 */
	@Override
	public int hashCode()
	{
		return ( left == null ? 0 : left.hashCode() ) ^ ( middle == null ? 0 : middle.hashCode() ) ^ ( right == null ? 0 : right.hashCode() );
	}

	/**
	 * <p>Returns a String representation of this triplet using the format {@code ($left,$middle,$right)}.</p>
	 *
	 * @return a string describing this object, not null
	 */
	@Override
	public String toString()
	{
		return new StringBuilder().append( '(' ).append( left ).append( ',' ).append( middle ).append( ',' ).append( right ).append( ')' ).toString();
	}

	/**
	 * <p>Formats the receiver using the given format.</p>
	 * <p>
	 * <p>This uses {@link java.util.Formattable} to perform the formatting. Three variables may
	 * be used to embed the left and right elements. Use {@code %1$s} for the left
	 * element, {@code %2$s} for the middle and {@code %3$s} for the right element.
	 * The default format used by {@code toString()} is {@code (%1$s,%2$s,%3$s)}.</p>
	 *
	 * @param format the format string, optionally containing {@code %1$s}, {@code %2$s} and {@code %3$s}, not null
	 * @return the formatted string, not null
	 */
	public String toString( final String format )
	{
		return String.format( format, left, middle, right );
	}
}

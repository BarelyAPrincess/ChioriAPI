/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * <p>
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.lang;

import com.chiorichan.logger.Log;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * This class is used to analyze and report exceptions
 */
public class ExceptionReport
{
	private static final Map<Class<? extends Throwable>, ExceptionCallback> registered = Maps.newConcurrentMap();

	public static String printExceptions( IException... exceptions )
	{
		// Might need some better handling for this!
		StringBuilder sb = new StringBuilder();
		for ( IException e : exceptions )
			sb.append( e.getMessage() + "\n" );
		return sb.toString();
	}

	/**
	 * Registers an expected exception to be thrown
	 *
	 * @param callback The Callback to call when such exception is thrown
	 * @param clzs     Classes to be registered
	 */
	@SafeVarargs
	public static void registerException( ExceptionCallback callback, Class<? extends Throwable>... clzs )
	{
		for ( Class<? extends Throwable> clz : clzs )
			registered.put( clz, callback );
	}

	public static void throwExceptions( IException... exceptions ) throws Exception
	{
		List<IException> exps = Lists.newArrayList();

		for ( IException e : exceptions )
		{
			IException.check( e );
			if ( !e.reportingLevel().isIgnorable() )
				exps.add( e );
		}

		if ( exps.size() == 1 )
			if ( exps.get( 0 ) instanceof Exception )
				throw ( Exception ) exps.get( 0 );
			else
				throw new UncaughtException( ( Throwable ) exps.get( 0 ) );
		else if ( exps.size() > 0 )
			throw new MultipleException( exps );
	}

	protected final List<IException> caughtExceptions = new ArrayList<>();

	public ExceptionReport addException( IException exception )
	{
		IException.check( exception );
		if ( exception != null )
			caughtExceptions.add( exception );
		return this;
	}

	public ExceptionReport addException( ReportingLevel level, String msg, Throwable throwable )
	{
		if ( throwable != null )
			caughtExceptions.add( new UncaughtException( level, msg, throwable ) );
		return this;
	}

	public ExceptionReport addException( ReportingLevel level, Throwable throwable )
	{
		if ( throwable != null )
			caughtExceptions.add( new UncaughtException( level, throwable ) );
		return this;
	}

	public IException[] getIgnorableExceptions()
	{
		return new ArrayList<IException>()
		{
			{
				for ( IException e : caughtExceptions )
					if ( e.reportingLevel().isIgnorable() )
						add( e );
			}
		}.toArray( new IException[0] );
	}

	public IException[] getNotIgnorableExceptions()
	{
		return new ArrayList<IException>()
		{
			{
				for ( IException e : caughtExceptions )
					if ( !e.reportingLevel().isIgnorable() )
						add( e );
			}
		}.toArray( new IException[0] );
	}

	/**
	 * Processes and appends the throwable to the context provided.
	 *
	 * @param cause   The exception thrown
	 * @param context The EvalContext associated with the eval request
	 * @return True if we should abort any further execution of code
	 */
	public final boolean handleException( Throwable cause, ExceptionContext context )
	{
		// ScriptingResult result = context.result();

		if ( cause == null )
			return false;

		if ( cause instanceof IException )
			return ( ( IException ) cause ).handle( this, context );
		else if ( cause instanceof MultipleException )
		{
			boolean abort = false;
			for ( IException e : ( ( MultipleException ) cause ).getExceptions() )
			{
				IException.check( e );
				if ( handleException( ( Throwable ) e, context ) )
					abort = true;
			}
			return abort;
		}
		else if ( cause instanceof NullPointerException || cause instanceof ArrayIndexOutOfBoundsException || cause instanceof IOException || cause instanceof StackOverflowError || cause instanceof ClassFormatError )
		{
			addException( ReportingLevel.E_ERROR, cause );
			return true;
		}
		else
		{
			boolean handled = false;

			Map<Class<? extends Throwable>, ExceptionCallback> assignable = new HashMap<>();

			for ( Entry<Class<? extends Throwable>, ExceptionCallback> entry : registered.entrySet() )
				if ( cause.getClass().equals( entry.getKey() ) )
				{
					ReportingLevel e = entry.getValue().callback( cause, this, context );
					if ( e == null )
					{
						handled = true;
						break;
					}
					else
						return !e.isIgnorable();
				}
				else if ( entry.getKey().isAssignableFrom( cause.getClass() ) )
					assignable.put( entry.getKey(), entry.getValue() );

			if ( !handled )
				if ( assignable.size() == 0 )
				{
					if ( cause instanceof IException )
						addException( ( IException ) cause );
					else
					{
						Log.get().severe( "Uncaught exception in EvalFactory for exception " + cause.getClass().getName(), cause );
						addException( ReportingLevel.E_ERROR, "Uncaught exception in EvalFactory", cause );
					}
				}
				else if ( assignable.size() == 1 )
				{
					ReportingLevel e = assignable.values().toArray( new ExceptionCallback[0] )[0].callback( cause, this, context );
					if ( e == null )
					{
						addException( ReportingLevel.E_ERROR, cause );
						return true;
					}
					else if ( !e.isIgnorable() )
						return true;
				}
				else
					for ( Entry<Class<? extends Throwable>, ExceptionCallback> entry : assignable.entrySet() )
					{
						boolean noAssignment = true;
						for ( Class<? extends Throwable> sub : assignable.keySet() )
							if ( sub != entry.getKey() )
								if ( sub.isAssignableFrom( entry.getKey() ) )
									noAssignment = false;
						if ( noAssignment )
						{
							ReportingLevel e = entry.getValue().callback( cause, this, context );
							return e != null && !e.isIgnorable();
						}
					}
		}
		return false;
	}

	/**
	 * Checks if exception is present by class name
	 *
	 * @param clz The exception to check for
	 * @return Is it present
	 */
	public boolean hasException( Class<? extends Throwable> clz )
	{
		Validate.notNull( clz );

		for ( IException e : caughtExceptions )
		{
			if ( e.getCause() != null && clz.isAssignableFrom( e.getCause().getClass() ) )
				return true;

			if ( clz.isAssignableFrom( e.getClass() ) )
				return true;
		}

		return false;
	}

	public boolean hasExceptions()
	{
		return !caughtExceptions.isEmpty();
	}

	public boolean hasIgnorableExceptions()
	{
		for ( IException e : caughtExceptions )
			if ( e.reportingLevel().isIgnorable() )
				return true;
		return false;
	}

	public boolean hasNonIgnorableExceptions()
	{
		for ( IException e : caughtExceptions )
			if ( !e.reportingLevel().isIgnorable() )
				return true;
		return false;
	}

	/*
	 * private static final ThreadLocal<Yaml> YAML_INSTANCE = new ThreadLocal<Yaml>()
	 * {
	 *
	 * @Override
	 * protected Yaml initialValue()
	 * {
	 * DumperOptions opts = new DumperOptions();
	 * opts.setDefaultFlowStyle( DumperOptions.FlowStyle.FLOW );
	 * opts.setDefaultScalarStyle( DumperOptions.ScalarStyle.DOUBLE_QUOTED );
	 * opts.setPrettyFlow( true );
	 * opts.setWidth( Integer.MAX_VALUE ); // Don't wrap scalars -- json no like
	 * return new Yaml( opts );
	 * }
	 * };
	 */
	//private static final URL GIST_POST_URL;
	/*
	 * static
	 * {
	 * try
	 * {
	 * GIST_POST_URL = new URL( "https://api.github.com/gists" );
	 * }
	 * catch ( MalformedURLException e )
	 * {
	 * throw new ExceptionInInitializerError( e );
	 * }
	 * }
	 */
}

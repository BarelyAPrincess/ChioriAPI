/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.services;

import java.util.ArrayList;
import java.util.List;

import com.chiorichan.ServiceManager;
import com.chiorichan.lang.ApplicationException;
import com.chiorichan.logger.Log;
import com.chiorichan.util.ObjectFunc;

public class AppManager<T extends ServiceManager>
{
	private static final List<AppManager<?>> managers = new ArrayList<>();

	@SuppressWarnings( "unchecked" )
	public static <M extends ServiceManager> AppManager<M> manager( Class<M> clz )
	{
		for ( AppManager<?> mgr : managers )
			if ( mgr.getManagerClass().equals( clz ) )
				return ( AppManager<M> ) mgr;
		return new AppManager<M>( clz );
	}

	private final Class<T> managerClass;
	private T instance = null;

	private AppManager( Class<T> managerClass )
	{
		this.managerClass = managerClass;
		managers.add( this );
	}

	public Log getLogger()
	{
		return Log.get( instance() );
	}

	public Class<T> getManagerClass()
	{
		return managerClass;
	}

	public String getName()
	{
		return managerClass.getSimpleName();
	}

	public void init( Object... args ) throws ApplicationException
	{
		// TODO Check if it's permitted!

		if ( instance != null )
			throw new IllegalStateException( "The " + getName() + " has already been initialized!" );

		instance = ObjectFunc.initClass( managerClass, args );
		instance.init();
	}

	public T instance()
	{
		if ( instance == null )
			throw new IllegalStateException( "The " + getName() + " has not been initialized!" );
		return instance;
	}

	public boolean isInitalized()
	{
		return instance != null;
	}
}

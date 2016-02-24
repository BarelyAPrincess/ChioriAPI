package com.chiorichan.services;

import java.util.ArrayList;
import java.util.List;

import com.chiorichan.ServiceManager;
import com.chiorichan.lang.ApplicationException;
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
			throw new IllegalStateException( "The " + getName() + " is not initialized, it must be first!" );
		return instance;
	}

	public boolean isInitalized()
	{
		return instance != null;
	}
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;

import com.chiorichan.AppConfig;
import com.chiorichan.event.EventBus;
import com.chiorichan.event.EventHandler;
import com.chiorichan.event.EventHandlers;
import com.chiorichan.event.EventPriority;
import com.chiorichan.event.EventRegistrar;
import com.chiorichan.event.Listener;
import com.chiorichan.event.application.RunlevelEvent;
import com.chiorichan.lang.PluginException;
import com.chiorichan.lang.PluginInformationException;
import com.chiorichan.lang.PluginInvalidException;
import com.chiorichan.lang.PluginNotFoundException;
import com.chiorichan.lang.RunLevel;
import com.chiorichan.lang.UnknownDependencyException;
import com.chiorichan.libraries.Libraries;
import com.chiorichan.libraries.MavenReference;
import com.chiorichan.logger.Log;
import com.chiorichan.plugin.loader.JavaPluginLoader;
import com.chiorichan.plugin.loader.Plugin;
import com.chiorichan.plugin.loader.PluginClassLoader;
import com.chiorichan.plugin.loader.PluginLoader;
import com.chiorichan.services.AppManager;
import com.chiorichan.services.ServiceManager;
import com.chiorichan.tasks.TaskManager;
import com.chiorichan.tasks.TaskRegistrar;
import com.chiorichan.util.FileFunc;
import com.google.common.collect.Maps;

public class PluginManager implements Listener, ServiceManager, EventRegistrar, TaskRegistrar
{
	public static Log getLogger()
	{
		return AppManager.manager( PluginManager.class ).getLogger();
	}

	public static PluginManager instance()
	{
		return AppManager.manager( PluginManager.class ).instance();
	}

	private final Map<Pattern, PluginLoader> fileAssociations = new HashMap<Pattern, PluginLoader>();

	private Set<String> loadedPlugins = new HashSet<String>();

	private final Map<String, Plugin> lookupNames = new HashMap<String, Plugin>();

	private final List<Plugin> plugins = new ArrayList<Plugin>();

	public PluginManager()
	{

	}

	public boolean canQueryPlugins()
	{
		return AppConfig.get().getBoolean( "plugins.allowQuery" );
	}

	private void checkUpdate( File file )
	{
		if ( AppConfig.get().getDirectoryUpdates() == null || !AppConfig.get().getDirectoryUpdates().isDirectory() )
			return;

		File updateFile = new File( AppConfig.get().getDirectoryUpdates(), file.getName() );
		if ( updateFile.isFile() && FileFunc.copy( updateFile, file ) )
			updateFile.delete();
	}

	public void clearPlugins()
	{
		synchronized ( this )
		{
			disablePlugins();
			plugins.clear();
			lookupNames.clear();
			EventHandlers.unregisterAll();
			fileAssociations.clear();
		}
	}

	public void disablePlugin( final Plugin plugin )
	{
		if ( plugin.isEnabled() )
		{
			try
			{
				plugin.getPluginLoader().disablePlugin( plugin );
			}
			catch ( NoClassDefFoundError ex )
			{
				// Ignore
			}
			catch ( Throwable ex )
			{
				getLogger().log( Level.SEVERE, "Error occurred (in the plugin loader) while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex );
			}

			try
			{
				TaskManager.instance().cancelTasks( plugin );
			}
			catch ( NoClassDefFoundError ex )
			{
				// Ignore
			}
			catch ( Throwable ex )
			{
				getLogger().log( Level.SEVERE, "Error occurred (in the plugin loader) while cancelling tasks for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex );
			}

			try
			{
				// Loader.getServicesManager().unregisterAll( plugin );
			}
			catch ( NoClassDefFoundError ex )
			{
				// Ignore
			}
			catch ( Throwable ex )
			{
				getLogger().log( Level.SEVERE, "Error occurred (in the plugin loader) while unregistering services for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex );
			}

			try
			{
				EventHandlers.unregisterAll( plugin );
			}
			catch ( NoClassDefFoundError ex )
			{
				// Ignore
			}
			catch ( Throwable ex )
			{
				getLogger().log( Level.SEVERE, "Error occurred (in the plugin loader) while unregistering events for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex );
			}

			try
			{
				// Loader.getMessenger().unregisterIncomingPluginChannel( plugin );
				// Loader.getMessenger().unregisterOutgoingPluginChannel( plugin );
			}
			catch ( NoClassDefFoundError ex )
			{
				// Ignore
			}
			catch ( Throwable ex )
			{
				getLogger().log( Level.SEVERE, "Error occurred (in the plugin loader) while unregistering plugin channels for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex );
			}
		}
	}

	public void disablePlugins()
	{
		Plugin[] plugins = getPlugins();
		for ( int i = plugins.length - 1; i >= 0; i-- )
			disablePlugin( plugins[i] );
	}

	public void enablePlugin( final Plugin plugin )
	{
		if ( !plugin.isEnabled() )
			try
			{
				plugin.getPluginLoader().enablePlugin( plugin );
			}
			catch ( Throwable ex )
			{
				getLogger().log( Level.SEVERE, "Error occurred (in the plugin loader) while enabling " + plugin.getDescription().getFullName() + " (Check for Version Mismatch)", ex );
			}
	}

	@Override
	public String getLoggerId()
	{
		return "PluginMgr";
	}

	@Override
	public String getName()
	{
		return "Plugin Manager";
	}

	public Plugin getPluginByClass( Class<?> clz ) throws PluginNotFoundException
	{
		try
		{
			if ( clz.getClassLoader() instanceof PluginClassLoader )
				for ( Plugin plugin : getPlugins() )
					if ( plugin == ( ( PluginClassLoader ) clz.getClassLoader() ).getPlugin() )
						return plugin;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

		throw new PluginNotFoundException( "We could not find a plugin with the class '" + clz + "', maybe it's not loaded." );
	}

	public Plugin getPluginByClassname( String className ) throws PluginNotFoundException
	{
		try
		{
			for ( Plugin plugin1 : getPlugins() )
				if ( plugin1.getClass().getName().startsWith( className ) )
					return plugin1;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

		throw new PluginNotFoundException( "We could not find a plugin with the classname '" + className + "', maybe it's not loaded." );
	}

	public Plugin getPluginByClassnameWithoutException( String className )
	{
		try
		{
			return getPluginByClassname( className );
		}
		catch ( PluginNotFoundException e )
		{
			getLogger().warning( e.getMessage() );
			return null;
		}
	}

	public Plugin getPluginByClassWithoutException( Class<?> clz )
	{
		try
		{
			return getPluginByClass( clz );
		}
		catch ( PluginNotFoundException e )
		{
			getLogger().warning( e.getMessage() );
			return null;
		}
	}

	public Plugin getPluginByName( String pluginName ) throws PluginNotFoundException
	{
		try
		{
			for ( Plugin plugin1 : getPlugins() )
				if ( plugin1.getClass().getCanonicalName().equals( pluginName ) || plugin1.getName().equalsIgnoreCase( pluginName ) )
					return plugin1;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

		throw new PluginNotFoundException( "We could not find a plugin by the name '" + pluginName + "', maybe it's not loaded." );
	}

	public Plugin getPluginByNameWithoutException( String pluginName )
	{
		try
		{
			return getPluginByName( pluginName );
		}
		catch ( PluginNotFoundException e )
		{
			getLogger().warning( e.getMessage() );
			return null;
		}
	}

	public synchronized Plugin[] getPlugins()
	{
		return plugins.toArray( new Plugin[0] );
	}

	@Override
	public void init()
	{
		EventBus.instance().registerEvents( this, this );
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	/**
	 * Loads the plugin in the specified file
	 * <p>
	 * File must be valid according to the current enabled Plugin interfaces
	 *
	 * @param file
	 *             File containing the plugin to load
	 * @return The Plugin instance
	 * @throws PluginInvalidException
	 *              Thrown when the specified file is not a valid plugin
	 * @throws UnknownDependencyException
	 *              If a required dependency could not be found
	 */
	public synchronized Plugin loadPlugin( File file ) throws PluginInvalidException, UnknownDependencyException
	{
		Validate.notNull( file, "File cannot be null" );

		checkUpdate( file );

		Set<Pattern> filters = fileAssociations.keySet();
		Plugin result = null;

		for ( Pattern filter : filters )
		{
			String name = file.getName();
			Matcher match = filter.matcher( name );

			if ( match.find() )
			{
				PluginLoader loader = fileAssociations.get( filter );

				result = loader.loadPlugin( file );
			}
		}

		if ( result != null )
		{
			plugins.add( result );
			lookupNames.put( result.getDescription().getName(), result );
		}

		return result;
	}

	protected void loadPlugin( Plugin plugin )
	{
		try
		{
			enablePlugin( plugin );
		}
		catch ( Throwable ex )
		{
			getLogger().log( Level.SEVERE, ex.getMessage() + " loading " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex );
		}
	}

	public void loadPlugins() throws PluginException
	{
		registerInterface( JavaPluginLoader.class );
		// registerInterface( GroovyPluginLoader.class );

		File pluginFolder = AppConfig.get().getDirectoryPlugins();
		if ( pluginFolder.exists() )
		{
			Plugin[] plugins = loadPlugins( pluginFolder );
			for ( Plugin plugin : plugins )
				try
				{
					String message = String.format( "Loading %s", plugin.getDescription().getFullName() );
					PluginManager.getLogger().info( message );
					plugin.onLoad();
				}
				catch ( Throwable ex )
				{
					getLogger().log( Level.SEVERE, ex.getMessage() + " initializing " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex );
				}
		}
		else
			pluginFolder.mkdir();
	}

	/**
	 * Loads the plugins contained within the specified directory
	 *
	 * @param directory
	 *             Directory to check for plugins
	 * @return A list of all plugins loaded
	 */
	public Plugin[] loadPlugins( File directory )
	{
		Validate.notNull( directory, "Directory cannot be null" );
		Validate.isTrue( directory.isDirectory(), "Directory must be a directory" );

		List<Plugin> result = new ArrayList<Plugin>();
		Set<Pattern> filters = fileAssociations.keySet();

		Map<String, File> plugins = new HashMap<String, File>();
		Map<String, Collection<MavenReference>> libraries = Maps.newHashMap();
		Map<String, Collection<String>> dependencies = Maps.newHashMap();
		Map<String, Collection<String>> softDependencies = Maps.newHashMap();

		// This is where it figures out all possible plugins
		for ( File file : directory.listFiles() )
		{
			PluginLoader loader = null;
			for ( Pattern filter : filters )
			{
				Matcher match = filter.matcher( file.getName() );
				if ( match.find() )
					loader = fileAssociations.get( filter );
			}

			if ( loader == null )
				continue;

			PluginInformation description = null;
			try
			{
				description = loader.getPluginDescription( file );
			}
			catch ( PluginInformationException ex )
			{
				getLogger().log( Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex );
				continue;
			}

			plugins.put( description.getName(), file );

			Collection<String> softDependencySet = description.getSoftDepend();
			if ( softDependencySet != null )
				if ( softDependencies.containsKey( description.getName() ) )
					// Duplicates do not matter, they will be removed together if applicable
					softDependencies.get( description.getName() ).addAll( softDependencySet );
				else
					softDependencies.put( description.getName(), new LinkedList<String>( softDependencySet ) );

			Collection<MavenReference> librariesSet = description.getLibraries();
			if ( librariesSet != null )
				libraries.put( description.getName(), new LinkedList<MavenReference>( librariesSet ) );

			Collection<String> dependencySet = description.getDepend();
			if ( dependencySet != null )
				dependencies.put( description.getName(), new LinkedList<String>( dependencySet ) );

			Collection<String> loadBeforeSet = description.getLoadBefore();
			if ( loadBeforeSet != null )
				for ( String loadBeforeTarget : loadBeforeSet )
					if ( softDependencies.containsKey( loadBeforeTarget ) )
						softDependencies.get( loadBeforeTarget ).add( description.getName() );
					else
					{
						// softDependencies is never iterated, so 'ghost' plugins aren't an issue
						Collection<String> shortSoftDependency = new LinkedList<String>();
						shortSoftDependency.add( description.getName() );
						softDependencies.put( loadBeforeTarget, shortSoftDependency );
					}
		}

		while ( !plugins.isEmpty() )
		{
			boolean missingDependency = true;
			Iterator<String> pluginIterator = plugins.keySet().iterator();

			while ( pluginIterator.hasNext() )
			{
				String plugin = pluginIterator.next();

				if ( libraries.containsKey( plugin ) )
				{
					Iterator<MavenReference> librariesIterator = libraries.get( plugin ).iterator();

					while ( librariesIterator.hasNext() )
					{
						MavenReference library = librariesIterator.next();

						if ( Libraries.isLoaded( library ) )
							librariesIterator.remove();
						else if ( !Libraries.loadLibrary( library ) )
						{
							missingDependency = false;
							File file = plugins.get( plugin );
							pluginIterator.remove();
							libraries.remove( plugin );
							softDependencies.remove( plugin );
							dependencies.remove( plugin );

							getLogger().severe( "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "' due to issue with library '" + library + "'." );
							break;
						}
					}
				}

				if ( dependencies.containsKey( plugin ) )
				{
					Iterator<String> dependencyIterator = dependencies.get( plugin ).iterator();

					while ( dependencyIterator.hasNext() )
					{
						String dependency = dependencyIterator.next();

						// Dependency loaded
						if ( loadedPlugins.contains( dependency ) )
							dependencyIterator.remove();
						else if ( !plugins.containsKey( dependency ) )
						{
							missingDependency = false;
							File file = plugins.get( plugin );
							pluginIterator.remove();
							libraries.remove( plugin );
							softDependencies.remove( plugin );
							dependencies.remove( plugin );

							getLogger().severe( "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", new UnknownDependencyException( dependency ) );
							break;
						}
					}

					if ( dependencies.containsKey( plugin ) && dependencies.get( plugin ).isEmpty() )
						dependencies.remove( plugin );
				}
				if ( softDependencies.containsKey( plugin ) )
				{
					Iterator<String> softDependencyIterator = softDependencies.get( plugin ).iterator();

					while ( softDependencyIterator.hasNext() )
					{
						String softDependency = softDependencyIterator.next();

						// Soft depend is no longer around
						if ( !plugins.containsKey( softDependency ) )
							softDependencyIterator.remove();
					}

					if ( softDependencies.get( plugin ).isEmpty() )
						softDependencies.remove( plugin );
				}
				if ( ! ( dependencies.containsKey( plugin ) || softDependencies.containsKey( plugin ) ) && plugins.containsKey( plugin ) )
				{
					// We're clear to load, no more soft or hard dependencies left
					File file = plugins.get( plugin );
					pluginIterator.remove();
					missingDependency = false;

					try
					{
						result.add( loadPlugin( file ) );
						loadedPlugins.add( plugin );
						continue;
					}
					catch ( PluginInvalidException ex )
					{
						getLogger().severe( "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex );
					}
				}
			}

			if ( missingDependency )
			{
				// We now iterate over plugins until something loads
				// This loop will ignore soft dependencies
				pluginIterator = plugins.keySet().iterator();

				while ( pluginIterator.hasNext() )
				{
					String plugin = pluginIterator.next();

					if ( !dependencies.containsKey( plugin ) )
					{
						softDependencies.remove( plugin );
						missingDependency = false;
						File file = plugins.get( plugin );
						pluginIterator.remove();

						try
						{
							result.add( loadPlugin( file ) );
							loadedPlugins.add( plugin );
							break;
						}
						catch ( PluginInvalidException ex )
						{
							getLogger().severe( "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex );
						}
					}
				}
				// We have no plugins left without a depend
				if ( missingDependency )
				{
					softDependencies.clear();
					dependencies.clear();
					Iterator<File> failedPluginIterator = plugins.values().iterator();

					while ( failedPluginIterator.hasNext() )
					{
						File file = failedPluginIterator.next();
						failedPluginIterator.remove();
						getLogger().severe( "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "': circular dependency detected" );
					}
				}
			}
		}

		return result.toArray( new Plugin[result.size()] );
	}

	/**
	 * Loads plugins in order as PluginManager receives the notices from the EventBus
	 */
	@EventHandler( priority = EventPriority.NORMAL )
	public void onServerRunLevelEvent( RunlevelEvent event )
	{
		RunLevel level = event.getRunLevel();

		Plugin[] plugins = getPlugins();

		for ( Plugin plugin : plugins )
			if ( !plugin.isEnabled() && plugin.getDescription().getLoad() == level )
				enablePlugin( plugin );
	}

	/**
	 * Registers the specified plugin loader
	 *
	 * @param loader
	 *             Class name of the PluginLoader to register
	 * @throws IllegalArgumentException
	 *              Thrown when the given Class is not a valid PluginLoader
	 */
	public void registerInterface( Class<? extends PluginLoader> loader ) throws IllegalArgumentException
	{
		PluginLoader instance;

		if ( PluginLoader.class.isAssignableFrom( loader ) )
		{
			Constructor<? extends PluginLoader> constructor;

			try
			{
				constructor = loader.getConstructor();
				instance = constructor.newInstance();
			}
			catch ( NoSuchMethodException ex )
			{
				/*
				 * try
				 * {
				 * constructor = loader.getConstructor( AppController.class );
				 * instance = constructor.newInstance( AppController.instance );
				 * }
				 * catch ( NoSuchMethodException ex1 )
				 * {
				 */
				String className = loader.getName();
				throw new IllegalArgumentException( String.format( "Class %s does not have a public %s(Server) constructor", className, className ), ex );
				/*
				 * }
				 * catch ( Exception ex1 )
				 * {
				 * throw new IllegalArgumentException( String.format( "Unexpected exception %s while attempting to construct a new instance of %s", ex.getClass().getName(), loader.getName() ), ex1 );
				 * }
				 */
			}
			catch ( Exception ex )
			{
				throw new IllegalArgumentException( String.format( "Unexpected exception %s while attempting to construct a new instance of %s", ex.getClass().getName(), loader.getName() ), ex );
			}
		}
		else
			throw new IllegalArgumentException( String.format( "Class %s does not implement interface PluginLoader", loader.getName() ) );

		Pattern[] patterns = instance.getPluginFileFilters();

		synchronized ( this )
		{
			for ( Pattern pattern : patterns )
				fileAssociations.put( pattern, instance );
		}
	}

	public void shutdown()
	{
		clearPlugins();
	}
}

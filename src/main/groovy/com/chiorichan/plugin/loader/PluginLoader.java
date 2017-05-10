/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.plugin.loader;

import java.io.File;
import java.util.regex.Pattern;

import com.chiorichan.lang.PluginInformationException;
import com.chiorichan.lang.PluginInvalidException;
import com.chiorichan.lang.UnknownDependencyException;
import com.chiorichan.plugin.PluginInformation;

/**
 * Represents a plugin loader, which handles direct access to specific types of plugins
 */
public interface PluginLoader
{
	/**
	 * Loads the plugin contained in the specified file
	 *
	 * @param file
	 *             File to attempt to load
	 * @return Plugin that was contained in the specified file, or null if
	 *         unsuccessful
	 * @throws PluginInvalidException
	 *              Thrown when the specified file is not a
	 *              plugin
	 * @throws UnknownDependencyException
	 *              If a required dependency could not
	 *              be found
	 */
	Plugin loadPlugin( File file ) throws PluginInvalidException, UnknownDependencyException;

	/**
	 * Loads a PluginDescriptionFile from the specified file
	 *
	 * @param file
	 *             File to attempt to load from
	 * @return A new PluginDescriptionFile loaded from the plugin.yml in the
	 *         specified file
	 * @throws PluginInformationException
	 *              If the plugin description file
	 *              could not be created
	 */
	PluginInformation getPluginDescription( File file ) throws PluginInformationException;

	/**
	 * Returns a list of all filename filters expected by this PluginLoader
	 *
	 * @return The filters
	 */
	Pattern[] getPluginFileFilters();

	/**
	 * Enables the specified plugin
	 * <p>
	 * Attempting to enable a plugin that is already enabled will have no effect
	 *
	 * @param plugin
	 *             Plugin to enable
	 */
	void enablePlugin( Plugin plugin );

	/**
	 * Disables the specified plugin
	 * <p>
	 * Attempting to disable a plugin that is not enabled will have no effect
	 *
	 * @param plugin
	 *             Plugin to disable
	 */
	void disablePlugin( Plugin plugin );
}

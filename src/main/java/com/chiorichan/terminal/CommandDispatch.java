/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.terminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.chiorichan.event.EventBus;
import com.chiorichan.event.account.CommandIssuedEvent;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.logger.Log;
import com.chiorichan.permission.PermissionCommand;
import com.chiorichan.terminal.commands.BuiltinCommand;
import com.google.common.collect.Maps;

/**
 * This is the Command Dispatch for executing a command from a console.
 */
public final class CommandDispatch
{
	private static Map<Terminal, TerminalInterviewer> activeInterviewer = Maps.newConcurrentMap();
	private static Map<Terminal, List<TerminalInterviewer>> interviewers = Maps.newConcurrentMap();
	private static final Pattern PATTERN_ON_SPACE = Pattern.compile( " ", Pattern.LITERAL );
	private static List<CommandContext> pendingCommands = Collections.synchronizedList( new ArrayList<CommandContext>() );
	private static List<Command> registeredCommands = Collections.synchronizedList( new ArrayList<Command>() );

	static
	{
		BuiltinCommand.registerBuiltinCommands();
		CommandDispatch.registerCommand( new PermissionCommand() );
	}

	public static void addInterviewer( Terminal handler, TerminalInterviewer interviewer )
	{
		if ( interviewers.get( handler ) == null )
			interviewers.put( handler, new ArrayList<TerminalInterviewer>( Arrays.asList( interviewer ) ) );
		else
			interviewers.get( handler ).add( interviewer );
	}

	private static Command getCommand( String sentCommandLabel )
	{
		for ( Command command : registeredCommands )
			if ( command.getName().equals( sentCommandLabel.toLowerCase() ) || command.getAliases().contains( sentCommandLabel.toLowerCase() ) )
				return command;
		return null;
	}

	public static void handleCommands()
	{
		for ( Entry<Terminal, List<TerminalInterviewer>> entry : interviewers.entrySet() )
			if ( activeInterviewer.get( entry.getKey() ) == null )
				if ( entry.getValue().isEmpty() )
				{
					interviewers.remove( entry.getKey() );
					entry.getKey().resetPrompt();
				}
				else
				{
					TerminalInterviewer i = entry.getValue().remove( 0 );
					activeInterviewer.put( entry.getKey(), i );
					entry.getKey().setPrompt( i.getPrompt() );
				}

		while ( !pendingCommands.isEmpty() )
		{
			CommandContext command = pendingCommands.remove( 0 );

			try
			{
				TerminalInterviewer i = activeInterviewer.get( command.terminal );
				Terminal permissible = command.terminal;

				if ( i != null )
				{
					if ( i.handleInput( command.command ) )
						activeInterviewer.remove( command.terminal );
					else
						command.terminal.prompt();
				}
				else
				{
					CommandIssuedEvent event = new CommandIssuedEvent( command.command, permissible );

					EventBus.instance().callEvent( event );

					if ( event.isCancelled() )
					{
						permissible.sendMessage( EnumColor.RED + "Your entry was cancelled by the event system." );
						return;
					}

					String[] args = PATTERN_ON_SPACE.split( command.command );

					if ( args.length > 0 )
					{
						String sentCommandLabel = args[0].toLowerCase();
						Command target = getCommand( sentCommandLabel );

						if ( target != null )
							try
							{
								if ( target.testPermission( permissible ) )
									target.execute( command.terminal, sentCommandLabel, Arrays.copyOfRange( args, 1, args.length ) );

								return;
							}
							catch ( CommandException ex )
							{
								throw ex;
							}
							catch ( Throwable ex )
							{
								command.terminal.sendMessage( EnumColor.RED + "Unhandled exception executing '" + command.command + "' in " + target + "\n" + ExceptionUtils.getStackTrace( ex ) );

								throw new CommandException( "Unhandled exception executing '" + command.command + "' in " + target, ex );
							}
					}

					permissible.sendMessage( EnumColor.YELLOW + "Your entry was unrecognized, type \"help\" for help." );
				}
			}
			catch ( Exception ex )
			{
				Log.get().warning( "Unexpected exception while parsing console command \"" + command.command + '"', ex );
			}
		}
	}

	public static void issueCommand( TerminalEntity handler, String command )
	{
		Validate.notNull( handler, "Handler cannot be null" );
		Validate.notNull( command, "CommandLine cannot be null" );

		Log.get().fine( "The remote connection '" + handler + "' issued the command '" + command + "'." );

		pendingCommands.add( new CommandContext( handler, command ) );
	}

	public static void registerCommand( Command command )
	{
		if ( getCommand( command.getName() ) == null )
			registeredCommands.add( command );
	}
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package com.chiorichan.terminal.commands;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.chiorichan.account.AccountAttachment;
import com.chiorichan.account.AccountManager;
import com.chiorichan.account.AccountType;
import com.chiorichan.account.auth.AccountAuthenticator;
import com.chiorichan.account.lang.AccountDescriptiveReason;
import com.chiorichan.account.lang.AccountException;
import com.chiorichan.account.lang.AccountResult;
import com.chiorichan.lang.EnumColor;
import com.chiorichan.permission.PermissionDefault;
import com.chiorichan.terminal.CommandDispatch;
import com.chiorichan.terminal.Terminal;
import com.chiorichan.terminal.TerminalInterviewer;

/**
 * Used to login an account to the console
 */
class LoginCommand extends BuiltinCommand
{
	class LoginInterviewerPass implements TerminalInterviewer
	{
		private AccountAttachment sender;
		
		public LoginInterviewerPass( AccountAttachment sender )
		{
			this.sender = sender;
		}
		
		@Override
		public String getPrompt()
		{
			return "Password for " + sender.getVariable( "user" ) + ": ";
		}
		
		@Override
		public boolean handleInput( String input )
		{
			String user = sender.getVariable( "user" );
			String pass = input;
			
			try
			{
				if ( user != null && pass != null )
				{
					// TODO Unregister Terminal from NONE account, for that matter unregister before overriding any account
					
					AccountResult result = sender.getPermissible().loginWithException( AccountAuthenticator.PASSWORD, user, pass );
					
					if ( !sender.getPermissible().checkPermission( PermissionDefault.QUERY.getNode() ).isTrue() )
						throw new AccountException( AccountDescriptiveReason.UNAUTHORIZED, sender.meta() );
					
					AccountManager.getLogger().info( EnumColor.GREEN + "Successful Console Login [username='" + user + "',password='" + pass + "',userId='" + result.getAccount().getId() + "',displayName='" + result.getAccount().getDisplayName() + "']" );
					
					sender.sendMessage( EnumColor.GREEN + "Welcome " + user + ", you have been successfully logged in." );
				}
			}
			catch ( AccountException l )
			{
				if ( l.getAccount() != null )
					AccountManager.getLogger().warning( EnumColor.GREEN + "Failed Console Login [username='" + user + "',password='" + pass + "',userId='" + l.getAccount().getId() + "',displayName='" + l.getAccount().getDisplayName() + "',reason='" + l.getMessage() + "']" );
				
				sender.sendMessage( EnumColor.YELLOW + l.getMessage() );
				
				if ( !AccountType.isNoneAccount( sender ) )
					sender.getPermissible().login( AccountAuthenticator.NULL, AccountType.ACCOUNT_NONE.getId() );
				
				return true;
			}
			
			sender.setVariable( "user", null );
			return true;
		}
	}
	
	class LoginInterviewerUser implements TerminalInterviewer
	{
		private AccountAttachment sender;
		
		public LoginInterviewerUser( AccountAttachment sender )
		{
			this.sender = sender;
		}
		
		@Override
		public String getPrompt()
		{
			try
			{
				return InetAddress.getLocalHost().getHostName() + " login: ";
			}
			catch ( UnknownHostException e )
			{
				return "login: ";
			}
		}
		
		@Override
		public boolean handleInput( String input )
		{
			if ( input == null || input.isEmpty() )
			{
				sender.sendMessage( "Username can't be empty!" );
				return true;
			}
			
			sender.setVariable( "user", input );
			return true;
		}
	}
	
	public LoginCommand()
	{
		super( "login" );
	}
	
	@Override
	public boolean execute( AccountAttachment sender, String command, String[] args )
	{
		if ( sender instanceof Terminal )
		{
			CommandDispatch.addInterviewer( ( Terminal ) sender, new LoginInterviewerUser( sender ) );
			CommandDispatch.addInterviewer( ( Terminal ) sender, new LoginInterviewerPass( sender ) );
		}
		
		return true;
	}
}

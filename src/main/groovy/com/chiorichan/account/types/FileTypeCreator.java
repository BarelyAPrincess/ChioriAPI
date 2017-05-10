/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package com.chiorichan.account.types;

import com.chiorichan.AppConfig;
import com.chiorichan.account.AccountContext;
import com.chiorichan.account.AccountLocation;
import com.chiorichan.account.AccountManager;
import com.chiorichan.account.AccountMeta;
import com.chiorichan.account.AccountPermissible;
import com.chiorichan.account.AccountType;
import com.chiorichan.account.LocationService;
import com.chiorichan.account.lang.AccountDescriptiveReason;
import com.chiorichan.account.lang.AccountException;
import com.chiorichan.account.lang.AccountResolveResult;
import com.chiorichan.account.lang.AccountResult;
import com.chiorichan.configuration.types.yaml.YamlConfiguration;
import com.chiorichan.lang.ReportingLevel;
import com.chiorichan.lang.UncaughtException;
import com.chiorichan.permission.PermissibleEntity;
import com.chiorichan.permission.Permission;
import com.chiorichan.permission.PermissionDefault;
import com.chiorichan.services.AppManager;
import com.chiorichan.tasks.Timings;
import com.chiorichan.utils.UtilIO;
import com.chiorichan.utils.UtilObjects;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Handles Accounts that are loaded from File
 */
public class FileTypeCreator extends AccountTypeCreator
{
	public static final AccountTypeCreator INSTANCE = new FileTypeCreator();

	private class AccountFile
	{
		File file;
		WeakReference<AccountContext> context;

		AccountFile( File file )
		{
			UtilObjects.notFalse( file.exists() );

			YamlConfiguration yser = YamlConfiguration.loadConfiguration( file );

			Map<String, Object> contents = new HashMap<>();

			for ( String key : yser.getKeys( false ) )
				contents.put( key, yser.get( key ) );

			AccountContextImpl context = new AccountContextImpl( FileTypeCreator.this, AccountType.FILE );

			context.setAcctId( yser.getString( "acctId" ) );
			context.setLocationId( yser.getString( "locId" ) );

			context.setValues( contents );

			this.file = file;
			this.context = new WeakReference<>( context );
		}

		AccountContext getContext()
		{
			return context.get();
		}

		String getLocId()
		{
			return context.get() == null ? null : context.get().getLocId();
		}

		String getAcctId()
		{
			return context.get() == null ? null : context.get().getAcctId();
		}

		File getFile()
		{
			return file;
		}
	}

	private File accountsDirectory = null;

	private List<AccountFile> preloaded = new CopyOnWriteArrayList<>();

	public FileTypeCreator()
	{
		accountsDirectory = AppConfig.get().getDirectory( "accounts.directory", "accounts" );

		if ( !UtilIO.setDirectoryAccess( accountsDirectory ) )
			throw new UncaughtException( ReportingLevel.E_ERROR, "This application experienced a problem setting read and write access to directory \"" + UtilIO.relPath( accountsDirectory ) + "\"!" );

		preloadCheck( accountsDirectory );
	}

	public void preloadCheck( File fileBase )
	{
		UtilObjects.notNull( fileBase );

		File[] files = fileBase.listFiles();

		/* Remove invalidated contexts */
		for ( AccountFile af : preloaded )
			if ( af.getContext() == null )
				preloaded.remove( af );

		if ( files == null )
			return;

		for ( File f : files )
			if ( FileFilterUtils.and( FileFilterUtils.suffixFileFilter( "yaml" ), FileFilterUtils.fileFileFilter() ).accept( f ) )
			{
				boolean found = false;
				for ( AccountFile accountFile : preloaded )
					if ( f.equals( accountFile.getFile() ) )
						found = true;
				AccountFile newAccountFile = new AccountFile( f );
				for ( AccountFile accountFile : preloaded )
					if ( Objects.equals( newAccountFile.getLocId(), accountFile.getLocId() ) && Objects.equals( newAccountFile.getAcctId(), accountFile.getAcctId() ) )
					{
						AccountManager.getLogger().severe( "Duplicate accounts (locId and acctId) were detected in files [" + accountFile.getFile().getName() + "] and [" + f.getName() + "]. Second account was ignored." );
						found = true;
					}
				if ( !found )
					preloaded.add( newAccountFile );
			}
	}

	@Override
	public boolean accountExists( String locId, String acctId )
	{
		AccountLocation location = resolveLocId( locId );
		File fileBase = null;

		if ( location != null )
			fileBase = location.getAccountDirectory();
		if ( fileBase == null )
			fileBase = accountsDirectory;

		preloadCheck( fileBase );

		for ( AccountFile accountFile : preloaded )
			if ( Objects.equals( accountFile.getAcctId(), acctId ) && ( Objects.equals( accountFile.getLocId(), locId ) || "%".equals( accountFile.getLocId() ) ) )
				return true;
		return false;
	}

	@Override
	public String getDisplayName( AccountMeta meta )
	{
		if ( meta.getString( "fname" ) != null && !meta.getString( "fname" ).isEmpty() && meta.getString( "name" ) != null && !meta.getString( "name" ).isEmpty() )
			return meta.getString( "fname" ) + " " + meta.getString( "name" );

		if ( meta.getString( "name" ) != null && !meta.getString( "name" ).isEmpty() )
			return meta.getString( "name" );

		if ( meta.getString( "email" ) != null && !meta.getString( "email" ).isEmpty() )
			return meta.getString( "email" );

		return null;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	@Override
	public AccountResolveResult resolveAccount( String locId, String acctId )
	{
		try
		{
			return new AccountResolveResult( readAccount( locId, acctId ), AccountDescriptiveReason.LOGIN_SUCCESS );
		}
		catch ( AccountException e )
		{
			if ( e.getReason() == AccountDescriptiveReason.INTERNAL_ERROR )
				return new AccountResolveResult( null, e );
			else
				return new AccountResolveResult( null, e.getReason() );
		}
	}

	@Override
	public void preLogin( AccountMeta meta, AccountPermissible via, String acctId, Object... credentials ) throws AccountException
	{
		if ( meta.getInteger( "numLoginFail" ) > 5 )
			if ( meta.getInteger( "lastLoginFail" ) > Timings.epoch() - 1800 )
				throw new AccountException( AccountDescriptiveReason.UNDER_ATTACK, meta );

		if ( !meta.getString( "actkey" ).equals( "" ) )
			throw new AccountException( AccountDescriptiveReason.ACCOUNT_NOT_ACTIVATED, meta );
	}

	public AccountLocation resolveLocId( String locId )
	{
		LocationService service = AppManager.getService( AccountLocation.class );
		return UtilObjects.isEmpty( locId ) ? null : service == null ? null : service.getLocation( locId );
	}

	public AccountContext readAccount( String locId, String acctId ) throws AccountException
	{
		if ( UtilObjects.isEmpty( acctId ) )
			throw new AccountException( AccountDescriptiveReason.EMPTY_ID, locId, acctId );

		Set<String> loginKeys = new HashSet<>( getLoginKeys() );

		loginKeys.add( "acctId" );
		loginKeys.add( "username" );

		AccountLocation location = resolveLocId( locId );
		File fileBase = null;

		if ( location != null )
		{
			Set<String> additionalLoginKeys = location.getAccountFields();
			if ( additionalLoginKeys != null )
				loginKeys.addAll( additionalLoginKeys );

			fileBase = location.getAccountDirectory();
		}
		if ( fileBase == null )
			fileBase = accountsDirectory;

		preloadCheck( fileBase );

		AccountFile found = null;

		for ( AccountFile accountFile : preloaded )
			for ( String loginKey : loginKeys )
				if ( accountFile.getContext().getValue( loginKey ) != null && ( "%".equals( accountFile.getLocId() ) || locId.equals( accountFile.getLocId() ) ) && acctId.equals( UtilObjects.castToString( accountFile.getContext().getValue( loginKey ) ) ) )
					found = accountFile;

		if ( found == null )
			throw new AccountException( AccountDescriptiveReason.INCORRECT_LOGIN, locId, acctId );

		return found.getContext();
	}

	@Override
	public void reload( AccountMeta meta ) throws AccountException
	{
		for ( AccountFile accountFile : preloaded )
			if ( accountFile.getContext() == meta.getContext() || ( Objects.equals( accountFile.getLocId(), meta.getLocId() ) && Objects.equals( accountFile.getAcctId(), meta.getId() ) ) )
			{
				YamlConfiguration yser = YamlConfiguration.loadConfiguration( accountFile.getFile() );

				if ( yser == null )
					throw new AccountException( new AccountDescriptiveReason( "The file for this Account Meta Data is missing, was it deleted.", ReportingLevel.L_ERROR ), meta );

				for ( String key : yser.getKeys( false ) )
					meta.set( key, yser.get( key ) );

				break;
			}
	}

	@Override
	public void save( AccountContext context )
	{
		if ( context == null )
			return;

		updateMeta( context.meta() );
	}

	@Override
	public void successInit( AccountMeta meta, PermissibleEntity entity )
	{
		Permission userNode = PermissionDefault.USER.getNode();
		if ( meta.getContext().creator() == this && !entity.checkPermission( userNode ).isAssigned() )
			entity.addPermission( userNode, true, null );
	}

	@Override
	public void failedLogin( AccountMeta meta, AccountResult result )
	{
		if ( meta == null )
			return;

		meta.set( "lastLoginFail", Timings.epoch() );
		meta.set( "numLoginFail", meta.getInteger( "numLoginFail", 0 ) + 1 );

		updateMeta( meta );
	}

	@Override
	public void successLogin( AccountMeta meta )
	{
		if ( meta == null )
			return;

		meta.set( "lastActive", Timings.epoch() );
		meta.set( "lastLogin", Timings.epoch() );
		meta.set( "lastLoginFail", 0 );
		meta.set( "numLoginFail", 0 );

		updateMeta( meta );
	}

	private void updateMeta( AccountMeta meta )
	{
		for ( AccountFile accountFile : preloaded )
			if ( accountFile.getContext() == meta.getContext() || ( Objects.equals( accountFile.getLocId(), meta.getLocId() ) && Objects.equals( accountFile.getAcctId(), meta.getId() ) ) )
			{
				YamlConfiguration yser = new YamlConfiguration();

				yser.set( meta.getMeta() );
				yser.set( "locId", meta.getLocId() );
				yser.set( "acctId", meta.getId() );

				try
				{
					yser.save( accountFile.getFile() );
				}
				catch ( IOException e )
				{
					e.printStackTrace();
				}

				break;
			}
	}
}

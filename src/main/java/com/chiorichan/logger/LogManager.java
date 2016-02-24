package com.chiorichan.logger;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chiorichan.AppController;
import com.chiorichan.util.FileFunc;

public class LogManager
{
	public static void cleanupLogs( final String suffix, int limit )
	{
		File[] files = AppController.config().logsDirectory().listFiles( new FilenameFilter()
		{
			@Override
			public boolean accept( File dir, String name )
			{
				return name.toLowerCase().endsWith( suffix );
			}
		} );

		if ( files.length < 1 )
			return;

		// Delete all logs, no archiving!
		if ( limit < 1 )
		{
			for ( File f : files )
				f.delete();
			return;
		}

		FileFunc.SortableFile[] sfiles = new FileFunc.SortableFile[files.length];

		for ( int i = 0; i < files.length; i++ )
			sfiles[i] = new FileFunc.SortableFile( files[i] );

		Arrays.sort( sfiles );

		if ( sfiles.length > limit )
			for ( int i = 0; i < sfiles.length - limit; i++ )
				sfiles[i].f.delete();
	}

	public LogManager()
	{
		for ( Handler h : logger.getHandlers() )
			logger.removeHandler( h );
	}

	public void addFileHandler( String filename, boolean useColor, int archiveLimit, Level level )
	{
		try
		{
			File log = new File( AppController.config().logsDirectory(), filename + ".log" );

			if ( log.exists() )
			{
				if ( archiveLimit > 0 )
					FileFunc.gzFile( log, new File( AppController.config().logsDirectory(), new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" ).format( new Date() ) + "-" + filename + ".log.gz" ) );
				log.delete();
			}

			cleanupLogs( "-" + filename + ".log.gz", archiveLimit );

			FileHandler fileHandler = new FileHandler( log.getAbsolutePath() );
			fileHandler.setLevel( level );
			fileHandler.setFormatter( new DefaultLogFormatter( useColor ) );

			addHandler( fileHandler );
		}
		catch ( Exception e )
		{
			getLogger().severe( "Failed to log to '" + filename + ".log'", e );
		}
	}

	public void addHandler( Handler h )
	{
		logger.addHandler( h );
	}

	public Logger getParent()
	{
		return logger;
	}

	public void removeHandler( Handler h )
	{
		logger.removeHandler( h );
	}
}

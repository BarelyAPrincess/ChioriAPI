/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.datastore;

import java.sql.SQLException;
import java.util.List;

import com.chiorichan.ServiceManager;
import com.chiorichan.lang.ExceptionCallback;
import com.chiorichan.lang.ExceptionContext;
import com.chiorichan.lang.ExceptionReport;
import com.chiorichan.lang.ReportingLevel;
import com.chiorichan.logger.Log;
import com.chiorichan.logger.LogSource;
import com.chiorichan.services.AppManager;
import com.chiorichan.tasks.TaskRegistrar;
import com.google.common.collect.Lists;

/**
 *
 */
public class DatastoreManager implements ServiceManager, TaskRegistrar, LogSource
{
	public static Log getLogger()
	{
		return AppManager.manager( DatastoreManager.class ).getLogger();
	}

	public static DatastoreManager instance()
	{
		return AppManager.manager( DatastoreManager.class ).instance();
	}

	List<Datastore> datastores = Lists.newArrayList();

	private DatastoreManager()
	{

	}

	@Override
	public String getLoggerId()
	{
		return "DsMgr";
	}

	@Override
	public String getName()
	{
		return "DatastoreManger";
	}

	@Override
	public void init()
	{
		ExceptionReport.registerException( new ExceptionCallback()
		{
			@Override
			public ReportingLevel callback( Throwable cause, ExceptionReport report, ExceptionContext context )
			{
				report.addException( ReportingLevel.E_ERROR, cause );
				return ReportingLevel.E_ERROR;
			}
		}, SQLException.class );
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}
}

package com.chiorichan;

import com.chiorichan.lang.ApplicationException;
import com.chiorichan.logger.LogSource;

public interface ServiceManager extends LogSource
{
	public void init() throws ApplicationException;
}

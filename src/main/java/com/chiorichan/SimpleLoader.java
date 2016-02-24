package com.chiorichan;

import com.chiorichan.event.EventHandler;
import com.chiorichan.event.EventPriority;
import com.chiorichan.event.application.RunlevelEvent;
import com.chiorichan.lang.ApplicationException;

public class SimpleLoader extends AppLoader
{
	@Override
	@EventHandler( priority = EventPriority.NORMAL )
	public void onRunlevelChange( RunlevelEvent event ) throws ApplicationException
	{

	}
}

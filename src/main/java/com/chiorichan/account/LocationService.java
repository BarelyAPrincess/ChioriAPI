package com.chiorichan.account;

import java.util.List;

public interface LocationService
{
	AccountLocation getLocation( String locationId );

	AccountLocation getDefaultLocation();

	List<AccountLocation> getLocations();
}

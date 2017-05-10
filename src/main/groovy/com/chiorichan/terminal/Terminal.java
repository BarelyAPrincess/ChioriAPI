/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.terminal;

import com.chiorichan.account.AccountAttachment;
import com.chiorichan.account.Kickable;

public interface Terminal extends AccountAttachment, Kickable
{
	void prompt();
	
	void resetPrompt();
	
	void setPrompt( String prompt );
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package joptsimple.internal;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class Messages
{
	public static String message( Locale locale, String bundleName, Class<?> type, String key, Object... args )
	{
		ResourceBundle bundle = ResourceBundle.getBundle( bundleName, locale );
		String template = bundle.getString( type.getName() + '.' + key );
		MessageFormat format = new MessageFormat( template );
		format.setLocale( locale );
		return format.format( args );
	}

	private Messages()
	{
		throw new UnsupportedOperationException();
	}
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.fusesource.hawtjni.runtime;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@Target( {FIELD} )
@Retention( RetentionPolicy.RUNTIME )
public @interface JniField
{
	String accessor() default "";

	String cast() default "";

	String conditional() default "";

	FieldFlag[] flags() default {};
}

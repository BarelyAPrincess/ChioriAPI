/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.fusesource.hawtjni.runtime;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@Target( {METHOD} )
@Retention( RetentionPolicy.RUNTIME )
public @interface JniMethod
{
	//    Pointer pointer() default Pointer.DETERMINE_FROM_CAST;
	String accessor() default "";

	JniArg[] callbackArgs() default {};

	String cast() default "";

	String conditional() default "";

	String copy() default "";

	MethodFlag[] flags() default {};
}

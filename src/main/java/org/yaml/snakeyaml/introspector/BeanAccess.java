/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.introspector;

/**
 * Control instance variables.
 */
public enum BeanAccess {
    /** use JavaBean properties and public fields */
    DEFAULT,

    /** use all declared fields (including inherited) */
    FIELD,

    /** reserved */
    PROPERTY;
}
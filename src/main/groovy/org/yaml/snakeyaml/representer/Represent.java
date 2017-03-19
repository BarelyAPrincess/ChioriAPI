/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.representer;

import org.yaml.snakeyaml.nodes.Node;

/**
 * Create a Node Graph out of the provided Native Data Structure (Java
 * instance).
 * 
 * @see <a href="http://yaml.org/spec/1.1/#id859109">Chapter 3. Processing YAML
 *      Information</a>
 */
public interface Represent {
    /**
     * Create a Node
     * 
     * @param data
     *            the instance to represent
     * @return Node to dump
     */
    Node representData(Object data);
}

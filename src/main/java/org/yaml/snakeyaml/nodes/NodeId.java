/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.nodes;

/**
 * Enum for the three basic YAML types: scalar, sequence and mapping.
 */
public enum NodeId {
    scalar, sequence, mapping, anchor;
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.nodes;

import org.yaml.snakeyaml.error.Mark;

/**
 * Base class for the two collection types {@link MappingNode mapping} and
 * {@link SequenceNode collection}.
 */
public abstract class CollectionNode extends Node {
    private Boolean flowStyle;

    public CollectionNode(Tag tag, Mark startMark, Mark endMark, Boolean flowStyle) {
        super(tag, startMark, endMark);
        this.flowStyle = flowStyle;
    }

    /**
     * Serialization style of this collection.
     * 
     * @return <code>true</code> for flow style, <code>false</code> for block
     *         style.
     */
    public Boolean getFlowStyle() {
        return flowStyle;
    }

    public void setFlowStyle(Boolean flowStyle) {
        this.flowStyle = flowStyle;
    }

    public void setEndMark(Mark endMark) {
        this.endMark = endMark;
    }
}

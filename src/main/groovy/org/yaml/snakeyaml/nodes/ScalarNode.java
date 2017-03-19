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
 * Represents a scalar node.
 * <p>
 * Scalar nodes form the leaves in the node graph.
 * </p>
 */
public class ScalarNode extends Node {
    private Character style;
    private String value;

    public ScalarNode(Tag tag, String value, Mark startMark, Mark endMark, Character style) {
        this(tag, true, value, startMark, endMark, style);
    }

    public ScalarNode(Tag tag, boolean resolved, String value, Mark startMark, Mark endMark,
            Character style) {
        super(tag, startMark, endMark);
        if (value == null) {
            throw new NullPointerException("value in a Node is required.");
        }
        this.value = value;
        this.style = style;
        this.resolved = resolved;
    }

    /**
     * Get scalar style of this node.
     * 
     * @see org.yaml.snakeyaml.events.ScalarEvent
     * @see <a href="http://yaml.org/spec/1.1/#id903915">Chapter 9. Scalar
     *      Styles</a>
     */
    public Character getStyle() {
        return style;
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.scalar;
    }

    /**
     * Value of this scalar.
     * 
     * @return Scalar's value.
     */
    public String getValue() {
        return value;
    }

    public String toString() {
        return "<" + this.getClass().getName() + " (tag=" + getTag() + ", value=" + getValue()
                + ")>";
    }
}

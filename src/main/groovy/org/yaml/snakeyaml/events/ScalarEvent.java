/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

/**
 * Marks a scalar value.
 */
public final class ScalarEvent extends NodeEvent {
    private final String tag;
    // style flag of a scalar event indicates the style of the scalar. Possible
    // values are None, '', '\'', '"', '|', '>'
    private final Character style;
    private final String value;
    // The implicit flag of a scalar event is a pair of boolean values that
    // indicate if the tag may be omitted when the scalar is emitted in a plain
    // and non-plain style correspondingly.
    private final ImplicitTuple implicit;

    public ScalarEvent(String anchor, String tag, ImplicitTuple implicit, String value,
            Mark startMark, Mark endMark, Character style) {
        super(anchor, startMark, endMark);
        this.tag = tag;
        this.implicit = implicit;
        this.value = value;
        this.style = style;
    }

    /**
     * Tag of this scalar.
     * 
     * @return The tag of this scalar, or <code>null</code> if no explicit tag
     *         is available.
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * Style of the scalar.
     * <dl>
     * <dt>null</dt>
     * <dd>Flow Style - Plain</dd>
     * <dt>'\''</dt>
     * <dd>Flow Style - Single-Quoted</dd>
     * <dt>'"'</dt>
     * <dd>Flow Style - Double-Quoted</dd>
     * <dt>'|'</dt>
     * <dd>Block Style - Literal</dd>
     * <dt>'>'</dt>
     * <dd>Block Style - Folded</dd>
     * </dl>
     * 
     * @see <a href="http://yaml.org/spec/1.1/#id864487">Kind/Style
     *      Combinations</a>
     * @return Style of the scalar.
     */
    public Character getStyle() {
        return this.style;
    }

    /**
     * String representation of the value.
     * <p>
     * Without quotes and escaping.
     * </p>
     * 
     * @return Value as Unicode string.
     */
    public String getValue() {
        return this.value;
    }

    public ImplicitTuple getImplicit() {
        return this.implicit;
    }

    @Override
    protected String getArguments() {
        return super.getArguments() + ", tag=" + tag + ", " + implicit + ", value=" + value;
    }

    @Override
    public boolean is(Event.ID id) {
        return ID.Scalar == id;
    }
}

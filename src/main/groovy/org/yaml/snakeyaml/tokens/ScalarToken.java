/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;

public final class ScalarToken extends Token {
    private final String value;
    private final boolean plain;
    private final char style;

    public ScalarToken(String value, Mark startMark, Mark endMark, boolean plain) {
        this(value, plain, startMark, endMark, (char) 0);
    }

    public ScalarToken(String value, boolean plain, Mark startMark, Mark endMark, char style) {
        super(startMark, endMark);
        this.value = value;
        this.plain = plain;
        this.style = style;
    }

    public boolean getPlain() {
        return this.plain;
    }

    public String getValue() {
        return this.value;
    }

    public char getStyle() {
        return this.style;
    }

    @Override
    protected String getArguments() {
        return "value=" + value + ", plain=" + plain + ", style=" + style;
    }

    @Override
    public Token.ID getTokenId() {
        return ID.Scalar;
    }
}

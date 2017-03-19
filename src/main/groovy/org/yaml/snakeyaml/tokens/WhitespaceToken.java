/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;

public class WhitespaceToken extends Token {
    public WhitespaceToken(Mark startMark, Mark endMark) {
        super(startMark, endMark);
    }

    @Override
    public ID getTokenId() {
        return ID.Whitespace;
    }
}

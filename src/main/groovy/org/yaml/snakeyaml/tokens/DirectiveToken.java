/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.tokens;

import java.util.List;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;

public final class DirectiveToken<T> extends Token {
    private final String name;
    private final List<T> value;

    public DirectiveToken(String name, List<T> value, Mark startMark, Mark endMark) {
        super(startMark, endMark);
        this.name = name;
        if (value != null && value.size() != 2) {
            throw new YAMLException("Two strings must be provided instead of "
                    + String.valueOf(value.size()));
        }
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public List<T> getValue() {
        return this.value;
    }

    @Override
    protected String getArguments() {
        if (value != null) {
            return "name=" + name + ", value=[" + value.get(0) + ", " + value.get(1) + "]";
        } else {
            return "name=" + name;
        }
    }

    @Override
    public Token.ID getTokenId() {
        return ID.Directive;
    }
}

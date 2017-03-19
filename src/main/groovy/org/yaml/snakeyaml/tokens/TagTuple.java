/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.tokens;

public final class TagTuple {
    private final String handle;
    private final String suffix;

    public TagTuple(String handle, String suffix) {
        if (suffix == null) {
            throw new NullPointerException("Suffix must be provided.");
        }
        this.handle = handle;
        this.suffix = suffix;
    }

    public String getHandle() {
        return handle;
    }

    public String getSuffix() {
        return suffix;
    }
}

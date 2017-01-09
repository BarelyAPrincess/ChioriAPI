/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.resolver;

import java.util.regex.Pattern;

import org.yaml.snakeyaml.nodes.Tag;

final class ResolverTuple {
    private final Tag tag;
    private final Pattern regexp;

    public ResolverTuple(Tag tag, Pattern regexp) {
        this.tag = tag;
        this.regexp = regexp;
    }

    public Tag getTag() {
        return tag;
    }

    public Pattern getRegexp() {
        return regexp;
    }

    @Override
    public String toString() {
        return "Tuple tag=" + tag + " regexp=" + regexp;
    }
}
/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.parser;

import java.util.Map;

import org.yaml.snakeyaml.DumperOptions.Version;

/**
 * Store the internal state for directives
 */
class VersionTagsTuple {
    private Version version;
    private Map<String, String> tags;

    public VersionTagsTuple(Version version, Map<String, String> tags) {
        this.version = version;
        this.tags = tags;
    }

    public Version getVersion() {
        return version;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return String.format("VersionTagsTuple<%s, %s>", version, tags);
    }
}

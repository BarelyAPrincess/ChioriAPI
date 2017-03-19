/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.events;

import java.util.Map;

import org.yaml.snakeyaml.DumperOptions.Version;
import org.yaml.snakeyaml.error.Mark;

/**
 * Marks the beginning of a document.
 * <p>
 * This event followed by the document's content and a {@link DocumentEndEvent}.
 * </p>
 */
public final class DocumentStartEvent extends Event {
    private final boolean explicit;
    private final Version version;
    private final Map<String, String> tags;

    public DocumentStartEvent(Mark startMark, Mark endMark, boolean explicit, Version version,
            Map<String, String> tags) {
        super(startMark, endMark);
        this.explicit = explicit;
        this.version = version;
        // TODO enforce not null
        // if (tags == null) {
        // throw new NullPointerException("Tags must be provided.");
        // }
        this.tags = tags;
    }

    public boolean getExplicit() {
        return explicit;
    }

    /**
     * YAML version the document conforms to.
     * 
     * @return <code>null</code>if the document has no explicit
     *         <code>%YAML</code> directive. Otherwise an array with two
     *         components, the major and minor part of the version (in this
     *         order).
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Tag shorthands as defined by the <code>%TAG</code> directive.
     * 
     * @return Mapping of 'handles' to 'prefixes' (the handles include the '!'
     *         characters).
     */
    public Map<String, String> getTags() {
        return tags;
    }

    @Override
    public boolean is(Event.ID id) {
        return ID.DocumentStart == id;
    }
}

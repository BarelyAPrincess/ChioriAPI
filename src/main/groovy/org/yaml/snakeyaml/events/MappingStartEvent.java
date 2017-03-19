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
 * Marks the beginning of a mapping node.
 * <p>
 * This event is followed by a number of key value pairs. <br>
 * The pairs are not in any particular order. However, the value always directly
 * follows the corresponding key. <br>
 * After the key value pairs follows a {@link MappingEndEvent}.
 * </p>
 * <p>
 * There must be an even number of node events between the start and end event.
 * </p>
 * 
 * @see MappingEndEvent
 */
public final class MappingStartEvent extends CollectionStartEvent {
    public MappingStartEvent(String anchor, String tag, boolean implicit, Mark startMark,
            Mark endMark, Boolean flowStyle) {
        super(anchor, tag, implicit, startMark, endMark, flowStyle);
    }

    @Override
    public boolean is(Event.ID id) {
        return ID.MappingStart == id;
    }
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.emitter;

import java.io.IOException;

import org.yaml.snakeyaml.events.Event;

public interface Emitable {
    void emit(Event event) throws IOException;
}

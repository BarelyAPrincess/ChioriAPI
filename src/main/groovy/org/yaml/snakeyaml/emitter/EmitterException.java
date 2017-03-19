/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.emitter;

import org.yaml.snakeyaml.error.YAMLException;

public class EmitterException extends YAMLException {
    private static final long serialVersionUID = -8280070025452995908L;

    public EmitterException(String msg) {
        super(msg);
    }
}

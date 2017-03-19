/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.error;

public class YAMLException extends RuntimeException {
    private static final long serialVersionUID = -4738336175050337570L;

    public YAMLException(String message) {
        super(message);
    }

    public YAMLException(Throwable cause) {
        super(cause);
    }

    public YAMLException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.reader;

import org.yaml.snakeyaml.error.YAMLException;

public class ReaderException extends YAMLException {
    private static final long serialVersionUID = 8710781187529689083L;
    private final String name;
    private final char character;
    private final int position;

    public ReaderException(String name, int position, char character, String message) {
        super(message);
        this.name = name;
        this.character = character;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public char getCharacter() {
        return character;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "unacceptable character '" + character + "' (0x"
                + Integer.toHexString((int) character).toUpperCase() + ") " + getMessage()
                + "\nin \"" + name + "\", position " + position;
    }
}

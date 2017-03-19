/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.introspector;

/**
 * A property that does not map to a real property; this is used when {@link
 * PropertyUtils.setSkipMissingProperties(boolean)} is set to true.
 */
public class MissingProperty extends Property {

    public MissingProperty(String name) {
        super(name, Object.class);
    }

    @Override
    public Class<?>[] getActualTypeArguments() {
        return new Class[0];
    }

    /**
     * Setter does nothing.
     */
    @Override
    public void set(Object object, Object value) throws Exception {
    }

    @Override
    public Object get(Object object) {
        return object;
    }
}

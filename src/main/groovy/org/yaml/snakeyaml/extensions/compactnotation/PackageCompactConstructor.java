/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.extensions.compactnotation;

public class PackageCompactConstructor extends CompactConstructor {
    private String packageName;

    public PackageCompactConstructor(String packageName) {
        this.packageName = packageName;
    }

    @Override
    protected Class<?> getClassForName(String name) throws ClassNotFoundException {
        if (name.indexOf('.') < 0) {
            try {
                Class<?> clazz = Class.forName(packageName + "." + name);
                return clazz;
            } catch (ClassNotFoundException e) {
                // use super implementation
            }
        }
        return super.getClassForName(name);
    }
}

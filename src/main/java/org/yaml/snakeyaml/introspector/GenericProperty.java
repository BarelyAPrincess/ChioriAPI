/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.introspector;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

abstract public class GenericProperty extends Property {

    private Type genType;

    public GenericProperty(String name, Class<?> aClass, Type aType) {
        super(name, aClass);
        genType = aType;
        actualClassesChecked = aType == null;
    }

    private boolean actualClassesChecked;
    private Class<?>[] actualClasses;

    public Class<?>[] getActualTypeArguments() { // should we synchronize here ?
        if (!actualClassesChecked) {
            if (genType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genType;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 0) {
                    actualClasses = new Class<?>[actualTypeArguments.length];
                    for (int i = 0; i < actualTypeArguments.length; i++) {
                        if (actualTypeArguments[i] instanceof Class<?>) {
                            actualClasses[i] = (Class<?>) actualTypeArguments[i];
                        } else if (actualTypeArguments[i] instanceof ParameterizedType) {
                            actualClasses[i] = (Class<?>) ((ParameterizedType) actualTypeArguments[i])
                                    .getRawType();
                        } else if (actualTypeArguments[i] instanceof GenericArrayType) {
                            Type componentType = ((GenericArrayType) actualTypeArguments[i])
                                    .getGenericComponentType();
                            if (componentType instanceof Class<?>) {
                                actualClasses[i] = Array.newInstance((Class<?>) componentType, 0)
                                        .getClass();
                            } else {
                                actualClasses = null;
                                break;
                            }
                        } else {
                            actualClasses = null;
                            break;
                        }
                    }
                }
            } else if (genType instanceof GenericArrayType) {
                Type componentType = ((GenericArrayType) genType).getGenericComponentType();
                if (componentType instanceof Class<?>) {
                    actualClasses = new Class<?>[] { (Class<?>) componentType };
                }
            } else if (genType instanceof Class<?>) {// XXX this check is only
                                                     // required for IcedTea6
                Class<?> classType = (Class<?>) genType;
                if (classType.isArray()) {
                    actualClasses = new Class<?>[1];
                    actualClasses[0] = getType().getComponentType();
                }
            }
            actualClassesChecked = true;
        }
        return actualClasses;
    }
}

/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.util;

import java.util.ArrayList;

public class ArrayStack<T> {
    private ArrayList<T> stack;

    public ArrayStack(int initSize) {
        stack = new ArrayList<T>(initSize);
    }

    public void push(T obj) {
        stack.add(obj);
    }

    public T pop() {
        return stack.remove(stack.size() - 1);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public void clear() {
        stack.clear();
    }
}

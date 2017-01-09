/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.parser;

import org.yaml.snakeyaml.events.Event;

/**
 * Helper for {@link ParserImpl}. A grammar rule to apply given the symbols on
 * top of its stack and the next input token
 * 
 * @see <a href="http://en.wikipedia.org/wiki/LL_parser"></a>
 */
interface Production {
    Event produce();
}

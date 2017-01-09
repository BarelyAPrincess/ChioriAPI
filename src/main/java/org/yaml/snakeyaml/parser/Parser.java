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
 * This interface represents an input stream of {@link Event Events}.
 * <p>
 * The parser and the scanner form together the 'Parse' step in the loading
 * process (see chapter 3.1 of the <a href="http://yaml.org/spec/1.1/">YAML
 * Specification</a>).
 * </p>
 * 
 * @see org.yaml.snakeyaml.events.Event
 */
public interface Parser {

    /**
     * Check if the next event is one of the given type.
     * 
     * @param choice
     *            Event ID.
     * @return <code>true</code> if the next event can be assigned to a variable
     *         of the given type. Returns <code>false</code> if no more events
     *         are available.
     * @throws ParserException
     *             Thrown in case of malformed input.
     */
    public boolean checkEvent(Event.ID choice);

    /**
     * Return the next event, but do not delete it from the stream.
     * 
     * @return The event that will be returned on the next call to
     *         {@link #getEvent}
     * @throws ParserException
     *             Thrown in case of malformed input.
     */
    public Event peekEvent();

    /**
     * Returns the next event.
     * <p>
     * The event will be removed from the stream.
     * </p>
     * 
     * @throws ParserException
     *             Thrown in case of malformed input.
     */
    public Event getEvent();
}

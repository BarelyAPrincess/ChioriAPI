/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Rights Reserved
 */
package org.yaml.snakeyaml.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.external.com.google.gdata.util.common.base.Escaper;
import org.yaml.snakeyaml.external.com.google.gdata.util.common.base.PercentEscaper;

public abstract class UriEncoder {
    private static final CharsetDecoder UTF8Decoder = Charset.forName("UTF-8").newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT);
    // Include the [] chars to the SAFEPATHCHARS_URLENCODER to avoid
    // its escape as required by spec. See
    // http://yaml.org/spec/1.1/#escaping%20in%20URI/
    private static final String SAFE_CHARS = PercentEscaper.SAFEPATHCHARS_URLENCODER + "[]/";
    private static final Escaper escaper = new PercentEscaper(SAFE_CHARS, false);

    /**
     * Escape special characters with '%'
     */
    public static String encode(String uri) {
        return escaper.escape(uri);
    }

    /**
     * Decode '%'-escaped characters. Decoding fails in case of invalid UTF-8
     */
    public static String decode(ByteBuffer buff) throws CharacterCodingException {
        CharBuffer chars = UTF8Decoder.decode(buff);
        return chars.toString();
    }

    public static String decode(String buff) {
        try {
            return URLDecoder.decode(buff, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new YAMLException(e);
        }
    }
}

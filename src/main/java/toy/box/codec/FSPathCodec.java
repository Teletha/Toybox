/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toy.box.codec;

import kiss.Decoder;
import kiss.Encoder;
import toybox.filesystem.FSPath;

/**
 * @version 2017/02/06 1:52:14
 */
public class FSPathCodec implements Decoder<FSPath>, Encoder<FSPath> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String encode(FSPath value) {
        return value.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FSPath decode(String value) {
        return FSPath.locate(value);
    }
}

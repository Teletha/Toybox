/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox.filesystem;

import org.junit.Test;

/**
 * @version 2012/03/12 9:55:34
 */
public class FSPathListTest {

    @Test
    public void delete() throws Exception {
        FSPathList list = new FSPathList();
        list.insert(FSPath.locate("a"));
        list.insert(FSPath.locate("b"));
        list.insert(FSPath.locate("c"));

        assert 1 == list.delete(FSPath.locate("b"));
    }
}

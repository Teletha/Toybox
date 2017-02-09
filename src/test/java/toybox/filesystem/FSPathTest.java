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

import java.nio.file.FileSystems;
import java.util.List;

import org.junit.Test;

import consolio.filesystem.FSPath;

/**
 * @version 2012/03/07 20:40:20
 */
public class FSPathTest {

    @Test
    public void root() throws Exception {
        assert FSPath.Root != null;
        assert FSPath.Root == FSPath.Root.getParent();
        assert FSPath.Root.list().get(0).getParent() == FSPath.Root;
        assert FSPath.Root.list().get(0).getParent().getParent() == FSPath.Root;
        assert FSPath.Root.toString().equals("/");
        assert FSPath.locate(FSPath.Root.toString()) == FSPath.Root;
    }

    @Test
    public void drive() throws Exception {
        // Search root directory (in windows, first drive will be returned)
        FSPath path = FSPath.locate(FileSystems.getDefault().getRootDirectories().iterator().next());

        for (int i = 0; i < 30; i++) {
            path = path.getParent();
        }
        assert path == FSPath.Root;
    }

    @Test
    public void driveTraverse() throws Exception {
        // Search root directory (in windows, first drive will be returned)
        FSPath root = FSPath.locate(FileSystems.getDefault().getRootDirectories().iterator().next());
        FSPath parent = root.getParent();
        List<FSPath> children = parent.list();

        boolean match = false;

        for (FSPath child : children) {

            if (child.equals(root)) {
                match = true;
                assert parent.equals(child.getParent());
            }
        }
        assert match;
    }
}

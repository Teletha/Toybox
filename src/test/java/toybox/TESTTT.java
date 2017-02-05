/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenases/mit-license.php
 */
package toybox;

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * @version 2012/06/07 9:11:13
 */
public class TESTTT {

    public static void main(String[] args) throws Exception {
        System.out.println("oo");
        Path path = java.nio.file.Paths.get("test.zip");
        System.out.println(path);
        Path zip = FileSystems.newFileSystem(path, ClassLoader.getSystemClassLoader()).getPath("/");
        System.out.println(zip);
        System.out.println("end");
    }
}

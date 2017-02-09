/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package consolio.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import kiss.I;
import kiss.Manageable;
import kiss.Singleton;

/**
 * @version 2017/02/09 4:43:55
 */
@Manageable(lifestyle = Singleton.class)
public class Model {

    public List<Console> consoles = new ArrayList();

    public void restore() {
        try {
            I.read(config(), this);
        } catch (Throwable e) {

        }
    }

    public void store() {
        I.write(this, config());
    }

    public Path config() {
        Path path = I.locate("").toAbsolutePath().resolve("preferences").resolve(getClass().getName() + ".txt");
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                throw I.quiet(e);
            }
        }
        return path;
    }
}

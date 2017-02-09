/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package consolio.cli;

import java.nio.file.Files;
import java.nio.file.Path;

import consolio.Task;
import consolio.filesystem.FSPath;

/**
 * @version 2012/03/02 23:18:35
 */
public class Cd extends Task {

    @Argument
    private String path;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() {
        Path path = console.getContext().toPath().resolve(this.path).normalize();

        if (Files.notExists(path)) {
            ui.write(path + " is not found.");
        } else {
            console.setContext(FSPath.locate(path));
        }
    }
}

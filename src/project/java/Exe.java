/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
import java.nio.file.Path;

import kiss.I;

/**
 * @version 2012/05/24 15:39:08
 */
public class Exe extends bee.task.Exe {

    /**
     * {@inheritDoc}
     */
    @Override
    public Path build() {
        icon = I.locate("icon.ico");

        return super.build();
    }
}


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

import bee.util.ZipArchiver;
import filer.Filer;

/**
 * @version 2012/05/22 14:47:48
 */
public class Install extends bee.task.Install {

    /**
     * {@inheritDoc}
     */
    @Override
    public void project() {
        Path zip = require(Exe.class).build();

        ZipArchiver.unpack(zip, Filer.locate("F:\\Application/Toybox"));
    }
}

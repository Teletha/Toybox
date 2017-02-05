/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toy.box;

import java.nio.file.Path;

import kiss.I;

/**
 * @version 2017/02/06 1:25:56
 */
public interface UserConfig {

    /**
     * <p>
     * Path for window icon.
     * </p>
     * 
     * @return
     */
    default Path icon() {
        return I.locate("icon.ico");
    }
}

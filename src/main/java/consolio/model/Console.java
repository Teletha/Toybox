/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package consolio.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import consolio.filesystem.FSPath;

/**
 * @version 2017/02/06 1:52:04
 */
public class Console {

    /** The current context directory. */
    public Property<FSPath> context = new SimpleObjectProperty();
}

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

import kiss.I;
import kiss.Manageable;
import kiss.Singleton;
import toybox.filesystem.FSPath;

/**
 * @version 2017/02/05 23:00:29
 */
@Manageable(lifestyle = Singleton.class)
public class Toybox {

    /** The application context directory. */
    public FSPath contextDirectory;

    /**
     * <p>
     * Entry point.
     * </p>
     * 
     * @param args
     */
    public static void main(String[] args) {
        I.load(Toybox.class, true);

        Window window = I.make(Window.class);
        window.open();
    }
}

/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox.console;

import kiss.Manageable;
import kiss.Preference;
import toybox.Toybox;
import bebop.model.Selectable;

/**
 * @version 2012/03/01 0:20:16
 */
@Manageable(lifestyle = Preference.class)
public class Consoles extends Selectable<Console> {

    /**
     * 
     */
    protected Consoles(Toybox toybox) {
        Console console = new Console();
        console.setContext(toybox.contextDirectory);

        add(console);
    }
}

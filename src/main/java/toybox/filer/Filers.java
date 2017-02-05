/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox.filer;

import kiss.Manageable;
import kiss.Preference;
import toybox.Toybox;
import bebop.model.Selectable;

/**
 * @version 2012/03/06 22:24:53
 */
@Manageable(lifestyle = Preference.class)
public class Filers extends Selectable<Filer> {

    /**
     * 
     */
    private Filers(Toybox toybox) {
        Filer filer = new Filer();
        filer.setContext(toybox.contextDirectory);

        add(filer);
    }
}

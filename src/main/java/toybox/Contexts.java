/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox;

import kiss.Manageable;
import kiss.Singleton;
import kiss.Storable;
import viewtify.model.Selectable;

/**
 * @version 2018/09/26 21:15:43
 */
@Manageable(lifestyle = Singleton.class)
public class Contexts extends Selectable<Context> implements Storable<Contexts> {

    /**
     * Hide constructor.
     */
    private Contexts() {
        restore().auto();
    }
}

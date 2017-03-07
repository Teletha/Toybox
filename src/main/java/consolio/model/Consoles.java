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

import bebop.model.Selectable;
import kiss.Configurable;
import kiss.Manageable;
import kiss.Singleton;

/**
 * @version 2017/02/09 4:43:55
 */
@Manageable(lifestyle = Singleton.class)
public class Consoles extends Selectable<Console> implements Configurable<Consoles> {
}

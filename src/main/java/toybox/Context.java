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

import kiss.Variable;
import psychopath.Directory;

/**
 * @version 2018/09/26 21:31:38
 */
public class Context {

    public final Variable<Directory> directory = Variable.<Directory> empty().adjust(Directory::absolutize);

    public String name() {
        return directory.v.name();
    }
}

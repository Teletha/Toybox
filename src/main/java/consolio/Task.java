/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package consolio;

import consolio.ConsoleView.ConsoleText;
import consolio.model.Console;

/**
 * @version 2017/02/13 8:14:42
 */
public abstract class Task {

    /** The associated console. */
    protected Console console;

    /** The associated console interface. */
    protected ConsoleText ui;

    /**
     * {@inheritDoc}
     */
    public final void execute(ConsoleText context) {
        execute();
    }

    /**
     * <p>
     * Execute console command.
     * </p>
     */
    protected abstract void execute();
}

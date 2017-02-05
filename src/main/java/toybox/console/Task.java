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

import toybox.cli.Command;

/**
 * @version 2012/03/03 0:07:06
 */
public abstract class Task implements Command<ConsoleUI> {

    /** The associated console. */
    protected Console console;

    /** The associated console interface. */
    protected ConsoleUI ui;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void execute(ConsoleUI context) {
        execute();
    }

    /**
     * <p>
     * Execute console command.
     * </p>
     */
    protected abstract void execute();
}

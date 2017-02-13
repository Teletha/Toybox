/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.task;

import java.io.Writer;

/**
 * @version 2013/01/06 19:20:31
 */
public interface NativeProcessListener {

    /**
     * <p>
     * Start command execution.
     * </p>
     */
    void start(Writer writer);

    /**
     * <p>
     * Listen message.
     * </p>
     * 
     * @param message
     */
    void message(String message, String eol);

    /**
     * <p>
     * Finish command execution.
     * </p>
     */
    void finish();
}
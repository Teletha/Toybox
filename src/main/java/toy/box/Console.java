/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toy.box;

import toybox.filesystem.FSPath;

/**
 * @version 2017/02/06 1:52:04
 */
public class Console {

    /** The current context directory. */
    private FSPath context;

    /**
     * - Get the context property of this {@link Console}.
     * 
     * @return The context property.
     */
    public FSPath getContext() {
        return context;
    }

    /**
     * Set the context property of this {@link Console}.
     * 
     * @param context The context value to set.
     */
    public void setContext(FSPath context) {
        if (context != null) {
            this.context = context;
        }
    }
}

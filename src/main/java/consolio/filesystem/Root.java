/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package consolio.filesystem;

/**
 * @version 2017/02/09 4:42:20
 */
class Root extends FSPathByFile {

    /**
     * 
     */
    Root() {
        super(root);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FSPath getParent() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "/";
    }
}

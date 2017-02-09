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
 * @version 2017/02/09 4:42:14
 */
public interface FSScanner {

    void visitFile(FSPath path);

    void visitDirectory(FSPath path);
}
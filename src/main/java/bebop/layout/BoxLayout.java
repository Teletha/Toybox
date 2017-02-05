/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.layout;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

/**
 * @version 2012/03/15 12:16:19
 */
public class BoxLayout extends Layout {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void layout(Composite composite, boolean flushCache) {
    }

}

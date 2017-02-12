/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package consolio.bebop.ui;

import java.util.Objects;

import org.eclipse.swt.widgets.Widget;

/**
 * @version 2017/02/12 12:21:16
 */
public abstract class Materializable<M> {

    final Widget widget;

    /**
     * @param widget
     */
    protected Materializable(Widget widget) {
        this.widget = Objects.requireNonNull(widget);
    }

    public abstract Widget create(M model, int index);
}

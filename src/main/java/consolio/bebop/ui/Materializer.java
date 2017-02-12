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

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

import consolio.bebop.ui.UIBuilder.UINode;

/**
 * @version 2017/02/12 12:21:16
 */
public class Materializer<M> {

    /** The actual SWT widget. */
    final Widget widget;

    /**
     * <p>
     * Create {@link Materializer}
     * </p>
     * 
     * @param widget
     */
    public Materializer(Widget widget) {
        this.widget = Objects.requireNonNull(widget);
    }

    /**
     * @param children
     * @param object
     */
    void materializeChildren(List<UINode> children, BiConsumer<Composite, UINode> process) {
        if (widget instanceof Composite) {
            for (UINode child : children) {
                process.accept((Composite) widget, child);
            }
        }
    }
}

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
import java.util.function.Function;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

/**
 * @version 2017/02/10 10:57:59
 */
public abstract class AbstractUI<W extends Widget> {

    /** The parent composite. */
    private Composite parent;

    /** The widget builder. */
    private Function<AbstractUI<W>, Function<Composite, W>> builder;

    /** The actual SWT widget. */
    private W widget;

    /**
     * @param parent
     * @param builder
     */
    protected <C extends Composite> AbstractUI(C parent, Function<AbstractUI<W>, Function<C, W>> builder) {
        this.parent = Objects.requireNonNull(parent);
        this.builder = (Function<AbstractUI<W>, Function>) Objects.requireNonNull(builder);
    }

    /**
     * <p>
     * Retrieve the actual SWT widget lazily.
     * </p>
     * 
     * @return An actual {@link Widget}.
     */
    protected W widget() {
        if (widget == null) {
            widget = builder.apply(this).apply(parent);
        }
        return widget;
    }
}

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
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import kiss.Table;

/**
 * @version 2017/02/11 23:12:07
 */
public abstract class AbstractUI<M> {

    final Table<User, BiConsumer<User, Event>> listeners = new Table();

    /**
     * @param parent
     * @param builder
     */
    protected AbstractUI() {
    }

    /**
     * <p>
     * Build actual SWT widget for the specified model.
     * </p>
     * 
     * @param parent A parent {@link Widget}.
     * @param model A current model.
     * @return A created {@link Widget}.
     */
    protected abstract Materializable<M> materialize(Composite parent, M model);

    /**
     * Build actual ui.
     * 
     * @param parent
     * @param model
     * @return
     */
    protected final Materializable<M> build(Composite parent, M model) {
        Materializable<M> materialized = materialize(parent, model);

        for (Entry<User, List<BiConsumer<User, Event>>> entry : listeners.entrySet()) {
            User user = entry.getKey();

            materialized.widget.addListener(user.type, e -> {
                e.data = model;

                for (BiConsumer<User, Event> listener : entry.getValue()) {
                    listener.accept(user, e);
                }
            });
        }
        return materialized;
    }
}

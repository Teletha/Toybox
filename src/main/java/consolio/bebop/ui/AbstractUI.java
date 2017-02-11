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

import kiss.I;
import kiss.Table;

/**
 * @version 2017/02/10 10:57:59
 */
public abstract class AbstractUI<M> implements Cloneable {

    final Table<User, BiConsumer<User, Event>> listeners = new Table();

    /**
     * @param parent
     * @param builder
     */
    protected AbstractUI() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractUI clone() {
        try {
            return (AbstractUI) super.clone();
        } catch (CloneNotSupportedException e) {
            throw I.quiet(e);
        }
    }

    protected abstract Widget materialize(Composite parent, M model);

    /**
     * Build actual ui.
     * 
     * @param parent
     * @param model
     * @return
     */
    protected final Widget build(Composite parent, M model) {
        Widget materialized = materialize(parent, model);

        for (Entry<User, List<BiConsumer<User, Event>>> entry : listeners.entrySet()) {
            User user = entry.getKey();

            materialized.addListener(user.type, e -> {
                e.data = model;

                for (BiConsumer<User, Event> listener : entry.getValue()) {
                    listener.accept(user, e);
                }
            });
        }
        return materialized;
    }

    /**
     * <p>
     * Helper method to check the parent {@link Widget} type.
     * </p>
     * 
     * @param type A type to require.
     * @param parent A parent composite to check.
     * @return
     */
    protected final <W extends Composite> W requireParent(Class<W> type, Composite parent) {
        if (type.isAssignableFrom(parent.getClass())) {
            return (W) parent;
        }
        throw new IllegalArgumentException(getClass().getName() + " requires " + type.getSimpleName() + " as parent widget.");
    }
}

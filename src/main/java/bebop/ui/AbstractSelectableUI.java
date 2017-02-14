/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import bebop.model.Selectable;
import kiss.Events;

/**
 * @version 2017/02/11 15:10:46
 */
public abstract class AbstractSelectableUI<M extends Selectable<Child>, Child> extends AbstractUI<M> {

    /**
     * <p>
     * Select the child model of {@link Selectable} model which is located by {@link Event}.
     * </p>
     * 
     * @param e A location as {@link Event}.
     * @return A located child model.
     */
    public abstract Events<Child> selectBy(Event e);

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Materializer createMaterializer(Composite parent, M model) {
        ItemMaterializer<M, Child> materializer = materialize2(parent, model);

        model.add.to(item -> materializer.createItem(item, materializer.size()));

        return materializer;
    }

    protected abstract ItemMaterializer materialize2(Composite parent, M model);

    /**
     * @version 2017/02/12 9:25:53
     */
    protected abstract class ItemMaterializer<M, Child> extends Materializer<M> {

        protected final M model;

        protected ItemMaterializer(Widget widget, M model) {
            super(widget);

            this.model = model;
        }

        protected abstract Widget[] items();

        protected abstract int size();

        protected abstract Widget item(int index);

        protected abstract Widget createItem(Child model, int index);
    }
}

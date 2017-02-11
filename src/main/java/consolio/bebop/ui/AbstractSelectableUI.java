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

import org.eclipse.swt.widgets.Event;

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
}

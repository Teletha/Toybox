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

import java.util.function.Predicate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

/**
 * @version 2017/02/11 18:36:24
 */
public enum User {

    Activate(SWT.Activate),

    Deactivate(SWT.Deactivate),

    Close(SWT.Close),

    Iconify(SWT.Iconify),

    Deiconify(SWT.Deiconify),

    KeyDown(SWT.KeyDown),

    KeyUp(SWT.KeyUp),

    MouseDown(SWT.MouseDown),

    MouseUp(SWT.MouseUp),

    MouseLeftDown(SWT.MouseDown, e -> e.button == 1),

    MouseLeftUp(SWT.MouseUp, e -> e.button == 1),

    MouseMiddleDown(SWT.MouseDown, e -> e.button == 2),

    MouseMiddleUp(SWT.MouseUp, e -> e.button == 2),

    MouseRightDown(SWT.MouseDown, e -> e.button == 3),

    MouseRightUp(SWT.MouseUp, e -> e.button == 3),

    Move(SWT.Move),

    Resize(SWT.Resize),

    Select(SWT.Selection),

    SelectDefault(SWT.DefaultSelection);

    /** The action type identifier. */
    final int type;

    /** The conditional event. */
    final Predicate<Event> condition;

    /**
     * Hide constructor.
     * 
     * @param type An identifier for action.
     */
    private User(int type) {
        this(type, e -> true);
    }

    /**
     * Hide constructor.
     * 
     * @param type An identifier for action.
     */
    private User(int type, Predicate<Event> condition) {
        this.type = type;
        this.condition = condition;
    }
}

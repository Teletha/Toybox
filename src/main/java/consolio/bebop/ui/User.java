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

import org.eclipse.swt.SWT;

/**
 * @version 2017/02/08 21:28:09
 */
public enum User {

    Activate(SWT.Activate),

    Deactivate(SWT.Deactivate),

    Close(SWT.Close),

    Iconify(SWT.Iconify),

    Deiconify(SWT.Deiconify),

    KeyDown(SWT.KeyDown),

    KeyUp(SWT.KeyUp),

    Move(SWT.Move),

    Resize(SWT.Resize);

    /** The action type identifier. */
    public final int type;

    /**
     * Hide constructor.
     * 
     * @param type An identifier for action.
     */
    private User(int type) {
        this.type = type;
    }
}

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
import org.eclipse.swt.custom.CTabItem;

/**
 * @version 2017/02/10 11:48:26
 */
public class Tab<T> extends AbstractUI<CTabItem> {

    /**
     * @param parent
     * @param builder
     */
    public Tab(TabFolder<T> parent) {
        super(parent.widget(), p -> {
            return new CTabItem(p, SWT.None);
        });
    }

    public Tab<T> text(String text) {
        widget().setText(text);
        return this;
    }
}

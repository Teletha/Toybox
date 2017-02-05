/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toy.box.ui;

import javafx.beans.property.ListProperty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;

import kiss.I;

/**
 * @version 2017/02/06 2:32:18
 */
public class TabPanel<M> extends BaseUI<CTabFolder> {

    /** The items. */
    public final ListProperty<M> items = I.make(ListProperty.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public CTabFolder createUI(Composite parent) {
        CTabFolder folder = new CTabFolder(parent, SWT.None);
        folder.setSimple(false);
        folder.setMinimumCharacters(10);
        folder.setTabHeight(22);

        return folder;
    }
}

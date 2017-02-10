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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;

/**
 * @version 2017/02/10 10:51:40
 */
public class TabFolder<T> extends AbstractUI<CTabFolder> {

    /** The tab manager. */
    private List<Tab> tabs = new CopyOnWriteArrayList();

    /**
     * @param parent
     */
    public TabFolder(Composite parent, Selectable<T> selectable, Function<TabFolder<T>, Function<T, Tab>> itemBuilder) {
        super(parent, self -> p -> {
            CTabFolder folder = new CTabFolder(p, SWT.None);

            for (T item : selectable) {
                Tab tab = itemBuilder.apply(self).apply(item);
                tab.widget();
            }
            return folder;
        });
    }

    /**
     * Sets the minimum number of characters that will be displayed in a fully compressed tab.
     *
     * @param count the minimum number of characters that will be displayed in a fully compressed
     *            tab
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
     *                the receiver</li>
     *                <li>ERROR_INVALID_RANGE - if the count is less than zero</li>
     *                </ul>
     */
    public TabFolder<T> minimumCharacters(int count) {
        widget().setMinimumCharacters(count);
        return this;
    }

    /**
     * Specify a fixed height for the tab items. If no height is specified, the default height is
     * the height of the text or the image, whichever is greater. Specifying a height of -1 will
     * revert to the default height.
     *
     * @param height the pixel value of the height or -1
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
     *                the receiver</li>
     *                <li>ERROR_INVALID_ARGUMENT - if called with a height of less than 0</li>
     *                </ul>
     */
    public TabFolder<T> tabHeight(int height) {
        widget().setTabHeight(height);
        return this;
    }
}

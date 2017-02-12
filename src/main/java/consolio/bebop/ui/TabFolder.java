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

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import kiss.Events;

/**
 * @version 2017/02/10 10:51:40
 */
public class TabFolder<M extends Selectable<Child>, Child> extends AbstractSelectableUI<M, Child> {

    /**
     * Hide constructor.
     */
    TabFolder() {
    }

    private int minimumCharacters;

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
    public TabFolder<M, Child> minimumCharacters(int count) {
        this.minimumCharacters = count;
        return this;
    }

    private int tabHeight = -1;

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
    public TabFolder<M, Child> tabHeight(int height) {
        this.tabHeight = height;
        return this;
    }

    private Function<Child, String> tabText = Child::toString;

    /**
     * <p>
     * Set tab text.
     * </p>
     * 
     * @param text
     * @return
     */
    public TabFolder<M, Child> tabText(String text) {
        return tabText(child -> text);
    }

    /**
     * <p>
     * Set tab text.
     * </p>
     * 
     * @param text
     * @return
     */
    public TabFolder<M, Child> tabText(Function<Child, String> text) {
        this.tabText = Objects.requireNonNull(text);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ItemMaterializer materialize(Composite parent, M model, Object context) {
        CTabFolder folder = new CTabFolder(parent, SWT.None);
        folder.setMinimumCharacters(minimumCharacters);
        folder.setTabHeight(tabHeight);

        return new ItemMaterializer<M, Child>(folder, model) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected Widget[] items() {
                return folder.getItems();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected int size() {
                return folder.getItemCount();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected Widget item(int index) {
                return folder.getItem(index);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected Widget createItem(Child model, int index) {
                CTabItem tab = new CTabItem(folder, SWT.None, index == -1 ? size() : index);
                tab.setText(tabText.apply(model));
                tab.setData(UI.KeyModel, model);

                this.model.remove.take(model::equals).take(1).to(tab::dispose);

                return tab;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Events<Child> selectBy(Event e) {
        return Events.from(e.widget)
                .as(CTabFolder.class)
                .map(folder -> folder.getItem(new Point(e.x, e.y)))
                .map(item -> (Child) item.getData(UI.KeyModel));
    }
}

/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toy.box;

import static bebop.util.SWTUtil.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import bebop.Listen;
import bebop.Speak;
import bebop.input.Key;
import bebop.input.KeyBind;
import bebop.model.Selectable;
import bebop.ui.AbstractSelectableUI;
import bebop.ui.AbstractUI;
import bebop.ui.UIEvent;
import bebop.util.Resources;
import kiss.I;
import kiss.Manageable;
import kiss.Preference;
import toybox.console.Console;
import toybox.filesystem.FSPath;

/**
 * @version 2017/02/05 23:25:29
 */
@Manageable(lifestyle = Preference.class)
public class Window {

    /** The root display. */
    private final Display display = Display.getDefault();

    /** The screen size. */
    private Rectangle screen = display.getPrimaryMonitor().getClientArea();

    /** The termination flag. */
    private volatile boolean terminate = false;

    /** The actual window. */
    private Shell shell;

    /** The window is maximized or not. */
    public boolean maximized = false;

    /** The window size and position. */
    public Rectangle bounds = new Rectangle(screen.width / 4, screen.height / 4, screen.width / 2, screen.height / 2);

    public List<Console> consoles = new ArrayList();

    /**
     * Open window.
     */
    public final void open() {
        shell = new Shell();
        shell.setLayout(new FillLayout(SWT.VERTICAL));

        // Restore window location and size.
        shell.setBounds(bounds);
        shell.addControlListener(new WindowLocationChaser());
        shell.addShellListener(new Deactivator());
        shell.setImage(Resources.getImage(I.locate("icon.ico")));

        // TAB
        CTabFolder folder = new CTabFolder(shell, SWT.None);
        folder.setSimple(false);
        folder.setMinimumCharacters(10);
        folder.setTabHeight(22);

        for (Console console : consoles) {
            CTabItem item = new CTabItem(folder, SWT.None);
            item.setText("  " + console.getContext().getName() + "    ");
        }

        Key.bind(this, folder);

        shell.layout();
        shell.open();

        while (!terminate) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        // Clean up resources.
        display.dispose();
    }

    public void active() {
        shell.forceActive();
    }

    @KeyBind(key = Key.T, ctrl = true)
    public void createNewTab() {
        Console console = new Console();
        console.setContext(FSPath.Root);
        consoles.add(console);
    }

    /**
     * @version 2017/02/05 23:30:26
     */
    private class WindowLocationChaser implements ControlListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void controlMoved(ControlEvent e) {
            bounds = ((Shell) e.widget).getBounds();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void controlResized(ControlEvent e) {
            bounds = ((Shell) e.widget).getBounds();
        }
    }

    /**
     * @version 2017/02/05 23:18:52
     */
    private class Deactivator implements ShellListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void shellActivated(ShellEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void shellClosed(ShellEvent event) {
            terminate = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void shellDeactivated(ShellEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void shellDeiconified(ShellEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void shellIconified(ShellEvent event) {
        }
    }

    /**
     * @version 2017/02/06 1:47:41
     */
    public static abstract class TabUI<Model extends Selectable<SubModel>, SubModel>
            extends AbstractSelectableUI<Model, SubModel, CTabFolder> {

        /**
         * <p>
         * Create user interface.
         * </p>
         * 
         * @param parent
         * @return
         */
        @Override
        protected CTabFolder createUI(Composite parent) {
            CTabFolder folder = new CTabFolder(parent, SWT.None);
            folder.setSimple(false);
            folder.setMinimumCharacters(10);
            folder.setTabHeight(22);

            for (SubModel subModel : model) {
                CTabItem item = new CTabItem(folder, SWT.None);
                labelTab(item, subModel);

                set(item, subModel);
            }

            return folder;
        }

        /**
         * <p>
         * Label tab title.
         * </p>
         * 
         * @param tab
         * @param subModel
         */
        protected abstract void labelTab(CTabItem tab, SubModel subModel);

        /**
         * <p>
         * Select the next tab. If the curret tab is last, it will select the first tab.
         * </p>
         */
        @KeyBind(key = Key.Tab, ctrl = true)
        protected void selectNextTab() {
            int index = model.getSelectionIndex() + 1;

            if (index == model.size()) {
                index = 0;
            }
            model.setSelectionIndex(index);
        }

        /**
         * <p>
         * Select the previous tab. If the curret tab is first, it will select the last tab.
         * </p>
         */
        @KeyBind(key = Key.Tab, ctrl = true, shift = true)
        protected void selectPreviousTab() {
            int index = model.getSelectionIndex() - 1;

            if (index == -1) {
                index = model.size() - 1;
            }
            model.setSelectionIndex(index);
        }

        /**
         * <p>
         * Close the curretn selected tab.
         * </p>
         */
        @KeyBind(key = Key.W, ctrl = true)
        protected void closeCurrentTab() {
            model.remove(model.getSelection());
        }

        /**
         * <p>
         * Helper method to indicate tab widget associated with the specified sub model.
         * </p>
         * 
         * @param model A sub model.
         * @return An associated tab.
         */
        protected final CTabItem getTabBy(SubModel model) {
            return this.ui.getItem(this.model.indexOf(model));
        }

        /**
         * <p>
         * Helper method to indicate sub mode associated with the specified tab.
         * </p>
         * 
         * @param model A sub model.
         * @return An associated tab.
         */
        protected final SubModel getModelBy(CTabItem tab) {
            return this.model.get(this.ui.indexOf(tab));
        }

        /**
         * Notify tab selection event to model.
         */
        @Listen(UIEvent.Selection)
        protected void selection(Event e) {
            model.setSelectionIndex(ui.indexOf((CTabItem) e.item));
        }

        /**
         * Notify tab closing event to model.
         */
        @Listen(UIEvent.MouseUp)
        protected void removeTabByMiddleClick(Event e) {
            if (e.button == 2) {
                // mouse middle click
                CTabItem item = ui.getItem(new Point(e.x, e.y));

                if (item != null) {
                    model.remove(model.get(ui.indexOf(item)));
                }
            }
        }

        /**
         * Notify tab closing event to model.
         */
        @Listen(UIEvent.Close)
        protected void close(CTabFolderEvent e) {
            model.remove(model.get(ui.indexOf((CTabItem) e.item)));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Speak
        public void select(SubModel item) {
            int index = model.indexOf(item);

            if (index != -1) {
                CTabItem tab = ui.getItem(index);

                if (tab.getControl() == null) {
                    // lazy initalization
                    AbstractUI child = createChildUI(item);

                    tab.setControl(child.ui);
                }

                // change tab selection
                ui.setSelection(tab);

                // focus actual control
                tab.getControl().forceFocus();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Speak
        public void deselect(SubModel item) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Speak
        public void add(SubModel model) {
            CTabItem tab = new CTabItem(ui, SWT.None);
            labelTab(tab, model);

            set(tab, model);

            // select new one
            this.model.setSelection(model);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Speak
        public void remove(SubModel item, int index) {
            CTabItem tab = ui.getItem(index);

            if (tab != null) {
                tab.dispose();
            }
        }

    }
}

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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import bebop.input.Key;
import bebop.input.KeyBind;
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
}

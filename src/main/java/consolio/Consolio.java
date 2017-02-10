/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package consolio;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;

import bebop.input.Key;
import bebop.util.Resources;
import consolio.bebop.ui.Application;
import consolio.bebop.ui.TabFolder;
import consolio.bebop.ui.UI;
import consolio.filesystem.FSPath;
import consolio.model.Console;
import consolio.model.Consoles;
import kiss.Events;
import kiss.I;

/**
 * @version 2017/02/08 20:00:18
 */
public class Consolio extends Application {

    private final Consoles model = I.make(Consoles.class).restore();

    private final TabFolder<Console> folder = new TabFolder<>(shell, model, p -> item -> {
        return new Tab().text(" " + item.getContext().getName() + " ");
    }).minimumCharacters(10).tabHeight(22);

    public final Events<Event> addConsole = UI.whenPress(Key.T).at(folder);

    /**
     * 
     */
    private Consolio() {
        addConsole.to(e -> {
            Console console = new Console();
            console.setContext(FSPath.locate(I.locate("")));
            model.add(console);
            model.store();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        shell.setLayout(new FillLayout(SWT.VERTICAL));
        shell.setImage(Resources.getImage(I.locate("icon.ico")));

        // whenPress(Key.T).at(folder).to(e -> {
        // Console console = new Console();
        // console.setContext(FSPath.locate(I.locate("").toAbsolutePath()));
        // model.consoles.add(console);
        // model.store();
        // });
        //

        virtualize().materialize(shell);
    }

    public UIBuilder virtualize() {
        return new UIBuilder() {
            {
                $("tabs", foŕ(model, console -> {
                    $("tab");
                }));
            }
        };
    }

    /**
     * <p>
     * Application activator.
     * </p>
     * 
     * @param args
     */
    public static void main(String[] args) {
        launch(Consolio.class, ActivationPolicy.Latest);
    }
}

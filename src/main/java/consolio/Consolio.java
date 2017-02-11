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

import static consolio.bebop.ui.UI.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;

import bebop.input.Key;
import bebop.util.Resources;
import consolio.bebop.ui.Application;
import consolio.bebop.ui.TabFolder;
import consolio.bebop.ui.UIBuilder;
import consolio.bebop.ui.User;
import consolio.model.Console;
import consolio.model.Consoles;
import kiss.I;

/**
 * @version 2017/02/08 20:00:18
 */
public class Consolio extends Application {

    /** The root model. */
    private final Consoles consoles = I.make(Consoles.class).restore();

    private final TabFolder<Consoles, Console> folder = new TabFolder<>(Consoles.class).minimumCharacters(10)
            .tabHeight(22)
            .tabText(item -> " " + item.getContext().getName() + " ");

    /**
     * 
     */
    private Consolio() {
        // addConsole.to(e -> {
        // Console console = new Console();
        // console.setContext(FSPath.locate(I.locate("")));
        // consoles.add(console);
        // consoles.store();
        // });

        whenPress(Key.T).at(folder).to(e -> {
            System.out.println("Add " + e);
        });

        when(User.MouseMiddleUp).at(folder).flatMap(folder::selectBy).to(e -> {
            consoles.remove(e);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        shell.setLayout(new FillLayout(SWT.VERTICAL));
        shell.setImage(Resources.getImage(I.locate("icon.ico")));

        virtualize().materialize(shell);
    }

    public UIBuilder virtualize() {
        return new UIBuilder() {
            {
                $(folder, consoles, console -> {
                    System.out.println(console);
                });
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

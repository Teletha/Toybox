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

import bebop.util.Resources;
import consolio.bebop.ui.Application;
import consolio.bebop.ui.Key;
import consolio.bebop.ui.Key.With;
import consolio.bebop.ui.TabFolder;
import consolio.bebop.ui.UI;
import consolio.bebop.ui.UIBuilder;
import consolio.bebop.ui.User;
import consolio.filesystem.FSPath;
import consolio.model.Console;
import consolio.model.Consoles;
import kiss.I;

/**
 * @version 2017/02/08 20:00:18
 */
public class Consolio extends Application {

    /** The root model. */
    private final Consoles consoles = I.make(Consoles.class).restore();

    private final TabFolder<Consoles, Console> folder = UI.tab(Consoles.class)
            .minimumCharacters(10)
            .tabHeight(22)
            .tabText(item -> " " + item.getContext().getName() + " ");

    private final ConsoleView view = new ConsoleView();

    /**
     * 
     */
    private Consolio() {
        whenUserPress(Key.T, With.Ctrl).at(folder).to(e -> {
            Console console = new Console();
            console.setContext(FSPath.locate(I.locate("")));
            consoles.add(console);
        });

        when(User.MouseMiddleUp).at(folder).flatMap(folder::selectBy).to(e -> {
            consoles.remove(e);
        });

        consoles.add.merge(consoles.remove).to(this::updateView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        shell.setLayout(new FillLayout(SWT.VERTICAL));
        shell.setImage(Resources.getImage(I.locate("icon.ico")));

        updateView();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIBuilder virtualize() {
        return new UIBuilder() {
            {
                $(folder, consoles, console -> {
                    $(view);
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

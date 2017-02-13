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

import static bebop.ui.UI.*;

import bebop.ui.Application;
import bebop.ui.Key;
import bebop.ui.UI;
import bebop.ui.UIBuilder;
import bebop.ui.UITab;
import bebop.ui.User;
import bebop.ui.Key.With;
import consolio.filesystem.FSPath;
import consolio.model.Console;
import consolio.model.Consoles;
import kiss.I;

/**
 * @version 2017/02/13 13:33:33
 */
public class Consolio extends Application {

    /** The root model. */
    private final Consoles consoles = I.make(Consoles.class).restore();

    /** The tab container. */
    private final UITab<Consoles, Console> tabs = UI.tab(Consoles.class)
            .tabMinimumCharacters(10)
            .tabHeight(22)
            .tabText(item -> " " + item.getContext().getName() + " ");

    /** The editable console. */
    private final UIConsole consoleUI = new UIConsole().lineLimit(2000);

    /**
     * 
     */
    private Consolio() {
        whenUserPress(Key.T, With.Ctrl).at(tabs).to(e -> {
            Console console = new Console();
            console.setContext(FSPath.locate(I.locate("")));
            consoles.add(console);
        });

        when(User.MouseMiddleUp).at(tabs).flatMap(tabs::selectBy).to(e -> {
            consoles.remove(e);
        });

        consoles.add.merge(consoles.remove).to(this::updateView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIBuilder virtualize() {
        return new UIBuilder() {
            {
                $(tabs, consoles, console -> {
                    $(consoleUI);
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

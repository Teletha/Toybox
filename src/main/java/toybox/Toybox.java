/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox;

import kiss.I;
import psychopath.Locator;
import viewtify.Key;
import viewtify.Theme;
import viewtify.Viewtify;
import viewtify.ui.UI;
import viewtify.ui.UITabPane;
import viewtify.ui.View;
import viewtify.ui.helper.User;

/**
 * @version 2018/09/26 19:16:01
 */
public class Toybox extends View {

    private UITabPane tabs;

    private Contexts contexts = I.make(Contexts.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected UI declareUI() {
        return new UI() {
            {
                $(tabs);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        tabs.model(contexts, Context::name, Console::new);

        tabs.when(User.input(Key.T.ctrl()), () -> {
            Context created = new Context();
            created.directory.set(Locator.directory(""));

            contexts.add(created);
        });
    }

    /**
     * Entry point.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Viewtify.application().use(Theme.Dark).icon("icon.png").activate(Toybox.class);
    }
}

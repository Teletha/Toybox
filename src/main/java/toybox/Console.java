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

import viewtify.Key;
import viewtify.ui.UI;
import viewtify.ui.UITab;
import viewtify.ui.View;
import viewtify.ui.editor.UIEditor;
import viewtify.ui.helper.User;

/**
 * @version 2018/09/26 21:27:07
 */
public class Console extends View {

    private final Context context;

    private UIEditor editor;

    /**
     * @param tab
     * @param context
     */
    Console(UITab tab, Context context) {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected UI declareUI() {
        return new UI() {
            {
                $(editor);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        editor.when(User.input(Key.A), () -> {
            System.out.println(editor.cusor());
        });
    }
}

/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox.console;

import kiss.I;

import org.eclipse.swt.custom.CTabItem;

import toybox.Toybox;
import toybox.filer.FilerWindow;
import bebop.Bind;
import bebop.input.Key;
import bebop.input.KeyBind;
import bebop.ui.TabUI;

/**
 * @version 2012/03/03 8:38:11
 */
public class ConsolesUI extends TabUI<Consoles, Console> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(Console item) {
        super.select(item);

        ui.getShell().setText(item.getContext().toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Bind
    protected void labelTab(CTabItem tab, Console subModel) {
        tab.setText("  " + subModel.getContext().getName() + "    ");

        if (model.getSelection() == subModel) {
            tab.getParent().getShell().setText(subModel.getContext().toString());
        }
    }

    /**
     * 
     */
    @KeyBind(key = Key.T, ctrl = true)
    public void createNewConsole() {
        Console console = new Console();

        if (model.getSelectionIndex() == -1) {
            console.setContext(I.make(Toybox.class).contextDirectory);
        } else {
            console.setContext(model.getSelection().getContext());
        }
        model.add(console);
    }

    /**
     * 
     */
    @KeyBind(key = Key.F12)
    public void createFiler() {
        I.make(Toybox.class).open(FilerWindow.class);
    }
}

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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;

import bebop.input.Key;
import bebop.util.Resources;
import consolio.bebop.ui.Application;
import consolio.bebop.ui.UI;
import consolio.model.Console;
import consolio.model.Model;
import kiss.I;

/**
 * @version 2017/02/08 20:00:18
 */
public class Consolio extends Application {

    private final Model model = I.make(Model.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        shell.setLayout(new FillLayout(SWT.VERTICAL));
        shell.setImage(Resources.getImage(I.locate("icon.ico")));

        // TAB
        CTabFolder folder = new CTabFolder(shell, SWT.None);
        folder.setMinimumCharacters(10);
        folder.setTabHeight(22);

        UI.when(Key.T).at(folder).to(e -> {
            System.out.println(e);
        });

        for (Console console : model.consoles) {
            CTabItem item = new CTabItem(folder, SWT.None);
            item.setText("  " + console.getContext().getName() + "    ");
        }
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

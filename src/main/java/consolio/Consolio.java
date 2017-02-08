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

import consolio.ui.Application;

/**
 * @version 2017/02/08 20:00:18
 */
public class Consolio extends Application {

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        shell.setLayout(new FillLayout(SWT.VERTICAL));
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

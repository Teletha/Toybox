/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toy.box.ui;

import org.eclipse.swt.widgets.Composite;

/**
 * @version 2017/02/06 2:33:55
 */
public abstract class BaseUI<W extends Composite> {

    public abstract W createUI(Composite parent);
}

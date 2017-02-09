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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import consolio.UIBuilder.WidgetNode;
import kiss.Tree;

/**
 * @version 2017/02/09 9:59:32
 */
public class UIBuilder extends Tree<WidgetNode> {

    protected UIBuilder() {
        super(WidgetNode::new, (parent, child) -> {
            child.accept(parent);
        }, null);
    }

    public void materialize(Composite composite) {
        Control[] children = composite.getChildren();

    }

    /**
     * @version 2017/02/09 10:00:16
     */
    public static class WidgetNode implements Consumer<WidgetNode> {

        /** The widget type. */
        private final String name;

        private List<WidgetNode> children = new ArrayList();

        private WidgetNode(String name, int id, Object context) {
            this.name = name;

            System.out.println(name + "  " + id + "   " + context);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(WidgetNode parent) {
            parent.children.add(this);
        }
    }
}

/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package consolio.bebop.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.swt.widgets.Composite;

import consolio.bebop.ui.UIBuilder.UINode;
import kiss.Tree;

/**
 * @version 2017/02/09 9:59:32
 */
public class UIBuilder extends Tree<AbstractUI, UINode> {

    protected UIBuilder() {
        super(UINode::new, (parent, child) -> {
            child.accept(parent);
        }, null);
    }

    protected final <Model extends Selectable<Child>, Child> void $(AbstractSelectableUI<Model, Child> ui, Selectable<Child> model, Consumer<Child> item) {
        $(ui, new ModelNode(model), foŕ(model, i -> {
            item.accept(i);
        }));
    }

    protected final <Model> void $(AbstractUI<Model> ui, Model model) {
        $(ui);
    }
    //
    // public void materialize(Composite composite) {
    // for (UINode node : root) {
    // materialize(composite, node);
    // }
    // }

    /**
     * <p>
     * Build actual ui.
     * </p>
     * 
     * @param parent
     * @param node
     */
    private void materialize(Composite parent, UINode node) {
        // Widget widget = node.ui.build(parent, node.model);
        //
        // if (widget instanceof Composite) {
        // Composite composite = (Composite) widget;
        //
        // for (UINode child : node.children) {
        // materialize(composite, child);
        // }
        // }
    }

    /**
     * @version 2017/02/09 10:00:16
     */
    static class UINode implements Consumer<UINode> {

        final AbstractUI ui;

        final int id;

        Object model;

        List<UINode> children = new ArrayList();

        /**
         * 
         */
        private UINode(AbstractUI ui, int id, Object model) {
            this.ui = ui;
            this.id = id;
            this.model = model;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(UINode parent) {
            parent.children.add(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "UINode[ui = " + ui + "  model = " + model + "]";
        }
    }

    /**
     * @version 2017/02/11 19:34:58
     */
    private static class ModelNode implements Consumer<UINode> {

        /** The model. */
        private Object model;

        /**
         * @param model
         */
        private ModelNode(Object model) {
            this.model = model;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(UINode parent) {
            parent.model = model;
        }
    }
}

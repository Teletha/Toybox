/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.ui;

import java.util.List;
import java.util.function.Consumer;

import org.eclipse.swt.widgets.Composite;

import bebop.model.Selectable;
import bebop.ui.UIBuilder.UINode;
import kiss.Tree;
import kiss.TreeNode;

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

    /**
     * @version 2017/02/15 21:00:37
     */
    static class UINode extends TreeNode<UINode, UINode, Composite> {

        final AbstractUI ui;

        Object model;

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
        protected void addTo(Composite parent, Object index) {
            ui.materialize(parent, model, nodes);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void removeFrom(Composite parent) {
            super.removeFrom(parent);
            System.out.println("remove");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void moveTo(Composite parent) {
            super.moveTo(parent);
            System.out.println("move");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void diff(List<Runnable> patches, UINode next) {
            super.diff(patches, next);
            System.err.println("diff");
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

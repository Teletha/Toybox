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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

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

    public void materialize(Composite composite) {
        for (UINode node : root) {
            materialize(composite, node);
        }
    }

    /**
     * <p>
     * Build actual ui.
     * </p>
     * 
     * @param parent
     * @param node
     */
    private void materialize(Composite parent, UINode node) {
        for (Control child : parent.getChildren()) {
            UINode exist = (UINode) child.getData("node");

            if (exist == node) {
                return;
            }
        }

        // new node
        Widget widget = node.ui.build(parent, node.model);

        if (widget instanceof Composite) {
            Composite composite = (Composite) widget;

            for (UINode child : node.children) {
                materialize(composite, child);
            }
        }
    }

    /**
     * @version 2017/02/09 10:00:16
     */
    static class UINode implements Consumer<UINode> {

        private AbstractUI ui;

        private int id;

        private Object model;

        private List<UINode> children = new ArrayList();

        /**
         * 
         */
        private UINode(AbstractUI ui, int id, Object model) {
            this.ui = ui;
            this.id = id;
            this.model = model;

            System.out.println(ui + "  " + id + "   " + model);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(UINode parent) {
            parent.children.add(this);
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

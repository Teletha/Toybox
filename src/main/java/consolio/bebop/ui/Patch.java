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

import org.eclipse.swt.widgets.Widget;

import consolio.bebop.ui.UIBuilder.UINode;

/**
 * @version 2017/02/12 2:20:39
 */
public abstract class Patch {

    /** The parent widget. */
    protected final Widget parent;

    /**
     * <p>
     * Create operation.
     * </p>
     * 
     * @param parent A parent (context) element.
     */
    protected Patch(Widget parent) {
        this.parent = parent;
    }

    /**
     * <p>
     * Apply this patch operation.
     * </p>
     */
    public abstract void apply();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    /**
     * @version 2017/02/12 2:20:55
     */
    private static abstract class ChildPatch extends Patch {

        /** The target child node. */
        protected final UINode child;

        /**
         * <p>
         * Create operation for child node manipulation.
         * </p>
         * 
         * @param parent A parent element.
         * @param child A target child node.
         */
        private ChildPatch(Widget parent, UINode child) {
            super(parent);

            this.child = child;
        }
    }

    /**
     * @version 2014/08/29 9:49:18
     */
    static class RemoveChild extends ChildPatch {

        /**
         * <p>
         * Create child remove operation.
         * </p>
         * 
         * @param parent
         * @param child
         */
        RemoveChild(Element parent, VirtualNode child) {
            super(parent, child);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply() {
            parent.removeChild(child.dom);
            child.dispose();
        }
    }

    /**
     * @version 2017/02/12 2:20:48
     */
    static class InsertChild extends ChildPatch {

        /** The index to insert. */
        private final Widget index;

        /**
         * <p>
         * Create child insert operation.
         * </p>
         * 
         * @param parent
         * @param index
         * @param child
         */
        InsertChild(Widget parent, Widget index, UINode child) {
            super(parent, child);
            this.index = index;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply() {
            Widget created = child.materialize();

            if (this.index == null) {
                parent.append(created);
            } else {
                parent.insertBefore(created, index);
            }
        }
    }

    /**
     * @version 2014/08/29 12:45:20
     */
    static class MoveChild extends ChildPatch {

        /** The child node to move. */
        private final Node child;

        /**
         * <p>
         * Create child move operation.
         * </p>
         * 
         * @param parent
         * @param child
         */
        MoveChild(Element parent, Node child) {
            super(parent, null);

            this.child = child;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply() {
            parent.append(child);
        }
    }

    /**
     * @version 2014/08/29 12:45:20
     */
    static class ReplaceChild extends ChildPatch {

        /** The new contents to replace. */
        private final VirtualNode replace;

        /**
         * <p>
         * Create child replace operation.
         * </p>
         * 
         * @param parent
         * @param child
         * @param replace
         */
        ReplaceChild(Element parent, VirtualNode child, VirtualNode replace) {
            super(parent, child);

            this.replace = replace;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply() {
            parent.replace(child.dom, replace.materialize());
            child.dispose();
        }
    }

    /**
     * @version 2014/09/14 12:51:53
     */
    static class ReplaceText extends ChildPatch {

        /** The new contents to replace. */
        private final VirtualText replace;

        /**
         * <p>
         * Create text replace operation.
         * </p>
         * 
         * @param child
         * @param replace
         */
        ReplaceText(VirtualNode child, VirtualText replace) {
            super(null, child);

            this.replace = replace;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply() {
            child.dom.text(replace.text);
        }
    }

    /**
     * @version 2014/09/08 18:19:19
     */
    private static abstract class AttributePatch extends Patch {

        /** The name. */
        protected final String name;

        /** The value. */
        protected final String value;

        /**
         * @param name
         * @param value
         */
        private AttributePatch(Element parent, String name, String value) {
            super(parent);

            this.name = name;
            this.value = value;
        }
    }

    /**
     * @version 2014/08/31 8:20:20
     */
    static class AddAttribute extends AttributePatch {

        /**
         * <p>
         * Create add attribute operation.
         * </p>
         * 
         * @param parent
         * @param name
         * @param value
         */
        AddAttribute(Element parent, String name, String value) {
            super(parent, name, value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply() {
            parent.attr(name, value);
        }
    }

    /**
     * @version 2014/08/31 8:20:20
     */
    static class ChangeAttribute extends AttributePatch {

        /**
         * <p>
         * Create change attribute value operation.
         * </p>
         * 
         * @param parent
         * @param name
         * @param value
         */
        ChangeAttribute(Element parent, String name, String value) {
            super(parent, name, value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply() {
            parent.attr(name, value);
        }
    }

    /**
     * @version 2014/09/12 12:11:25
     */
    static class RemoveAttribute extends AttributePatch {

        /**
         * <p>
         * Create remove attribute operation.
         * </p>
         * 
         * @param parent
         * @param name
         */
        RemoveAttribute(Element parent, String name) {
            super(parent, name, null);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply() {
            parent.remove(name);
        }
    }

    /**
     * @version 2014/09/08 18:19:19
     */
    private static abstract class ClassPatch extends Patch {

        /** The class name. */
        protected final Style style;

        /**
         * @param parent
         * @param style
         */
        private ClassPatch(Element parent, Style style) {
            super(parent);

            this.style = style;
        }
    }

    /**
     * @version 2015/09/28 21:31:57
     */
    static class AddClass extends ClassPatch {

        /**
         * <p>
         * Create remove attribute operation.
         * </p>
         * 
         * @param parent
         * @param name
         */
        AddClass(Element parent, Style style) {
            super(parent, style);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply() {
            parent.add(style);
        }
    }

    /**
     * @version 2014/08/31 8:20:20
     */
    static class RemoveClass extends ClassPatch {

        /**
         * <p>
         * Create remove attribute operation.
         * </p>
         * 
         * @param parent
         * @param name
         */
        RemoveClass(Element parent, Style className) {
            super(parent, className);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply() {
            parent.remove(style);
        }
    }

    /**
     * @version 2015/01/13 16:28:58
     */
    static class SetInlineStyle extends Patch {

        /** The property name. */
        private final String name;

        /** The property value. */
        private final String value;

        /**
         * @param parent
         * @param style
         */
        SetInlineStyle(Element parent, String name, String value) {
            super(parent);

            this.name = name;
            this.value = value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply() {
            parent.style().set(name, value);
        }
    }

    /**
     * @version 2015/01/13 16:31:55
     */
    static class RemoveInlineStyle extends Patch {

        /** The property name. */
        private final String name;

        /**
         * @param parent
         * @param style
         */
        RemoveInlineStyle(Element parent, String name) {
            super(parent);

            this.name = name;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply() {
            parent.style().remove(name);
        }
    }
}

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
import java.util.Objects;

import org.eclipse.swt.widgets.Composite;

import consolio.bebop.ui.UIBuilder.UINode;

/**
 * @version 2017/02/12 2:02:33
 */
class Diff {

    static void apply(Composite composite, UIBuilder prev, UIBuilder next) {
        List<Patch> patches = new ArrayList();
        patches.addAll(diff(composite, prev.root, next.root));

        for (Patch patch : patches) {
            System.out.println(patch);
            patch.run();
        }
    }

    /**
     * <p>
     * Diff child nodes.
     * </p>
     * 
     * @param prev A previouse state.
     * @param next A next state.
     * @return
     */
    static List<Patch> diff(Composite composite, List<UINode> prev, List<UINode> next) {
        List<Patch> patches = new ArrayList();

        int prevSize = prev.size();
        int nextSize = next.size();
        int max = prevSize + nextSize;
        int prevPosition = 0;
        int nextPosition = 0;
        int actualManipulationPosition = 0;

        for (int i = 0; i < max; i++) {
            if (prevSize <= prevPosition) {
                if (nextSize <= nextPosition) {
                    break; // all items were scanned
                } else {
                    // all prev items are scanned, but next items are remaining
                    UINode nextItem = next.get(nextPosition++);
                    int index = prev.indexOf(nextItem);

                    if (index == -1) {
                        patches.add(new InsertChild(composite, index, nextItem));
                    } else {
                        patches.add(new MoveChild(composite, nextItem));
                    }
                }
            } else {
                if (nextSize <= nextPosition) {
                    // all next items are scanned, but prev items are remaining
                    patches.add(new RemoveChild(composite, prev.get(prevPosition++)));
                } else {
                    // prev and next items are remaining
                    UINode prevItem = prev.get(prevPosition);
                    UINode nextItem = next.get(nextPosition);

                    if (prevItem.id == nextItem.id) {
                        // same item
                        patches.addAll(diff((Composite) composite.getChildren()[prevPosition], prevItem.children, nextItem.children));

                        actualManipulationPosition++;
                        prevPosition++;
                        nextPosition++;
                    } else {
                        // different item
                        int nextItemInPrev = prev.indexOf(nextItem);
                        int prevItemInNext = next.indexOf(prevItem);

                        if (nextItemInPrev == -1) {
                            if (prevItemInNext == -1) {
                                patches.add(new ReplaceChild(composite, prevItem, nextItem));
                                prevPosition++;
                            } else {
                                patches.add(new InsertChild(composite, prevItemInNext, nextItem));
                            }
                            nextPosition++;
                            actualManipulationPosition++;
                        } else {
                            if (prevItemInNext == -1) {
                                patches.add(new RemoveChild(composite, prevItem));
                            } else {
                                // both items are found in each other list
                                // hold and skip the current value
                                actualManipulationPosition++;
                            }
                            prevPosition++;
                        }
                    }
                }
            }
        }
        return patches;
    }

    /**
     * @version 2017/02/12 2:20:55
     */
    private static abstract class Patch implements Runnable {

        /** The parent SWT widget. */
        protected final Composite parent;

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
        private Patch(Composite parent, UINode child) {
            this.parent = Objects.requireNonNull(parent);
            this.child = child;
        }
    }

    /**
     * @version 2017/02/12 12:16:10
     */
    private static class RemoveChild extends Patch {

        /**
         * <p>
         * Create child remove operation.
         * </p>
         * 
         * @param parent
         * @param child
         */
        RemoveChild(Composite parent, UINode child) {
            super(parent, child);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
        }
    }

    /**
     * @version 2017/02/12 12:16:13
     */
    private static class InsertChild extends Patch {

        /** The index to insert. */
        private final int index;

        /**
         * <p>
         * Create child insert operation.
         * </p>
         * 
         * @param parent
         * @param index
         * @param child
         */
        InsertChild(Composite parent, int index, UINode child) {
            super(parent, child);

            this.index = index;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            child.ui.materialize(parent, child.model, child.children);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "Insert " + child + " at " + index;
        }
    }

    /**
     * @version 2017/02/12 12:16:17
     */
    private static class MoveChild extends Patch {

        /** The child node to move. */
        private final UINode child;

        /**
         * <p>
         * Create child move operation.
         * </p>
         * 
         * @param parent
         * @param child
         */
        MoveChild(Composite parent, UINode child) {
            super(parent, null);

            this.child = child;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
        }
    }

    /**
     * @version 2017/02/12 12:16:21
     */
    private static class ReplaceChild extends Patch {

        /** The new contents to replace. */
        private final UINode replace;

        /**
         * <p>
         * Create child replace operation.
         * </p>
         * 
         * @param parent
         * @param child
         * @param replace
         */
        ReplaceChild(Composite parent, UINode child, UINode replace) {
            super(parent, child);

            this.replace = replace;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
        }
    }
}

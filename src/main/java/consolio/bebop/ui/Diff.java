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

import org.eclipse.swt.widgets.Widget;

import consolio.bebop.ui.UIBuilder.UINode;

/**
 * @version 2017/02/12 2:02:33
 */
class Diff {

    static List<Patch> Diff(UIBuilder prev, UIBuilder next) {
        List<Patch> patches = new ArrayList();

        return patches;
    }

    /**
     * <p>
     * Diff child nodes.
     * </p>
     * 
     * @param context
     * @param prev A previouse state.
     * @param next A next state.
     * @return
     */
    static List<Patch> diff(Widget context, UINode prev, UINode next) {
        List<Patch> patches = new ArrayList();

        int prevSize = prev.children.size();
        int nextSize = next.children.size();
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
                    UINode nextItem = next.children.get(nextPosition++);
                    int index = prev.children.indexOf(nextItem);

                    if (index == -1) {
                        patches.add(new InsertChild(context, null, nextItem));
                    } else {
                        VirtualNode prevItem = prev.items.get(index);

                        /**
                         * {@link VirtualNode#dom}
                         * <p>
                         * We passes the Real DOM from the previous Virtual DOM to the next Virtual
                         * DOM. To tell the truth, we don't want to manipulate Real DOM in here. But
                         * here is the best place to pass the reference.
                         * </p>
                         */
                        nextItem.dom = prevItem.dom;

                        patches.add(new MoveChild(context, prevItem.dom));
                    }
                }
            } else {
                if (nextSize <= nextPosition) {
                    // all next items are scanned, but prev items are remaining
                    patches.add(new RemoveChild(context, prev.items.get(prevPosition++)));
                } else {
                    // prev and next items are remaining
                    VirtualNode prevItem = prev.items.get(prevPosition);
                    VirtualNode nextItem = next.items.get(nextPosition);

                    if (prevItem.id == nextItem.id) {
                        // same item

                        if (prevItem instanceof VirtualElement) {
                            VirtualElement prevElement = (VirtualElement) prevItem;
                            VirtualElement nextElement = (VirtualElement) nextItem;

                            patches.addAll(diff(prevElement, nextElement));
                        } else {
                            /**
                             * {@link VirtualNode#dom}
                             * <p>
                             * We passes the Real DOM from the previous Virtual DOM to the next
                             * Virtual DOM. To tell the truth, we don't want to manipulate Real DOM
                             * in here. But here is the best place to pass the reference.
                             * </p>
                             */
                            nextItem.dom = prevItem.dom;
                        }

                        actualManipulationPosition++;
                        prevPosition++;
                        nextPosition++;
                    } else {
                        // different item
                        int nextItemInPrev = prev.indexOf(nextItem);
                        int prevItemInNext = next.indexOf(prevItem);

                        if (nextItemInPrev == -1) {
                            if (prevItemInNext == -1) {
                                if (prevItem instanceof VirtualText && nextItem instanceof VirtualText) {
                                    /**
                                     * {@link VirtualNode#dom}
                                     * <p>
                                     * We passes the Real DOM from the previous Virtual DOM to the
                                     * next Virtual DOM. To tell the truth, we don't want to
                                     * manipulate Real DOM in here. But here is the best place to
                                     * pass the reference.
                                     * </p>
                                     */
                                    nextItem.dom = prevItem.dom;

                                    patches.add(new ReplaceText(prevItem, (VirtualText) nextItem));
                                } else {
                                    patches.add(new ReplaceChild(context, prevItem, nextItem));
                                }
                                prevPosition++;
                            } else {
                                patches.add(new InsertChild(context, prevItem.dom, nextItem));
                            }
                            nextPosition++;
                            actualManipulationPosition++;
                        } else {
                            if (prevItemInNext == -1) {
                                patches.add(new RemoveChild(context, prevItem));
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
}

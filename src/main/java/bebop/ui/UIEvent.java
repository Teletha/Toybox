/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.ui;

import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bebop.model.SelectableListener;
import kiss.I;

/**
 * @version 2012/03/01 22:50:35
 */
public enum UIEvent {

    /** The model event type. */
    Model_Select,

    /** The model event type. */
    Model_Deselect,

    /** The model event type. */
    Model_Add,

    /** The model event type. */
    Model_Remove,

    /** The ui event type. */
    Close,

    /** The ui event type. */
    Minimize,

    /** The ui event type. */
    Maximize,

    /** The ui event type. */
    Restore,

    /** The ui event type. */
    ShowList,

    /** The ui event type. */
    Activate(SWT.Activate),

    /** The ui event type. */
    Deactivate(SWT.Deactivate),

    /** The ui event type. */
    Dispose(SWT.Dispose),

    /** The ui event type. */
    FocusIn(SWT.FocusIn),

    /** The ui event type. */
    FocusOut(SWT.FocusOut),

    /** The ui event type. */
    SetData(SWT.SetData),

    /** The ui event type. */
    EraseItem(SWT.EraseItem),

    /** The ui event type. */
    Resize(SWT.Resize),

    /** The ui event type. */
    MouseDown(SWT.MouseDown),

    /** The ui event type. */
    MouseUp(SWT.MouseUp),

    /** The ui event type. */
    MouseMove(SWT.MouseMove),

    /** The ui event type. */
    MouseDoubleClick(SWT.MouseDoubleClick),

    /** The ui event type. */
    MouseDragStart(SWT.DragDetect),

    /** The ui event type. */
    Selection(SWT.Selection);

    /** The event type. */
    private final int type;

    /**
     * 
     */
    private UIEvent() {
        this(-1);
    }

    /**
     * @param type
     */
    private UIEvent(int type) {
        this.type = type;
    }

    /**
     * @version 2012/03/01 22:55:57
     */
    private static class UIEventListener implements Listener, CTabFolder2Listener {

        /** The actual event listener. */
        private final Object listener;

        /** The actual event method. */
        private final Method delegator;

        /** The actual event method parameter. */
        private final boolean hasParam;

        /** The event type. */
        private final UIEvent type;

        /**
         * @param listener
         * @param delegator
         * @param type
         */
        private UIEventListener(Object listener, Method delegator, UIEvent type) {
            this.listener = listener;
            this.delegator = delegator;
            this.type = type;
            this.hasParam = delegator.getParameterTypes().length == 1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void handleEvent(Event event) {
            try {
                if (hasParam) {
                    delegator.invoke(listener, event);
                } else {
                    delegator.invoke(listener);
                }
            } catch (Exception e) {
                throw I.quiet(e);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close(CTabFolderEvent event) {
            if (type == Close) {
                try {
                    delegator.invoke(listener, event);
                } catch (Exception e) {
                    throw I.quiet(e);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void minimize(CTabFolderEvent event) {
            if (type == Minimize) {
                try {
                    delegator.invoke(listener, event);
                } catch (Exception e) {
                    throw I.quiet(e);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void maximize(CTabFolderEvent event) {
            if (type == Maximize) {
                try {
                    delegator.invoke(listener, event);
                } catch (Exception e) {
                    throw I.quiet(e);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void restore(CTabFolderEvent event) {
            if (type == Restore) {
                try {
                    delegator.invoke(listener, event);
                } catch (Exception e) {
                    throw I.quiet(e);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void showList(CTabFolderEvent event) {
            if (type == ShowList) {
                try {
                    delegator.invoke(listener, event);
                } catch (Exception e) {
                    throw I.quiet(e);
                }
            }
        }
    }

    /**
     * @version 2012/03/02 0:55:12
     */
    private static class ModelEventListener implements SelectableListener {

        /** The actual event listener. */
        private final Object listener;

        /** The actual event method. */
        private final Method delegator;

        /** The event type. */
        private final UIEvent type;

        /**
         * @param listener
         * @param delegator
         * @param type
         */
        private ModelEventListener(Object listener, Method delegator, UIEvent type) {
            this.listener = listener;
            this.delegator = delegator;
            this.type = type;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void select(Object item) {
            if (type == Model_Select) {
                try {
                    delegator.invoke(listener, item);
                } catch (Exception e) {
                    throw I.quiet(e);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void deselect(Object item) {
            if (type == Model_Deselect) {
                try {
                    delegator.invoke(listener, item);
                } catch (Exception e) {
                    throw I.quiet(e);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void add(Object item) {
            if (type == Model_Add) {
                try {
                    delegator.invoke(listener, item);
                } catch (Exception e) {
                    throw I.quiet(e);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remove(Object item, int index) {
            if (type == Model_Remove) {
                try {
                    delegator.invoke(listener, item, index);
                } catch (Exception e) {
                    throw I.quiet(e);
                }
            }
        }
    }
}

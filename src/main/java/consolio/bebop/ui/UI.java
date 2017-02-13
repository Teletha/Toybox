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

import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import consolio.bebop.ui.Key.With;
import kiss.Events;

/**
 * @version 2017/02/11 23:40:56
 */
public class UI {

    /** The UI thread executor. */
    public static final Consumer<Runnable> Thread = Display.getDefault()::asyncExec;

    /** The model key. */
    static final String KeyModel = UI.class.getName() + "$Model$";

    /**
     * <p>
     * Ensure that the specified process runs in UI thread.
     * </p>
     * 
     * @param process
     */
    public static void process(Runnable process) {
        if (process != null) {
            Display display = Display.getCurrent();

            if (display != null) {
                process.run();
            } else {
                Thread.accept(process);
            }
        }
    }

    /**
     * <p>
     * Create {@link TabFolder}.
     * </p>
     * 
     * @return A new {@link TabFolder}.
     */
    public static <M extends Selectable<C>, C> TabFolder<M, C> tab(Class<M> type) {
        return new TabFolder();
    }

    /**
     * <p>
     * Listen the target user action at the specified {@link Widget}.
     * </p>
     * 
     * @param actions A list of target user action to listen.
     * @return An event source locator.
     */
    public static Locator when(User... actions) {
        return new Locator(actions);
    }

    /**
     * @version 2017/02/09 1:55:13
     */
    public static class Locator {

        /** The actions. */
        private final User[] actions;

        /**
         * Hide constructor.
         */
        private Locator(User[] actions) {
            this.actions = actions;
        }

        /**
         * <p>
         * Locate event source.
         * </p>
         * 
         * @param ui A target {@link AbstractUI}.
         * @return An {@link Events} stream.
         */
        public Events<Event> at(AbstractUI<?> ui) {
            return new Events<>(observer -> {
                BiConsumer<User, Event> listener = (user, e) -> {
                    if (user.condition.test(e)) observer.accept(e);
                };

                // register
                for (User action : actions) {
                    ui.listeners.push(action, listener);
                }

                // unregister
                return () -> {
                    for (User action : actions) {
                        ui.listeners.pull(action, listener);
                    }
                };
            });
        }

        /**
         * <p>
         * Locate event source.
         * </p>
         * 
         * @param widget A target {@link Widget}.
         * @return An {@link Events} stream.
         */
        public Events<Event> at(Widget widget) {
            return new Events<>(observer -> {
                Listener listener = e -> {
                    observer.accept(e);
                };

                // register
                for (User action : actions) {
                    widget.addListener(action.type, listener);
                }

                // unregister
                return () -> {
                    for (User action : actions) {
                        widget.removeListener(action.type, listener);
                    }
                };
            });
        }

        /**
         * <p>
         * Locate event source.
         * </p>
         * 
         * @param widget A target {@link Widget}.
         * @return An {@link Events} stream.
         */
        public <W extends Widget> Events<W> in(W widget) {
            return at(widget).mapTo(widget);
        }
    }

    /**
     * <p>
     * Listen the target user key action at the specified {@link Widget}.
     * </p>
     * 
     * @param key A targe key to listen.
     * @param modifiers A list of modifier keys.
     * @return An event source locator.
     */
    public static KeyLocator whenUserPress(Key key, With... modifiers) {
        return new KeyLocator(true, key, modifiers);
    }

    /**
     * <p>
     * Listen the target user key action at the specified {@link Widget}.
     * </p>
     * 
     * @param key A targe key to listen.
     * @param modifiers A list of modifier keys.
     * @return An event source locator.
     */
    public static KeyLocator whenUserRelease(Key key, With... modifiers) {
        return new KeyLocator(false, key, modifiers);
    }

    /**
     * @version 2017/02/09 5:02:38
     */
    public static class KeyLocator {

        /** press or release. */
        private final User type;

        /** The key code. */
        private final Key key;

        /** The key modifier. */
        private int modifier;

        /**
         * Hide constructor.
         */
        private KeyLocator(boolean press, Key key, With... modifiers) {
            this.type = press ? User.KeyDown : User.KeyUp;
            this.key = key;

            for (With with : modifiers) {
                modifier |= with.mask;
            }
        }

        /**
         * <p>
         * Locate event source.
         * </p>
         * 
         * @param ui A target {@link AbstractUI}.
         * @return An {@link Events} stream.
         */
        public Events<Event> at(AbstractUI ui) {
            return new Events<>(observer -> {
                BiConsumer<User, Event> listener = (user, e) -> {
                    if (e.keyCode == key.code && e.stateMask == modifier) {
                        observer.accept(e);
                    }
                };

                // register
                ui.listeners.push(type, listener);

                // unregister
                return () -> {
                    ui.listeners.pull(type, listener);
                };
            });
        }

        /**
         * <p>
         * Locate event source.
         * </p>
         * 
         * @param ui A target {@link AbstractUI}.
         * @return An {@link Events} stream.
         */
        public Events<Event> at(Widget ui) {
            return new Events<>(observer -> {
                Listener listener = e -> {
                    if (e.keyCode == key.code && e.stateMask == modifier) {
                        observer.accept(e);
                    }
                };

                // register
                ui.addListener(type.type, listener);

                // unregister
                return () -> {
                    ui.removeListener(type.type, listener);
                };
            });
        }
    }
}

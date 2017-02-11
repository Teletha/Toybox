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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import bebop.input.Key;
import kiss.Events;

/**
 * @version 2017/02/08 21:07:30
 */
public class UI {

    /** The UI thread executor. */
    public static final Consumer<Runnable> Thread = Display.getDefault()::asyncExec;

    /** The model key. */
    static final String KeyModel = UI.class.getName() + "$Model$";

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
     * <p>
     * Listen the target user action at the specified {@link Widget}.
     * </p>
     * 
     * @param actions A list of target user action to listen.
     * @return An event source locator.
     */
    public static KeyLocator whenPress(Key key) {
        return new KeyLocator(key);
    }

    /**
     * @version 2017/02/09 5:02:38
     */
    public static class KeyLocator {

        private final Key key;

        /**
         * Hide constructor.
         */
        private KeyLocator(Key key) {
            this.key = key;
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
                    if (user.condition.test(e)) observer.accept(e);
                };

                // register
                ui.listeners.push(User.KeyDown, listener);

                // unregister
                return () -> {
                    ui.listeners.pull(User.KeyDown, listener);
                };
            });
        }
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
}

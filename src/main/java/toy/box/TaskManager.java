/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toy.box;

import java.util.ArrayList;
import java.util.List;

import kiss.Disposable;
import kiss.I;
import kiss.Manageable;
import kiss.Preference;
import toybox.cli.Command;
import toybox.cli.CommandLine;
import bebop.InUIThread;
import bebop.InWorkerThread;
import consolio.util.NativeProcess;

/**
 * @version 2013/01/07 2:06:21
 */
@Manageable(lifestyle = Preference.class)
public class TaskManager {

    /** The command history. */
    private List<String> history = new ArrayList();

    /** The history index. */
    private int historyIndex = 0;

    /**
     * Get the history property of this {@link ToyboxConfiguration}.
     * 
     * @return The history property.
     */
    public List<String> getHistory() {
        return history;
    }

    /**
     * Set the history property of this {@link ToyboxConfiguration}.
     * 
     * @param history The history value to set.
     */
    public void setHistory(List<String> history) {
        if (history != null) {
            this.history = history;
            this.historyIndex = history.size();
        }
    }

    /**
     * <p>
     * Execute task in the specified console.
     * </p>
     * 
     * @param ui
     * @param input
     */
    Disposable execute(Console console, ConsoleUI ui, String input) {
        addHistory(input);

        Command command = CommandLine.parse(input);

        if (command != null && command instanceof Task) {
            // toybox command
            ToyboxTask task = I.make(ToyboxTask.class);
            task.set(console, ui, (Task) command);

            return task;
        } else {
            // native command
            return NativeProcess.execute(input, console.getContext().toPath(), ui);
        }
    }

    /**
     * Add task history.
     * 
     * @param task
     */
    private void addHistory(String task) {
        int size = history.size();

        // reset index
        historyIndex = size;

        // check duplicate
        for (int i = 0; i < history.size(); i++) {
            if (task.equals(history.get(i))) {
                history.add(history.remove(i));
                return;
            }
        }

        // update index
        if (50 == size) {
            history.remove(0);
        } else {
            historyIndex++;
        }

        // add history
        history.add(task);
    }

    /**
     * Find next command.
     * 
     * @return
     */
    public String next() {
        if (history.size() == 0) {
            return "";
        }

        if (historyIndex < history.size()) {
            historyIndex++;
        }

        if (history.size() == historyIndex) {
            return "";
        }
        return history.get(historyIndex);
    }

    /**
     * Find previous commnad.
     * 
     * @return
     */
    public String prev() {
        if (history.size() == 0) {
            return "";
        }

        if (0 < historyIndex) {
            historyIndex--;
        }

        return history.get(historyIndex);
    }

    /**
     * @version 2011/12/02 0:20:47
     */
    private static class ToyboxTask implements Disposable {

        /** The actual task. */
        private Task command;

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {
        }

        /**
         * @param ui
         * @param command
         */
        private void set(Console console, ConsoleUI ui, Task command) {
            this.command = command;
            this.command.console = console;
            this.command.ui = ui;

            work();
        }

        /**
         * {@inheritDoc}
         */
        @InWorkerThread
        protected void work() {
            command.execute(null);

            done();
        }

        /**
         * {@inheritDoc}
         */
        @InUIThread
        protected void done() {
            command.ui.writeConsoleText();
        }
    }
}

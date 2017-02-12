/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package consolio;

import java.util.ArrayList;
import java.util.List;

import bebop.InWorkerThread;
import consolio.ConsoleView.ConsoleText;
import consolio.model.Console;
import consolio.util.NativeProcess;
import kiss.Disposable;
import kiss.Manageable;
import kiss.Preference;

/**
 * @version 2017/02/13 8:14:34
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
    @InWorkerThread
    Disposable execute(Console console, ConsoleText ui, String input) {
        addHistory(input);

        return NativeProcess.execute(input, console.getContext().toPath(), ui);
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
}

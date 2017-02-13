/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.task;

import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * @version 2017/02/13 10:32:55
 */
public class Worker {

    /** The Woker thread executor. */
    public static final Consumer<Runnable> Thread = Worker::process;

    /** The thread pool. */
    private static final Workers workers = new Workers();

    /**
     * <p>
     * Ensure that the specified process runs in worker thread.
     * </p>
     * 
     * @param process
     */
    public static void process(Runnable process) {
        workers.submit(process);
    }

    /**
     * @version 2017/02/13 10:27:45
     */
    private static class Workers extends ThreadPoolExecutor {

        /**
         * Hide constructor.
         */
        private Workers() {
            super(4, 16, 30, SECONDS, new SynchronousQueue(), runnable -> {
                Thread thread = new Thread(runnable, "UI Friendly Worker Task Thread");
                thread.setDaemon(true);

                return thread;
            }, new AbortPolicy());
        }
    }
}

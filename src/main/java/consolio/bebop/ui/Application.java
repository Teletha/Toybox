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

import static consolio.bebop.ui.UI.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.util.concurrent.TimeUnit.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import consolio.model.Configurable;
import kiss.I;

/**
 * @version 2017/02/08 20:00:04
 */
public abstract class Application {

    /** The application shell. */
    protected final Shell shell = new Shell();

    /** The termination flag. */
    private volatile boolean terminate = false;

    /**
     * Initialize application.
     */
    protected Application() {
    }

    /**
     * <p>
     * Start this application.
     * </p>
     */
    public abstract void start();

    /**
     * <p>
     * Stop this application.
     * </p>
     */
    public final void stop() {
        terminate = true;
    }

    /**
     * <p>
     * Make this application active.
     * </p>
     */
    public final void active() {
        shell.forceActive();
    }

    /**
     * <p>
     * Launch the specified application.
     * </p>
     * 
     * @param applicationClass An application to activate.
     * @param args A list of arguments for application.
     */
    protected static final void launch(Class<? extends Application> applicationClass, String... args) {
        launch(applicationClass, null, args);
    }

    /**
     * <p>
     * Launch the specified application.
     * </p>
     * 
     * @param applicationClass An application to activate.
     * @param policy An activation policy.
     * @param args A list of arguments for application.
     */
    protected static final void launch(Class<? extends Application> applicationClass, ActivationPolicy policy, String... args) {
        try {
            new Activator(applicationClass, policy, args);
        } catch (Throwable e) {
            // handle appication error
            Path path = I.locate(applicationClass);

            if (Files.isDirectory(path)) {
                e.printStackTrace(System.out);
            } else {
                try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(I.locate("error.log"), I.$encoding))) {
                    e.printStackTrace(writer);
                } catch (IOException io) {
                    io.printStackTrace(System.out);
                }
            }
        }
    }

    /**
     * @version 2017/02/08 20:02:40
     */
    protected static enum ActivationPolicy {

        /**
         * Continue to process the earliest application. The subsequent applications will not start
         * up.
         */
        Earliest,

        /**
         * Application has multiple processes.
         */
        Multiple,

        /**
         * Terminate the prior applications immediately, then the latest application will start up.
         */
        Latest;
    }

    /**
     * <p>
     * The activation manager.
     * </p>
     * 
     * @version 2017/02/08 20:04:34
     */
    private static class Activator {

        /** The current running application. */
        private Application application;

        /**
         * <p>
         * Hide constructor.
         * </p>
         * 
         * @param applicationClass An application to activate.
         * @param policy An activation policy.
         */
        @SuppressWarnings("resource")
        private Activator(Class<? extends Application> applicationClass, ActivationPolicy policy, String[] params) {
            Objects.requireNonNull(applicationClass, "Please specify the application class.");

            if (policy == null) {
                policy = ActivationPolicy.Earliest;
            }

            // =====================================================================
            // Validation Phase
            // =====================================================================
            if (policy != ActivationPolicy.Multiple) {
                try {
                    // create application specified directory for lock
                    Path root = I.locate(System.getProperty("java.io.tmpdir")).resolve(applicationClass.getName());

                    if (Files.notExists(root)) {
                        Files.createDirectory(root);
                    }

                    // try to retrieve lock to validate
                    FileChannel channel = new RandomAccessFile(root.resolve("lock").toFile(), "rw").getChannel();
                    FileLock lock = channel.tryLock();

                    if (lock == null) {
                        // another application is activated
                        if (policy == ActivationPolicy.Earliest) {
                            // make the window active
                            touch(root.resolve("active"));

                            return;
                        } else {
                            // close the window
                            touch(root.resolve("close"));

                            // wait for shutdown previous application
                            channel.lock();
                        }
                    }

                    // observe lock directory for next application
                    I.observe(root).to(e -> {
                        if (e.kind() == ENTRY_CREATE || e.kind() == ENTRY_MODIFY) {
                            notify(e.context());
                        }
                    });
                } catch (Exception e) {
                    throw I.quiet(e);
                }
            }

            // =====================================================================
            // Launching Phase
            // =====================================================================
            // load UI library
            I.load(Application.class, true);

            // load application library
            I.load(applicationClass, true);

            // build application
            application = I.make(applicationClass);

            // define user specified UI
            application.start();

            initialize();

            // run the event loop as long as the window is open
            Display display = Display.getCurrent();

            while (!application.terminate) {
                // read the next OS event queue and transfer it to a SWT event
                if (!display.readAndDispatch()) {
                    // if there are currently no other OS event to process
                    // sleep until the next OS event is available
                    if (!application.terminate) display.sleep();
                }
            }

            // Clean up resources.
            display.dispose();
        }

        /**
         * 
         */
        private void initialize() {
            Shell shell = application.shell;

            // initialize application's common abilities
            I.make(WindowPreference.class).restore().size(shell);

            when(User.Close).at(shell).to(application::stop);

            // show UI
            shell.open();
        }

        /**
         * <p>
         * Implements the same behaviour as the "touch" utility on Unix. It creates a new file with
         * size 0 or, if the file exists already, it is opened and closed without modifying it, but
         * updating the file date and time.
         * </p>
         * 
         * @param path
         */
        private void touch(Path path) throws IOException {
            if (Files.exists(path)) {
                Files.setLastModifiedTime(path, FileTime.fromMillis(System.currentTimeMillis()));
            } else {
                Files.createFile(path);
            }
        }

        /**
         * <p>
         * Notify modification.
         * </p>
         * 
         * @param path
         */
        private void notify(Path path) {
            String message = path.getFileName().toString();

            if (message.equals("active")) {
                // active window
                application.active();
            } else if (message.equals("close") && Files.exists(path)) {
                // close window
                application.stop();

                // wake up if ui thread is sleeping
                Display.getCurrent().wake();
            }
        }
    }

    /**
     * @version 2017/02/09 3:12:02
     */
    private static class WindowPreference implements Configurable<WindowPreference> {

        /** The physical display. */
        private Rectangle screen = Display.getDefault().getPrimaryMonitor().getClientArea();

        /** The size and location. */
        public Rectangle bounds = new Rectangle(screen.width / 4, screen.height / 4, screen.width / 2, screen.height / 2);

        /** The window state. */
        public WindowState state = WindowState.Normal;

        /**
         * <p>
         * Apply window size and location setting.
         * </p>
         * 
         * @param shell A target to apply.
         */
        private void size(Shell shell) {
            shell.setBounds(bounds);

            switch (state) {
            case Max:
                shell.setMaximized(true);
                break;

            case Min:
                shell.setMinimized(true);
                break;
            }

            when(User.Move, User.Resize).in(shell).debounce(500, MILLISECONDS).on(UI.Thread).to(e -> {
                if (shell.getMaximized()) {
                    state = WindowState.Max;
                } else if (shell.getMinimized()) {
                    state = WindowState.Min;
                } else {
                    state = WindowState.Normal;
                    bounds = shell.getBounds();
                }
                store();
            });
        }
    }

    /**
     * @version 2017/02/10 0:42:50
     */
    private static enum WindowState {
        Max, Min, Normal;
    }
}

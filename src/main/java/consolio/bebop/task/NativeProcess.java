/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package consolio.bebop.task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import kiss.Disposable;
import kiss.I;

/**
 * @version 2013/01/07 12:07:29
 */
public class NativeProcess implements Disposable {

    /** The native process. */
    private Process process;

    /** The message listener. */
    private NativeProcessListener listener;

    /** The current buffered message. */
    private StringBuilder buffer = new StringBuilder();

    /**
     * Hide constructor.
     */
    protected NativeProcess() {
    }

    /**
     * <p>
     * Execute native command.
     * </p>
     * 
     * @param command A native command.
     */
    public static NativeProcess execute(String command) {
        return execute(command, null, null);
    }

    /**
     * <p>
     * Execute native command.
     * </p>
     * 
     * @param command A native command.
     * @param directory A working directory.
     * @return A native command process.
     */
    public static NativeProcess execute(String command, Path directory) {
        return execute(command, directory, null);
    }

    /**
     * <p>
     * Execute native command.
     * </p>
     * 
     * @param command A native command.
     * @param listener A command message listener.
     * @return A native command process.
     */
    public static NativeProcess execute(String command, NativeProcessListener listener) {
        return execute(command, null, listener);
    }

    /**
     * <p>
     * Execute native command.
     * </p>
     * 
     * @param command A native command.
     * @param directory A working directory.
     * @param listener A command message listener.
     * @return A native command process.
     */
    public static NativeProcess execute(String command, Path directory, NativeProcessListener listener) {
        List<String> list = new ArrayList();

        if (Platform.isWindows()) {
            list.add("cmd");
            list.add("/c");
        } else {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }
        list.add(command);

        ProcessBuilder builder = new ProcessBuilder(list);
        builder.redirectErrorStream(true);
        if (directory != null) builder.directory(directory.toFile());

        try {
            NativeProcess process = I.make(NativeProcess.class);
            process.process = builder.start();
            process.listener = listener;

            if (listener != null) {
                process.read();
            }

            return process;
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Read process output.
     * </p>
     */
    protected void read() {
        Reader reader = null;

        try {
            // fire start event
            listener.start(new OutputStreamWriter(process.getOutputStream(), "MS932"));
            reader = new InputStreamReader(process.getInputStream(), "MS932");

            while (true) {
                if (!reader.ready() && buffer.length() != 0) {
                    write("");
                }

                // read next character
                int current = reader.read();

                if (current == -1) {
                    return; // EOF
                }

                char c = (char) current;

                if (c == '\r') {
                    int next = reader.read();

                    if (next == -1) {
                        return; // EOF
                    }

                    char n = (char) next;

                    if (n == '\n') {
                        write("\r\n");
                        continue;
                    } else {
                        write("\r");
                        buffer.append(n);
                        continue;
                    }
                }

                if (c == '\n') {
                    write("\n");
                    continue;
                }
                buffer.append(c);
            }
        } catch (IOException e) {
            // terminate sub process
        } catch (Exception e) {
            throw I.quiet(e);
        } finally {
            // output current buffer
            if (buffer.length() != 0) write("");

            // fire end event
            listener.finish();

            // cleanup resources
            I.quiet(reader);
        }
    }

    /**
     * <p>
     * Write current buffer with the specified end line text.
     * </p>
     * 
     * @param end A end line text.
     */
    private void write(String end) {
        listener.message(buffer.toString(), end);

        // reset buffer
        buffer = new StringBuilder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        String command;

        try {
            // search pid and build kill command
            if (Platform.isWindows()) {
                Field field = process.getClass().getDeclaredField("handle");
                field.setAccessible(true);

                command = "taskkill /f /t /pid " + Kernel32.INSTANCE
                        .GetProcessId(new HANDLE(Pointer.createConstant(field.getLong(process))));
            } else {
                Field field = process.getClass().getDeclaredField("pid");
                field.setAccessible(true);

                command = "kill -9 " + field.getLong(process);
            }
        } catch (Exception e) {
            throw I.quiet(e);
        }

        execute(command);
    }
}

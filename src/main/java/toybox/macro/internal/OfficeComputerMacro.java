/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox.macro.internal;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import kiss.I;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import bebop.input.Key;
import bebop.input.Window;

/**
 * @version 2012/09/19 16:26:33
 */
public abstract class OfficeComputerMacro implements NativeKeyListener {

    /** The operation target. */
    final Window window;

    /** The last key detection time. */
    private long time = 0;

    /** The key awareness. */
    private boolean aware = true;

    /** The thread barrier. */
    private final CyclicBarrier barrier = new CyclicBarrier(2);

    /** The last input key. */
    private Key lastInput;

    /**
     * 
     */
    protected OfficeComputerMacro() {
        Window window;

        try {
            window = Window.getWindow("PC-BIND 2000 - [ｾｯｼｮﾝ1]").find("AfxOleControl42", 4);
        } catch (IllegalAccessError e) {
            window = Window.getWindow("PC-BIND 2000");
        }
        this.window = window;
    }

    /**
     * Delegate method to native macto.
     * 
     * @param ms
     * @return
     */
    protected final OfficeComputerMacro delay(int ms) {
        window.delay(ms);

        return this;
    }

    /**
     * Delegate method to native macto.
     * 
     * @param value
     * @return
     */
    protected final OfficeComputerMacro input(int value) {
        window.input(value);

        return this;
    }

    /**
     * Delegate method to native macto.
     * 
     * @param value
     * @return
     */
    protected final OfficeComputerMacro input(Date value) {
        window.input(value);

        return this;
    }

    /**
     * Delegate method to native macto.
     * 
     * @param value
     * @return
     */
    protected final OfficeComputerMacro input(String value) {
        window.input(value);

        return this;
    }

    /**
     * Delegate method to native macto.
     * 
     * @param key
     * @return
     */
    protected final OfficeComputerMacro input(Key key) {
        window.input(key);

        return this;
    }

    protected final OfficeComputerMacro post() {
        window.input(Key.ControlRight).delay(400);

        return this;
    }

    /**
     * @return
     */
    protected final Key waitInput() {
        try {
            barrier.await();

            // show window to reduce window focus error
            window.show().delay(400);

            // API definition
            return lastInput;
        } catch (InterruptedException e) {
            throw I.quiet(e);
        } catch (BrokenBarrierException e) {
            throw I.quiet(e);
        }
    }

    /**
     * @param size
     */
    protected final String copy(int size) {
        // select
        for (int i = 0; i < size; i++) {
            window.input(Key.Right, Key.ShiftLeft);
        }

        // copy
        window.input(Key.E, Key.AltLeft).input(Key.C).delay(100);

        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            String value = (String) clipboard.getData(DataFlavor.stringFlavor);

            // clear clipboard
            clipboard.setContents(new StringSelection(""), null);

            // API definition
            return value.replaceAll("\\s", "");
        } catch (UnsupportedFlavorException | IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * Perform login action.
     * 
     * @param kind
     */
    protected final void login(int kind) {
        window.show();
        window.input(kind).input(Key.ControlRight).delay(10000);
        window.input(42729).input(Key.ControlRight).delay(3000);
        window.input(8980).input(Key.ControlRight).delay(15000);
    }

    /**
     * Perform logout action.
     */
    protected final void logout() {
        window.show().input(Key.F1).delay(2000);
    }

    /**
     * Process each behaver.
     * 
     * @param behaver
     */
    protected abstract void process(Behaver behaver);

    /**
     * Hook macro starting.
     */
    protected void start() {
    }

    /**
     * Hook macro finishing.
     */
    protected void end() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void nativeKeyPressed(NativeKeyEvent event) {
        long current = System.nanoTime();

        if (100 < current - time && aware) {
            // update time
            time = current;

            // update key awareness
            aware = false;

            try {
                // retrieve key code
                switch (event.getKeyCode()) {
                case NativeKeyEvent.VK_F12:
                    // accept
                    lastInput = Key.F12;
                    barrier.await();
                    break;

                case NativeKeyEvent.VK_F11:
                    // cancel
                    lastInput = Key.F11;
                    barrier.await();
                    break;

                case NativeKeyEvent.VK_F10:
                    // restore last state
                    lastInput = Key.F10;
                    break;
                }
            } catch (Exception e) {
                throw I.quiet(e);
            } finally {
                // update key awareness
                aware = true;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void nativeKeyReleased(NativeKeyEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void nativeKeyTyped(NativeKeyEvent event) {
    }
}

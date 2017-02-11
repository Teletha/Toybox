/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package consolio;

import java.awt.Desktop;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kiss.Disposable;
import kiss.I;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bebop.InUIThread;
import bebop.input.KeyBind;
import bebop.ui.AbstractUI;
import bebop.util.Resources;
import consolio.bebop.ui.Key;
import consolio.model.Console;
import consolio.util.NativeProcessListener;

/**
 * @version 2012/03/01 0:55:44
 */
public class ConsoleUI extends AbstractUI<Console, StyledText> implements VerifyKeyListener, NativeProcessListener {

    private static final Random RANDOM = new Random();

    /** The URL pattern. */
    private static final Pattern URL_PATTERN = Pattern.compile("(http|https|file):([^\\x00-\\x20()\"<>\\x7F-\\xFF])*", Pattern.CASE_INSENSITIVE);

    /** The line limiter. */
    private final LineLimiter limiter = new LineLimiter();

    /** The URL detector. */
    private final URIDetector detector = new URIDetector();

    private LineKeeper keeper = new LineKeeper();

    /** The curernt start position of the caret in this pane. */
    private int caretStartPosition = 0;

    /** The command manager. */
    private TaskManager manager = I.make(TaskManager.class);

    /** The state. */
    private boolean editable = true;

    /** The console input. */
    private Writer output;

    /** The current processing task. */
    private Disposable currentTask;

    /**
     * {@inheritDoc}
     */
    @Override
    public StyledText createUI(Composite parent) {
        ui = new StyledText(parent, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        ui.setLineSpacing(1);
        ui.addVerifyKeyListener(this);
        ui.addListener(SWT.MouseDoubleClick, detector);

        setLineLimit(2000);

        setBackground(I.locate("E:\\神\\" + RANDOM.nextInt(32) + ".jpg"), 50);

        // initial text
        writeConsoleText();

        return ui;
    }

    /**
     * <p>
     * Set console output.
     * </p>
     * 
     * @param output
     */
    public void setOutput(Writer output) {
        this.output = output;
    }

    @KeyBind(key = Key.End)
    public void end() {
        ui.setTopIndex(ui.getLineCount() - 1);
    }

    @KeyBind(key = Key.Home)
    public void home() {
        ui.setTopIndex(0);
    }

    @KeyBind(key = Key.Up)
    public void up() {
        ui.replaceTextRange(caretStartPosition, ui.getCharCount() - caretStartPosition, manager.prev());

        // move caret to the end of previous command message
        ui.setCaretOffset(ui.getCharCount());

        end();
    }

    @KeyBind(key = Key.Down)
    public void down() {
        ui.replaceTextRange(caretStartPosition, ui.getCharCount() - caretStartPosition, manager.next());

        // move caret to the end of next command message
        ui.setCaretOffset(ui.getCharCount());

        end();
    }

    @KeyBind(key = Key.C, ctrl = true)
    public void terminate() {
        if (currentTask != null) {
            currentTask.dispose();
            currentTask = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void verifyKey(VerifyEvent event) {
        if (ui.getSelectionCount() != 0) {
            return;
        }

        switch (event.keyCode) {
        case SWT.PAGE_UP:
            event.doit = false;

            ui.setTopPixel(ui.getTopPixel() - ui.getClientArea().height);
            break;

        case SWT.PAGE_DOWN:
            event.doit = false;

            ui.setTopPixel(ui.getTopPixel() + ui.getClientArea().height);
            break;

        case SWT.BS:
        case SWT.ARROW_LEFT:
            if (ui.getCaretOffset() <= caretStartPosition) {
                event.doit = false;
            }
            break;

        case SWT.CR: // enter
            event.doit = false;

            // retrive user input
            String input = ui.getTextRange(caretStartPosition, ui.getCharCount() - caretStartPosition).trim();

            if (editable) {
                if (input.length() != 0) {
                    // disable this console interface
                    disableConsole();

                    ui.append("\r\n");
                    caretStartPosition += input.length() + 2;

                    // execute task
                    currentTask = manager.execute(model, this, input);
                }
            } else {
                inputln(input);
                caretStartPosition += input.length();
                writeln("");
            }
            break;

        default:
            // check caret position
            if (ui.getCaretOffset() < caretStartPosition) {
                ui.setCaretOffset(ui.getCharCount());
            }
            break;
        }
    }

    private int carriageReturned = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Writer writer) {
        setOutput(writer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void message(String message, String eol) {
        write(message + eol);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finish() {
        setOutput(null);
        writeConsoleText();
    }

    /**
     * <p>
     * Write system output to this console.
     * </p>
     * 
     * @param message A message to write.
     */
    @InUIThread
    public void write(CharSequence message) {
        // should we keep bottom line?
        if (ui.getLineIndex(ui.getClientArea().height) == ui.getLineCount() - 1) {
            if (!keeper.use) {
                keeper.use = true;
                ui.addListener(SWT.Modify, keeper);
            }
        } else {
            if (keeper.use) {
                keeper.use = false;
                ui.removeListener(SWT.Modify, keeper);
            }
        }

        // append actual text
        if (0 <= carriageReturned) {
            ui.replaceTextRange(carriageReturned, ui.getCharCount() - carriageReturned, message.toString());
            caretStartPosition = carriageReturned + message.length();
            carriageReturned = -1;
        } else {
            ui.replaceTextRange(caretStartPosition, 0, message.toString());
            caretStartPosition += message.length();
        }

        // wrap indent
        // search header space
        int index = 0;

        for (; index < message.length(); index++) {
            if (!Character.isWhitespace(message.charAt(index))) {
                break;
            }
        }

        if (index != 0) {
            GC canvas = new GC(ui);
            int width = canvas.textExtent(message.subSequence(0, index).toString()).y;
            canvas.dispose();

            ui.setLineWrapIndent(ui.getLineCount() - 2, 1, width);
        }

        if (message.length() != 0 && message.charAt(message.length() - 1) == '\r') {
            carriageReturned = ui.getOffsetAtLine(ui.getLineCount() - 2);
        }

        // start URI pattern matching
        Matcher matcher = URL_PATTERN.matcher(message);

        while (matcher.find()) {
            String link = matcher.group();

            StyleRange style = new StyleRange(caretStartPosition - message.length() + matcher.start(), link.length(), Resources.getColor(0, 51, 200), null);
            style.underline = true;
            style.underlineStyle = SWT.UNDERLINE_LINK;
            style.data = link;

            ui.setStyleRange(style);
        }
    }

    /**
     * <p>
     * Write system output to this console.
     * </p>
     * 
     * @param message A message to write.
     */
    public void writeln(CharSequence message) {
        write(message + "\r\n");
    }

    /**
     * <p>
     * Write user input to the asossiated system output.
     * </p>
     * 
     * @param message
     */
    public void input(CharSequence message) {
        try {
            if (output != null) {
                output.append(message);
                output.flush();
            }
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Write user input of this console to the asossiated system output.
     * </p>
     * 
     * @param message A message to write.
     */
    public void inputln(CharSequence message) {
        input(message + "\r\n");
    }

    /**
     * <p>
     * Set line limit size.
     * </p>
     * 
     * @param size
     */
    public void setLineLimit(int size) {
        if (0 < size) {
            limiter.limit = size;

            ui.addExtendedModifyListener(limiter);
        } else {
            ui.removeExtendedModifyListener(limiter);
        }
    }

    /**
     * Make this console acceptable from user input.
     */
    private void enableConsole() {
        // make console editable
        editable = true;

        // update caret
        ui.getCaret().setVisible(true);

        // update caret position
        ui.setCaretOffset(ui.getCharCount());
    }

    /**
     * Make this console deniable from user input.
     */
    private void disableConsole() {
        // make console unediatable
        editable = false;

        // update caret
        ui.getCaret().setVisible(false);
    }

    /**
     * Helper method to write system message.
     */
    @InUIThread
    protected void writeConsoleText() {
        // create console text
        write(ui.getCharCount() == 0 ? "$ " : "\r\n$ ");

        // open console
        enableConsole();
    }

    /**
     * @version 2011/12/04 1:18:57
     */
    private class URIDetector implements Listener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void handleEvent(Event event) {
            try {
                StyleRange style = ui.getStyleRangeAtOffset(ui.getOffsetAtLocation(new Point(event.x, event.y)));

                if (style != null && style.underlineStyle == SWT.UNDERLINE_LINK) {
                    Desktop.getDesktop().browse(new URI((String) style.data));
                }
            } catch (IllegalArgumentException | URISyntaxException | IOException e) {
                // ignore
            }
        }
    }

    /**
     * @version 2011/12/04 0:57:15
     */
    private class LineLimiter implements ExtendedModifyListener {

        /** The line limit size. */
        private int limit;

        /**
         * {@inheritDoc}
         */
        @Override
        public void modifyText(ExtendedModifyEvent event) {
            int top = ui.getTopIndex();

            while (limit < ui.getLineCount()) {

                int removeSize = ui.getLine(0).length() + ui.getLineDelimiter().length();
                ui.replaceTextRange(0, removeSize, "");

                // update caret position
                caretStartPosition -= removeSize;

                // and current line position
                top--;
            }
            ui.setTopIndex(top);
        }
    }

    /**
     * @version 2011/12/02 14:50:58
     */
    private class LineKeeper implements Listener {

        /** The usage. */
        private boolean use = false;

        /**
         * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
         */
        @Override
        public void handleEvent(Event event) {
            ui.setTopIndex(ui.getLineCount() - 1);
        }
    }
}

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

import static consolio.bebop.ui.UI.*;

import java.awt.Desktop;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import bebop.input.KeyBind;
import bebop.util.Resources;
import consolio.bebop.ui.AbstractUI;
import consolio.bebop.ui.Key;
import consolio.bebop.ui.Materializable;
import consolio.model.Console;
import consolio.util.NativeProcessListener;
import kiss.Disposable;
import kiss.I;

/**
 * @version 2017/02/11 23:58:28
 */
public class ConsoleView extends AbstractUI<Console> {

    /**
     * 
     */
    public ConsoleView() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Materializable materialize(Composite parent, Console model) {
        System.out.println("Materialize ConsoleView " + model + "  " + parent);
        ConsoleText ui = new ConsoleText(parent, model);
        ui.setLineLimit(2000);

        // initial text
        ui.writeConsoleText();

        return null;
    }

    private static final Random RANDOM = new Random();

    /** The URL pattern. */
    private static final Pattern URL_PATTERN = Pattern
            .compile("(http|https|file):([^\\x00-\\x20()\"<>\\x7F-\\xFF])*", Pattern.CASE_INSENSITIVE);

    /**
     * @version 2017/02/12 0:10:00
     */
    public class ConsoleText extends StyledText implements VerifyKeyListener, NativeProcessListener {

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

        private Console model;

        /**
         * @param parent
         * @param style
         */
        private ConsoleText(Composite parent, Console model) {
            super(parent, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);

            this.model = model;
            setLineSpacing(1);
            addVerifyKeyListener(this);
            addListener(SWT.MouseDoubleClick, detector);

            whenUserPress(Key.End).at(this).to(this::end);
            whenUserPress(Key.Home).at(this).to(this::home);
            whenUserPress(Key.Up).at(this).to(this::up);
            whenUserPress(Key.Down).at(this).to(this::down);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void checkSubclass() {
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

        public void end() {
            setTopIndex(getLineCount() - 1);
        }

        public void home() {
            setTopIndex(0);
        }

        public void up() {
            replaceTextRange(caretStartPosition, getCharCount() - caretStartPosition, manager.prev());

            // move caret to the end of previous command message
            setCaretOffset(getCharCount());

            end();
        }

        public void down() {
            replaceTextRange(caretStartPosition, getCharCount() - caretStartPosition, manager.next());

            // move caret to the end of next command message
            setCaretOffset(getCharCount());

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
            if (getSelectionCount() != 0) {
                return;
            }

            switch (event.keyCode) {
            case SWT.PAGE_UP:
                event.doit = false;

                setTopPixel(getTopPixel() - getClientArea().height);
                break;

            case SWT.PAGE_DOWN:
                event.doit = false;

                setTopPixel(getTopPixel() + getClientArea().height);
                break;

            case SWT.BS:
            case SWT.ARROW_LEFT:
                if (getCaretOffset() <= caretStartPosition) {
                    event.doit = false;
                }
                break;

            case SWT.CR: // enter
                event.doit = false;

                // retrive user input
                String input = getTextRange(caretStartPosition, getCharCount() - caretStartPosition).trim();

                if (editable) {
                    if (input.length() != 0) {
                        // disable this console interface
                        disableConsole();

                        append("\r\n");
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
                if (getCaretOffset() < caretStartPosition) {
                    setCaretOffset(getCharCount());
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
        public void write(CharSequence message) {
            // should we keep bottom line?
            if (getLineIndex(getClientArea().height) == getLineCount() - 1) {
                if (!keeper.use) {
                    keeper.use = true;
                    addListener(SWT.Modify, keeper);
                }
            } else {
                if (keeper.use) {
                    keeper.use = false;
                    removeListener(SWT.Modify, keeper);
                }
            }

            // append actual text
            if (0 <= carriageReturned) {
                replaceTextRange(carriageReturned, getCharCount() - carriageReturned, message.toString());
                caretStartPosition = carriageReturned + message.length();
                carriageReturned = -1;
            } else {
                replaceTextRange(caretStartPosition, 0, message.toString());
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
                GC canvas = new GC(this);
                int width = canvas.textExtent(message.subSequence(0, index).toString()).y;
                canvas.dispose();

                setLineWrapIndent(getLineCount() - 2, 1, width);
            }

            if (message.length() != 0 && message.charAt(message.length() - 1) == '\r') {
                carriageReturned = getOffsetAtLine(getLineCount() - 2);
            }

            // start URI pattern matching
            Matcher matcher = URL_PATTERN.matcher(message);

            while (matcher.find()) {
                String link = matcher.group();

                StyleRange style = new StyleRange(caretStartPosition - message.length() + matcher.start(), link.length(), Resources
                        .getColor(0, 51, 200), null);
                style.underline = true;
                style.underlineStyle = SWT.UNDERLINE_LINK;
                style.data = link;

                setStyleRange(style);
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

                addExtendedModifyListener(limiter);
            } else {
                removeExtendedModifyListener(limiter);
            }
        }

        /**
         * Make this console acceptable from user input.
         */
        private void enableConsole() {
            // make console editable
            editable = true;

            // update caret
            getCaret().setVisible(true);

            // update caret position
            setCaretOffset(getCharCount());
        }

        /**
         * Make this console deniable from user input.
         */
        private void disableConsole() {
            // make console unediatable
            editable = false;

            // update caret
            getCaret().setVisible(false);
        }

        /**
         * Helper method to write system message.
         */
        protected void writeConsoleText() {
            // create console text
            write(getCharCount() == 0 ? "$ " : "\r\n$ ");

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
                    StyleRange style = getStyleRangeAtOffset(getOffsetAtLocation(new Point(event.x, event.y)));

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
                int top = getTopIndex();

                while (limit < getLineCount()) {

                    int removeSize = getLine(0).length() + getLineDelimiter().length();
                    replaceTextRange(0, removeSize, "");

                    // update caret position
                    caretStartPosition -= removeSize;

                    // and current line position
                    top--;
                }
                setTopIndex(top);
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
                setTopIndex(getLineCount() - 1);
            }
        }
    }

}

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

import static bebop.ui.UI.*;

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

import bebop.task.NativeProcessListener;
import bebop.ui.AbstractUI;
import bebop.ui.Key;
import bebop.ui.Key.With;
import bebop.ui.Materializer;
import bebop.util.Resources;
import consolio.model.Console;
import kiss.Disposable;
import kiss.I;

/**
 * @version 2017/02/11 23:58:28
 */
public class UIConsole extends AbstractUI<Console> {

    /**
     * 
     */
    public UIConsole() {
    }

    private int lineLimit = 2000;

    /**
     * <p>
     * Set limit of lines.
     * </p>
     * 
     * @param size A limit of lines.
     * @return Chainable API.
     */
    public UIConsole lineLimit(int size) {
        this.lineLimit = 0 < size ? size : Integer.MAX_VALUE;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Materializer createMaterializer(Composite parent, Console model) {
        ConsoleText ui = I.make(ConsoleText.class);
        ui.initialize(parent, model);

        // initial text
        ui.writeConsoleText();

        return new Materializer(ui.ui);
    }

    private static final Random RANDOM = new Random();

    /** The URL pattern. */
    private static final Pattern URL_PATTERN = Pattern
            .compile("(http|https|file):([^\\x00-\\x20()\"<>\\x7F-\\xFF])*", Pattern.CASE_INSENSITIVE);

    /**
     * @version 2017/02/12 0:10:00
     */
    class ConsoleText implements VerifyKeyListener, NativeProcessListener {

        /** The line limiter. */
        private final LineLimiter lineLimiter = new LineLimiter();

        /** The URL detector. */
        private final URIDetector detector = new URIDetector();

        private LineKeeper keeper = new LineKeeper();

        /** The curernt start position of the caret in this pane. */
        private int caretStartPosition = 0;

        /** The command manager. */
        private TaskManager manager = I.make(TaskManager.class).restore();

        /** The state. */
        private boolean editable = true;

        /** The console input. */
        private Writer output;

        /** The current processing task. */
        private Disposable currentTask;

        private StyledText ui;

        private Console model;

        private void initialize(Composite parent, Console model) {
            this.ui = new StyledText(parent, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
            this.model = model;
            ui.setLineSpacing(1);
            ui.addVerifyKeyListener(this);
            ui.addListener(SWT.MouseDoubleClick, detector);
            ui.addExtendedModifyListener(lineLimiter);

            whenUserPress(Key.End).at(ui).to(this::scrollToBottom);
            whenUserPress(Key.Home).at(ui).to(this::scrollToTop);
            whenUserPress(Key.Up).at(ui).to(this::showPreviousTask);
            whenUserPress(Key.Down).at(ui).to(this::showNextTask);
            whenUserPress(Key.C, With.Ctrl).at(ui).to(this::terminate);
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

        public void write(CharSequence message) {
            process(() -> {
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

                    StyleRange style = new StyleRange(caretStartPosition - message.length() + matcher.start(), link.length(), Resources
                            .getColor(0, 51, 200), null);
                    style.underline = true;
                    style.underlineStyle = SWT.UNDERLINE_LINK;
                    style.data = link;

                    ui.setStyleRange(style);
                }
            });
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
        protected void writeConsoleText() {
            process(() -> {
                // create console text
                write(ui.getCharCount() == 0 ? "$ " : "\r\n$ ");

                // open console
                enableConsole();
            });
        }

        /**
         * <p>
         * Show previous task.
         * </p>
         */
        private void showPreviousTask() {
            ui.replaceTextRange(caretStartPosition, ui.getCharCount() - caretStartPosition, manager.prev());

            // move caret to the end of previous command message
            ui.setCaretOffset(ui.getCharCount());

            scrollToBottom();
        }

        /**
         * <p>
         * Show next task.
         * </p>
         */
        private void showNextTask() {
            ui.replaceTextRange(caretStartPosition, ui.getCharCount() - caretStartPosition, manager.next());

            // move caret to the end of next command message
            ui.setCaretOffset(ui.getCharCount());

            scrollToBottom();
        }

        /**
         * <p>
         * Scroll to bottom of this widget.
         * </p>
         */
        private void scrollToBottom() {
            ui.setTopIndex(ui.getLineCount() - 1);
        }

        /**
         * <p>
         * Scroll to top of this widget.
         * </p>
         */
        private void scrollToTop() {
            ui.setTopIndex(0);
        }

        /**
         * <p>
         * Terminate current task.
         * </p>
         */
        private void terminate() {
            if (currentTask != null) {
                currentTask.dispose();
                currentTask = null;
            }
        }

        /**
         * @version 2017/02/13 13:22:34
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
         * @version 2017/02/13 13:22:26
         */
        private class LineLimiter implements ExtendedModifyListener {

            /**
             * {@inheritDoc}
             */
            @Override
            public void modifyText(ExtendedModifyEvent event) {
                int top = ui.getTopIndex();

                while (lineLimit < ui.getLineCount()) {

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
         * @version 2017/02/13 13:22:30
         */
        private class LineKeeper implements Listener {

            /** The usage. */
            private boolean use = false;

            /**
             * {@inheritDoc}
             */
            @Override
            public void handleEvent(Event event) {
                ui.setTopIndex(ui.getLineCount() - 1);
            }
        }
    }

}
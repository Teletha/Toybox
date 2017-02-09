/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox.cli;

import org.junit.Rule;
import org.junit.Test;

import antibug.PrivateModule;
import consolio.cli.Argument;
import consolio.cli.Command;
import consolio.cli.CommandLine;

/**
 * @version 2012/02/29 19:01:30
 */
public class CommandTest {

    @Rule
    public static final PrivateModule module = new PrivateModule();

    @Test
    public void Null() throws Exception {
        assert CommandLine.parse(null) == null;
    }

    @Test
    public void empty() throws Exception {
        assert CommandLine.parse("") == null;
    }

    @Test
    public void noArgument() throws Exception {
        Command command = CommandLine.parse("no-argument");

        assert command != null;
        assert command instanceof NoArgument;
    }

    /**
     * @version 2012/02/29 19:05:59
     */
    private static class NoArgument implements Command<Object> {

        /**
         * {@inheritDoc}
         */
        @Override
        public void execute(Object context) {
        }
    }

    @Test
    public void argument() throws Exception {
        Command command = CommandLine.parse("single-argument one");

        assert command != null;
        assert command instanceof SingleArgument;

        SingleArgument argument = (SingleArgument) command;
        assert argument.name.equals("one");
    }

    @Test
    public void argumentTail() throws Exception {
        Command command = CommandLine.parse("single-argument one drop");

        assert command != null;
        assert command instanceof SingleArgument;

        SingleArgument argument = (SingleArgument) command;
        assert argument.name.equals("one drop");
    }

    @Test
    public void argumentWhitespace() throws Exception {
        Command command = CommandLine.parse("single-argument \" o   e \"");

        assert command != null;
        assert command instanceof SingleArgument;

        SingleArgument argument = (SingleArgument) command;
        assert argument.name.equals(" o   e ");
    }

    /**
     * @version 2012/02/29 19:05:59
     */
    private static class SingleArgument implements Command<Object> {

        @Argument
        private String name;

        /**
         * {@inheritDoc}
         */
        @Override
        public void execute(Object context) {
        }
    }

    @Test
    public void convert() throws Exception {
        Command command = CommandLine.parse("convert-argument 10");

        assert command != null;
        assert command instanceof ConvertArgument;

        ConvertArgument argument = (ConvertArgument) command;
        assert argument.id == 10;
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertInvalid() throws Exception {
        CommandLine.parse("convert-argument invalid");
    }

    /**
     * @version 2012/02/29 19:05:59
     */
    private static class ConvertArgument implements Command<Object> {

        @Argument
        private int id;

        /**
         * {@inheritDoc}
         */
        @Override
        public void execute(Object context) {
        }
    }

    @Test
    public void arguments() throws Exception {
        Command command = CommandLine.parse("multi-argument one 20");

        assert command != null;
        assert command instanceof MultiArgument;

        MultiArgument argument = (MultiArgument) command;
        assert argument.name.equals("one");
        assert argument.id == 20;
    }

    @Test(expected = IllegalArgumentException.class)
    public void argumentsInvalidOrder() throws Exception {
        CommandLine.parse("multi-argument 20 one");
    }

    /**
     * @version 2012/02/29 19:05:59
     */
    private static class MultiArgument implements Command<Object> {

        @Argument
        private String name;

        @Argument
        private int id;

        /**
         * {@inheritDoc}
         */
        @Override
        public void execute(Object context) {
        }
    }
}

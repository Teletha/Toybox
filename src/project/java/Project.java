/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
public class Project extends bee.api.Project {

    {
        product("com.github.teletha", "toybox", "1.0");

        require("com.github.teletha", "sinobu", "1.0");
        require("com.github.teletha", "viewtify", "1.0");
        require("com.github.teletha", "psychopath", "0.6");
        require("com.github.teletha", "antibug", "0.6").atTest();
        require("net.java.dev.jna", "platform", "3.5.1");
    }
}

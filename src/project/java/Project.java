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
        require("org.eclipse.swt", "org.eclipse.swt.win32.win32.x86_64", "4.7M3");
        require("com.ibm.icu", "icu4j", "4.8.1.1");
        require("net.java.dev.jna", "platform", "3.5.1");
        require("com.github.teletha", "antibug", "0.3");
        require("jnativehook", "jnativehook", "1.0");

        repository("http://maven-eclipse.github.io/dev-releases/maven");
    }
}

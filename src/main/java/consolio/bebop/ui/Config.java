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

import java.beans.Introspector;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import kiss.I;
import kiss.model.Model;
import kiss.model.Property;

/**
 * @version 2017/02/09 2:33:24
 */
public class Config {

    private static final ConcurrentHashMap cache = new ConcurrentHashMap();

    public static <C> C user(Class<C> config) {
        return of(config, I.locate(System.getProperty("user.home")).resolve(Config.class.getName()).resolve(config.getName() + ".txt"));
    }

    public static <C> C application(Class<C> config) {
        return of(config, I.locate("").toAbsolutePath().resolve("preferences").resolve(config.getName() + ".txt"));
    }

    public static <C> C of(Class<C> config, Path file) {
        if (Files.notExists(file)) {
            try {
                Files.createDirectories(file.getParent());
                Files.createFile(file);
            } catch (IOException e) {
                throw I.quiet(e);
            }
        }

        return (C) cache.computeIfAbsent(config, c -> I.make(config, (p, method, args) -> {
            String name = method.getName();
            Class returnType = method.getReturnType();
            boolean setter = returnType == void.class;
            String prefix = setter ? "set" : returnType == boolean.class ? "is" : "get";
            name = Introspector.decapitalize(name.startsWith(prefix) ? name.substring(prefix.length()) : name);

            Model<?> model = Model.of(method.getDeclaringClass());
            Property property = model.property(name);

            if (property == null) {
                return !method.isDefault() ? null : invokeDefaultMethod(p, method, args);
            } else {
                Properties properties = new Properties();
                properties.load(Files.newBufferedReader(file, StandardCharsets.UTF_8));

                if (setter) {
                    properties.setProperty(name, I.transform(args[0], String.class));
                    properties.store(Files.newBufferedWriter(file, StandardCharsets.UTF_8), "local configuration.");
                    return null;
                } else {
                    String value = properties.getProperty(name);

                    if (value == null) {
                        if (method.isDefault()) {
                            return invokeDefaultMethod(p, method, args);
                        }
                    }
                    return I.transform(value, returnType);
                }
            }
        }));
    }

    /**
     * <p>
     * Helper method to invoke default method.
     * </p>
     * 
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    private static Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
        constructor.setAccessible(true);

        Class<?> declaringClass = method.getDeclaringClass();
        int allModes = MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE;
        return constructor.newInstance(declaringClass, allModes)
                .unreflectSpecial(method, declaringClass)
                .bindTo(proxy)
                .invokeWithArguments(args);
    }
}

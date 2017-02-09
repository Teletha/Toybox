/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package consolio;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Widget;

import kiss.I;

/**
 * @version 2017/02/09 9:29:51
 */
public class Debug {

    public static void event(Widget widget) {
        try {
            for (Field field : SWT.class.getFields()) {
                if (field.getType() == int.class && isEvent(field.getName())) {
                    int type = field.getInt(null);
                    widget.addListener(type, e -> {
                        System.out.println("Event SWT." + field.getName());
                    });
                }
            }
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Check upper name.
     * </p>
     * 
     * @param name
     * @return
     */
    private static boolean isEvent(String name) {
        if (Character.isLowerCase(name.charAt(0))) {
            return false;
        }

        if (Character.isUpperCase(name.charAt(1))) {
            return false;
        }

        if (Character.isDigit(name.charAt(1))) {
            return false;
        }
        return true;
    }
}

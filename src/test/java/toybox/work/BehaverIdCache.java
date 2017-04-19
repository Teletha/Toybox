/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox.work;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import filer.Filer;
import kiss.I;

/**
 * @version 2012/09/11 16:43:48
 */
public class BehaverIdCache {

    /** The actual mapping. */
    private Map<Integer, String> mapping = new HashMap();

    /**
     * Get the mapping property of this {@link BehaverIdCache}.
     * 
     * @return The mapping property.
     */
    public Map<Integer, String> getMapping() {
        return mapping;
    }

    /**
     * Set the mapping property of this {@link BehaverIdCache}.
     * 
     * @param mapping The mapping value to set.
     */
    public void setMapping(Map<Integer, String> mapping) {
        this.mapping = mapping;
    }

    /**
     * <p>
     * Query id from cache.
     * </p>
     * 
     * @param behaver
     */
    static String query(Behaver behaver) {
        if (behaver == null) {
            return "";
        }

        String id = behaver.getId();

        if (id.length() == 0) {
            id = I.make(BehaverIdCache.class).mapping.get(hash(behaver.getName(), behaver.getReading(), behaver.getAddress()));
        }
        return id;
    }

    /**
     * <p>
     * Save id to cache.
     * </p>
     * 
     * @param behaver
     */
    static void save(Behaver behaver) {
        if (behaver != null) {
            I.make(BehaverIdCache.class).mapping.put(hash(behaver.getName(), behaver.getReading(), behaver.getAddress()), behaver.getId());
        }
    }

    /**
     * <p>
     * Build behaver id mapping file.
     * </p>
     */
    public static void main(String[] args) throws Exception {
        BehaverIdCache map = I.make(BehaverIdCache.class);
        Map<Integer, String> mapping = new HashMap();
        Path path = Filer.locate("\\\\Adm01018\\本坊-参詣課\\FTP受信\\住所録データ（任意会講分／参詣）.csv");

        for (String line : Files.readAllLines(path, Charset.forName("windows-31j"))) {
            String[] values = line.split(",");
            mapping.put(hash(values[3], values[2], values[13]), trim(values[1]));
        }
        map.setMapping(mapping);
    }

    /**
     * <p>
     * Compute hash.
     * </p>
     * 
     * @param name
     * @param reading
     * @param address
     * @return
     */
    private static int hash(String name, String reading, String address) {
        return Objects.hash(trim(name), trim(reading), trim(address));
    }

    /**
     * Helper method.
     * 
     * @param value
     * @return
     */
    private static String trim(String value) {
        if (value == null) {
            return "";
        }

        value = value.trim();

        if (value.length() == 0) {
            return "";
        }

        if (value.charAt(0) == '"') {
            value = value.substring(1);
        }

        int size = value.length();

        if (1 <= size && value.charAt(size - 1) == '"') {
            value = value.substring(0, size - 1);
        }
        return value.trim();
    }

}

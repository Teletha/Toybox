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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import kiss.I;

/**
 * @version 2012/08/21 14:38:14
 */
public class Behaver {

    private static final Map<String, String> specialCharacters = new HashMap();

    private static final Map<String, String> numbers = new HashMap();

    private static final Map<String, String> zenkaku = new HashMap();

    static {
        specialCharacters.put("髙", "");

        numbers.put("１", "1");
        numbers.put("２", "2");
        numbers.put("３", "3");
        numbers.put("４", "4");
        numbers.put("５", "5");
        numbers.put("６", "6");
        numbers.put("７", "7");
        numbers.put("８", "8");
        numbers.put("９", "9");
        numbers.put("０", "0");
        numbers.put("－", "-");

        zenkaku.put("1", "１");
        zenkaku.put("2", "２");
        zenkaku.put("3", "３");
        zenkaku.put("4", "４");
        zenkaku.put("5", "５");
        zenkaku.put("6", "６");
        zenkaku.put("7", "７");
        zenkaku.put("8", "８");
        zenkaku.put("9", "９");
        zenkaku.put("0", "０");
        zenkaku.put("A", "Ａ");
        zenkaku.put("B", "Ｂ");
        zenkaku.put("C", "Ｃ");
        zenkaku.put("D", "Ｄ");
        zenkaku.put("E", "Ｅ");
        zenkaku.put("F", "Ｆ");
        zenkaku.put("G", "Ｇ");
        zenkaku.put("H", "Ｈ");
        zenkaku.put("I", "Ｉ");
    }

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy'/'MM'/'dd");

    // private static final Transliterator half = Transliterator.getInstance("Fullwidth-Halfwidth");

    // private static final Transliterator full = Transliterator.getInstance("Halfwidth-Fullwidth");

    /** The name. */
    private String name;

    /** The reading of name. */
    private String reading;

    /** The post code. */
    private String postCode;

    /** The address. */
    private String address;

    /** The phone number. */
    private String phone;

    /** The id. */
    private String id = "";

    /** The some date. */
    private Date date;

    /** The price rate. */
    private int itemPrice = 0;

    /** The item number. */
    private int itemNumber = 1;

    /** The flag for management. */
    private boolean mark = false;

    /**
     * Get the name property of this {@link Behaver}.
     * 
     * @return The name property.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name property of this {@link Behaver}.
     * 
     * @param name The name value to set.
     */
    protected void setName(String name) {
        this.name = translate(name.replace("\\s+", "　").trim(), specialCharacters);
    }

    /**
     * Get the reading property of this {@link Behaver}.
     * 
     * @return The reading property.
     */
    public String getReading() {
        return reading;
    }

    /**
     * Set the reading property of this {@link Behaver}.
     * 
     * @param reading The reading value to set.
     */
    protected void setReading(String reading) {
        this.reading = reading.replaceAll("\\s+", " ").trim();
    }

    /**
     * Get the postCode property of this {@link Behaver}.
     * 
     * @return The postCode property.
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * Set the postCode property of this {@link Behaver}.
     * 
     * @param postCode The postCode value to set.
     */
    protected void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    /**
     * Get the address property of this {@link Behaver}.
     * 
     * @return The address property.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the address property of this {@link Behaver}.
     * 
     * @param address The address value to set.
     */
    protected void setAddress(String address) {
        this.address = translate(address, zenkaku);
    }

    /**
     * Get the phone property of this {@link Behaver}.
     * 
     * @return The phone property.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Set the phone property of this {@link Behaver}.
     * 
     * @param phone The phone value to set.
     */
    protected void setPhone(String phone) {
        this.phone = translate(phone, numbers);
    }

    /**
     * Get the id property of this {@link Behaver}.
     * 
     * @return The id property.
     */
    public String getId() {
        return id;
    }

    /**
     * Set the id property of this {@link Behaver}.
     * 
     * @param id The id value to set.
     */
    protected void setId(String id) {
        if (id == null) {
            id = "";
        }

        // normalize
        id = id.replaceAll("\\s", "");

        if (id.length() == 0) {
            // query id from cache
            id = BehaverIdCache.query(this);

            this.id = id;
        } else {
            this.id = id;

            // cache id
            BehaverIdCache.save(this);
        }
    }

    /**
     * Get the date property of this {@link Behaver}.
     * 
     * @return The date property.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the date property of this {@link Behaver}.
     * 
     * @param date The date value to set.
     */
    protected void setDate(String date) {
        try {
            this.date = formatter.parse(date);
        } catch (ParseException e) {
            throw I.quiet(e);
        }
        // String[] values = date.split("/");
        // StringBuilder builder = new StringBuilder();
        // builder.append(values[0]);
        //
        // if (values[1].length() == 1) {
        // builder.append("0").append(values[1]);
        // } else {
        // builder.append(values[1]);
        // }
        //
        // if (values[2].length() == 1) {
        // builder.append("0").append(values[2]);
        // } else {
        // builder.append(values[2]);
        // }
        // this.date = builder.toString();
    }

    /**
     * Set the date property of this {@link Behaver}.
     * 
     * @param date The date value to set.
     */
    protected void setDate(Date date) {
        this.date = date;
    }

    /**
     * Get the itemPrice property of this {@link Behaver}.
     * 
     * @return The itemPrice property.
     */
    public int getItemPrice() {
        return itemPrice;
    }

    /**
     * Set the itemPrice property of this {@link Behaver}.
     * 
     * @param itemPrice The itemPrice value to set.
     */
    protected void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    /**
     * Get the itemNumber property of this {@link Behaver}.
     * 
     * @return The itemNumber property.
     */
    public int getItemNumber() {
        return itemNumber;
    }

    /**
     * Set the itemNumber property of this {@link Behaver}.
     * 
     * @param itemNumber The itemNumber value to set.
     */
    protected void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }

    /**
     * Get the mark property of this {@link Behaver}.
     * 
     * @return The mark property.
     */
    public boolean isMarked() {
        return mark;
    }

    /**
     * Set the mark property of this {@link Behaver}.
     * 
     * @param mark The mark value to set.
     */
    protected void setMarked(boolean mark) {
        this.mark = mark;
    }

    /**
     * Check id.
     * 
     * @return
     */
    public boolean hasId() {
        return id != null && id.length() != 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Behaver) {
            Behaver behaver = (Behaver) obj;

            if (id != null) {
                return id.equals(behaver.id);
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Behaver [name=" + name + ", reading=" + reading + ", postCode=" + postCode + ", address=" + address + ", phone=" + phone + ", id=" + id + ", date=" + date + ", itemPrice=" + itemPrice + ", itemNumber=" + itemNumber + "]";
    }

    /**
     * @param value
     * @param table
     * @return
     */
    private String translate(String value, Map<String, String> table) {
        for (Entry<String, String> entry : table.entrySet()) {
            value = value.replaceAll(entry.getKey(), entry.getValue());
        }
        return value;
    }
}

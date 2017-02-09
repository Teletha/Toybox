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

/**
 * @version 2012/10/01 16:26:31
 */
public interface BehaverFilter {

    /**
     * <p>
     * Filtering.
     * </p>
     * 
     * @param behaver
     * @return
     */
    boolean accept(Behaver behaver);

    /**
     * Built-in filter.
     */
    public static final BehaverFilter ByMark = new BehaverFilter() {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean accept(Behaver behaver) {
            return behaver.isMarked();
        }
    };
}

/*
 * Copyright (C) 2011 Nameless Production Committee.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package toybox.print;

import javax.print.attribute.standard.MediaSize;

/**
 * @version 2011/11/08 9:21:02
 */
public abstract class PrintPaper {

    /** The paper width. (unit: mm) */
    double width = 0;

    /** The paper height. (unit: mm) */
    double height = 0;

    /**
     * <p>
     * Create printing paper.
     * </p>
     * 
     * @param width The paper width. (unit: mm)
     * @param height The paper height. (unit: mm)
     */
    protected PrintPaper(double width, double height) {
        this.width = width;
        this.height = height;
    }

    /**
     * <p>
     * Create printing paper.
     * </p>
     * 
     * @param width The paper width. (unit: mm)
     * @param height The paper height. (unit: mm)
     */
    protected PrintPaper(MediaSize size) {
        this.width = size.getX(MediaSize.MM);
        this.height = size.getY(MediaSize.MM);
    }

    /**
     * <p>
     * Draw contents actually.
     * </p>
     */
    protected abstract void draw(Canvas canvas);

    /**
     * <p>
     * Create new style info and set position.
     * </p>
     * 
     * @param x X direction position. (unit : mm)
     * @param y Y direction position. (unit : mm)
     * @return Chainable API.
     */
    protected PrintStyle position(double x, double y) {
        return new PrintStyle(x, y, this);
    }
}

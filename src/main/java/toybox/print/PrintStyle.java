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

import java.awt.Font;

/**
 * @version 2011/11/11 11:19:08
 */
public class PrintStyle {

    /** The translation ratio. */
    static final double pixel = 72.0 / 25.4;

    /** The translation ratio. */
    static final double mm = 25.4 / 72.0;

    /** The print target. */
    private PrintPaper paper;

    /** unit : pixel */
    double x;

    /** unit : pixel */
    double y;

    /** unit : pixel */
    double width;

    /** unit : pixel */
    double height;

    Font font = new Font("AR楷書体M", Font.PLAIN, 16);

    /** property */
    HorizontalAlign align = HorizontalAlign.Left;

    /**
     * Enclose constructor.
     */
    PrintStyle(double x, double y, PrintPaper paper) {
        this.x = x * pixel;
        this.y = y * pixel;
        this.paper = paper;
        this.width = paper.width * pixel - this.x;
    }

    /**
     * <p>
     * Set box width.
     * </p>
     * 
     * @param width X A width of box. (unit : mm)
     * @return Chainable API.
     */
    public final PrintStyle width(double width) {
        this.width = width * pixel;

        // Chainable API
        return this;
    }

    /**
     * <p>
     * Set box height.
     * </p>
     * 
     * @param height X A height of box. (unit : mm)
     * @return Chainable API.
     */
    public final PrintStyle height(double height) {
        this.height = height * pixel;

        // Chainable API
        return this;
    }

    /**
     * <p>
     * Set text align center.
     * </p>
     * 
     * @return Chainable API.
     */
    public final PrintStyle alignCenter() {
        this.align = HorizontalAlign.Center;

        // Chainable API
        return this;
    }

    /**
     * <p>
     * Set text align center.
     * </p>
     * 
     * @return Chainable API.
     */
    public final PrintStyle alignRight() {
        this.align = HorizontalAlign.Right;

        // Chainable API
        return this;
    }

    public final PrintStyle font(String name, int size) {
        this.font = new Font(name, Font.PLAIN, size);

        // Chainable API
        return this;
    }

    /**
     * @version 2011/11/12 12:46:03
     */
    static enum HorizontalAlign {
        Center, Left, Right;
    }
}

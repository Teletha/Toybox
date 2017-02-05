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

import static toybox.print.PrintStyle.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * @version 2011/11/08 13:22:19
 */
public class Canvas {

    /** The actual canvas. */
    private final Graphics graphic;

    /**
     * @param graphic
     */
    Canvas(Graphics graphic) {
        this.graphic = graphic;

        graphic.setColor(Color.BLACK);
        graphic.setFont(new Font("MeiryoKe_Gothic", Font.PLAIN, 16));
    }

    /**
     * <p>
     * Draw rectangle.
     * </p>
     * 
     * @param width
     * @param height
     */
    public void rect(int x, int y, int width, int height) {
        graphic.drawRect((int) Math.round(x * pixel), (int) Math.round(y * pixel), (int) Math.round(width * pixel), (int) Math.round(height * pixel));
    }

    /**
     * <p>
     * Draw text.
     * </p>
     * 
     * @param text A text to draw.
     */
    public void text(char text, PrintStyle style) {
        text(String.valueOf(text), style.width(graphic.getFontMetrics().charWidth(text)));
    }

    /**
     * <p>
     * Draw wrappable text.
     * </p>
     * 
     * @param text A text to draw.
     */
    public void text(CharSequence text, PrintStyle style) {
        int start = 0;
        int current = 0;
        int end = text.length();

        graphic.setFont(style.font);
        FontMetrics metrics = graphic.getFontMetrics();

        double lineY = style.y;
        double lineWidth = 0;
        double lineHeight = metrics.getHeight() * 1.4;

        while (current < end) {
            char c = text.charAt(current++);
            double charWidth = metrics.charWidth(c);

            if (c == '\r' || c == '\n' || style.width <= lineWidth + charWidth) {
                System.out.println((c == '\r') + "   " + (c == '\n') + "   " + (style.width <= lineWidth + charWidth) + "   " + style.width + "   " + lineWidth + "   " + charWidth);
                // create new line
                text(text.subSequence(start, current - 1), style.x, lineY, lineWidth, style);
                System.out.println(text.subSequence(start, current - 1));
                if (c == '\r' && current < end && text.charAt(current) == '\n') {
                    System.out.println("skip");
                    current++; // skip crlf
                }

                start = current - 1;
                lineWidth = charWidth;
                lineY += lineHeight;
            } else {
                lineWidth += charWidth;
            }
        }

        // draw last line
        text(text.subSequence(start, current), style.x, lineY, lineWidth, style);

        // set box height
        style.height = lineY + metrics.getHeight() - style.y;
    }

    /**
     * <p>
     * Draw text.
     * </p>
     * 
     * @param text A text to draw.
     * @param x A unit is pixel.
     * @param y A unit is pixel.
     * @param style
     */
    private void text(CharSequence text, double x, double y, double lineWidth, PrintStyle style) {
        switch (style.align) {
        case Center:
            x = x + (style.width - lineWidth) / 2;
            break;

        case Right:
            x = x + style.width - lineWidth;
            break;
        }

        int increase = 0;
        FontMetrics metrics = graphic.getFontMetrics();

        for (int i = 0; i < text.length(); i++) {
            graphic.drawString(String.valueOf(text.charAt(i)), (int) x + increase, (int) y + metrics.getMaxAscent());
            increase += metrics.charWidth(text.charAt(i));
        }
    }

    public void name(CharSequence text, PrintStyle style) {
        graphic.setColor(new Color(100, 100, 100));
        graphic.drawRect((int) style.x, (int) style.y, (int) style.width, (int) style.height);
    }

    /**
     * <p>
     * Draw wrappable text.
     * </p>
     * 
     * @param text A text to draw.
     */
    public void vtext(CharSequence text, PrintStyle style) {
        int start = 0;
        int current = 0;
        int end = text.length();

        graphic.setFont(style.font);
        FontMetrics metrics = graphic.getFontMetrics();

        double lineX = style.x + style.width - metrics.getMaxAdvance();
        double lineWidth = metrics.getMaxAdvance();
        double lineHeight = 0;
        double charHeight = metrics.getHeight() - 4;

        while (current < end) {
            char c = text.charAt(current++);

            if (c == '\r' || c == '\n' || style.height <= lineHeight + charHeight) {
                // create new line
                vtext(text.subSequence(start, current - 1), lineX, style.y, lineWidth, lineHeight, charHeight, style);

                if (c == '\r' && current < end && text.charAt(current) == '\n') {
                    current++; // skip crlf
                }

                start = current - 1;
                lineHeight = charHeight;
                lineX -= lineWidth;
            } else {
                lineHeight += charHeight;
            }
        }

        // draw last line
        vtext(text.subSequence(start, current), lineX, style.y, lineWidth, lineHeight, charHeight, style);

        // set box height
        style.width = style.y + metrics.getHeight() - style.y;
    }

    private void vtext(CharSequence text, double x, double y, double lineWidth, double lineHeight, double charHeight, PrintStyle style) {
        switch (style.align) {
        case Center:
            y = y + (style.height - lineHeight) / 2;
            break;

        case Right:
            y = y + style.height - lineHeight;
            break;
        }

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            graphic.drawString(String.valueOf(c), (int) Math.round(x), (int) Math.round(y + charHeight * i) - graphic.getFontMetrics()
                    .getMaxAscent());
        }
    }
}

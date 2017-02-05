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
package toybox.print.internal;

import javax.print.attribute.standard.MediaSize;

import toybox.print.Canvas;
import toybox.print.PrintPaper;

/**
 * @version 2011/11/12 15:31:53
 */
public class GratitudeLetter extends PrintPaper {

    /**
     * @param size
     */
    public GratitudeLetter() {
        super(MediaSize.JIS.B5);
    }

    /**
     * @see toybox.print.PrintPaper#draw(toybox.print.Canvas)
     */
    @Override
    protected void draw(Canvas canvas) {
        StringBuilder builder = new StringBuilder();
        builder.append("　謹啓　貴家益々御清祥のことと存じお慶び申しあげます\r\n\r\n");
        builder.append("　さて　今般当山伽藍回廊　永代奉安万灯灯籠のお申し込みを頂き　有難く厚く御礼申し上げます\r\n");
        builder.append("　つきましては　お申し込みの万灯灯籠は　早速御指示通り銘記の上　平成二十三年十一月十六日に回廊内の別紙番号の場所に奉納させて頂きます");
        canvas.vtext(builder, position(16, 30).width(150).height(90));
        canvas.text("御礼の御挨拶を申しあげます\r\n合掌", position(16, 110).width(150).alignRight());
        canvas.text("　平成二十三年十一月", position(16, 130));
        canvas.text("総本山四天王寺", position(16, 150).width(150).alignRight());
    }
}

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
package toybox;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * @version 2011/10/30 15:53:36
 */
public class PrintTester implements Printable {

    private Paint pnt = new GradientPaint(100f, 100f, Color.red, 136f, 100f, Color.green, true);

    private int nCount = 0;

    public static void main(String[] args) {

        PrinterJob job = PrinterJob.getPrinterJob();

        // インスタンス作成
        job.setPrintable(new PrintTester());
        // 印刷ダイアログ表示
        if (job.printDialog()) {
            try {
                job.print();
            } catch (Exception e) {
            }
        }
    }

    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {

        if (pageIndex >= 5) {
            return Printable.NO_SUCH_PAGE;
        }

        int ret;
        while (true) {
            ret = partsPrint(g, pageIndex);
            if (ret == -1) {
                break;
            }
        }

        return Printable.PAGE_EXISTS;
    }

    public int partsPrint(Graphics g, int pageIndex) throws PrinterException {

        nCount++;
        if (nCount > 10) {
            nCount = 0;
            return -1;
        }
        if (pageIndex == 0) {
            g.setColor(Color.DARK_GRAY);
            g.drawString("日本語表示テスト : Page " + (pageIndex + 1), 100, 100 + 20 * nCount);
        } else {
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(pnt);
            g2.drawString("日本語表示テスト : Page " + (pageIndex + 1), 100f, 100f + 20 * nCount);
        }

        return 0;
    }
}

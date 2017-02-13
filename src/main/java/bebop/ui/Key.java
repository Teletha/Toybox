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
package bebop.ui;

import static org.eclipse.swt.SWT.*;
import static org.eclipse.swt.internal.win32.OS.*;

import org.eclipse.swt.SWT;

/**
 * @version 2017/02/11 23:09:26
 */
public enum Key {

    /** Virtual Key Code */
    N1('1', '1'),

    /** Virtual Key Code */
    N2('2', '2'),

    /** Virtual Key Code */
    N3('3', '3'),

    /** Virtual Key Code */
    N4('4', '4'),

    /** Virtual Key Code */
    N5('5', '5'),

    /** Virtual Key Code */
    N6('6', '6'),

    /** Virtual Key Code */
    N7('7', '7'),

    /** Virtual Key Code */
    N8('8', '8'),

    /** Vitual Key Code */
    N9('9', '9'),

    /** Virtual Key Code */
    N0('0', '0'),

    /** Virtual Key Code */
    A('a'),

    /** Virtual Key Code */
    B('b'),

    /** Virtual Key Code */
    C('c'),

    /** Virtual Key Code */
    D('d'),

    /** Virtual Key Code */
    E('e'),

    /** Virtual Key Code */
    F('f'),

    /** Virtual Key Code */
    G('g'),

    /** Virtual Key Code */
    H('h'),

    /** Virtual Key Code */
    I('i'),

    /** Virtual Key Code */
    J('j'),

    /** Virtual Key Code */
    K('k'),

    /** Virtual Key Code */
    L('l'),

    /** Virtual Key Code */
    M('m'),

    /** Virtual Key Code */
    N('n'),

    /** Virtual Key Code */
    O('o'),

    /** Virtual Key Code */
    P('p'),

    /** Virtual Key Code */
    Q('q'),

    /** Virtual Key Code */
    R('r'),

    /** Virtual Key Code */
    S('s'),

    /** Virtual Key Code */
    T('t'),

    /** Virtual Key Code */
    U('u'),

    /** Virtual Key Code */
    V('v'),

    /** Virtual Key Code */
    W('w'),

    /** Virtual Key Code */
    X('x'),

    /** Virtual Key Code */
    Y('y'),

    /** Virtual Key Code */
    Z('z'),

    /** Virtual Key Code */
    Up(ARROW_UP, VK_UP, false, true),

    /** Virtual Key Code */
    Down(ARROW_DOWN, VK_DOWN, false, true),

    /** Virtual Key Code */
    Right(ARROW_RIGHT, VK_RIGHT, false, true),

    /** Virtual Key Code */
    Left(ARROW_LEFT, VK_LEFT, false, true),

    /** Virtual Key Code */
    Space(SPACE, VK_SPACE),

    /** Virtual Key Code */
    Backspace(BS, VK_BACK),

    /** Virtual Key Code */
    Enter(CR, VK_RETURN),

    /** Virtual Key Code */
    EnterInTenKey(CR, VK_SEPARATOR),

    /** Virtual Key Code */
    Delete(DEL, VK_DELETE),

    /** Virtual Key Code */
    Escape(ESC, VK_ESCAPE),

    /** Virtual Key Code */
    Insert(INSERT, VK_INSERT),

    /** Virtual Key Code */
    Tab(TAB, VK_TAB),

    /** Virtual Key Code */
    Home(HOME, VK_HOME),

    /** Virtual Key Code */
    End(END, VK_END),

    /** Virtual Key Code */
    PageUp(PAGE_UP, VK_PRIOR),

    /** Virtual Key Code */
    PageDown(PAGE_DOWN, VK_NEXT),

    /** Virtual Key Code */
    ControlRight(CTRL, VK_RCONTROL, false, true),

    /** Virtual Key Code */
    ControlLeft(CTRL, VK_LCONTROL, false, true),

    /** Virtual Key Code */
    ShiftRight(SHIFT, VK_RSHIFT, false, false),

    /** Virtual Key Code */
    ShiftLeft(SHIFT, VK_LSHIFT, false, false),

    /** Virtual Key Code */
    AltRight(ALT, VK_RMENU, true),

    /** Virtual Key Code */
    AltLeft(ALT, VK_LMENU, true),

    /** Virtual Key Code */
    F1(SWT.F1, VK_F1, true),

    /** Virtual Key Code */
    F2(SWT.F2, VK_F2, true),

    /** Virtual Key Code */
    F3(SWT.F3, VK_F3, true),

    /** Virtual Key Code */
    F4(SWT.F4, VK_F4, true),

    /** Virtual Key Code */
    F5(SWT.F5, VK_F5, true),

    /** Virtual Key Code */
    F6(SWT.F6, VK_F6, true),

    /** Virtual Key Code */
    F7(SWT.F7, VK_F7, true),

    /** Virtual Key Code */
    F8(SWT.F8, VK_F8, true),

    /** Virtual Key Code */
    F9(SWT.F9, VK_F9, true),

    /** Virtual Key Code */
    F10(SWT.F10, VK_F10, true),

    /** Virtual Key Code */
    F11(SWT.F11, VK_F11, true),

    /** Virtual Key Code */
    F12(SWT.F12, VK_F12, true);

    /** The SWT key code. */
    public final int code;

    /** The native virtual key code. */
    public final short nativeCode;

    /** Is this key is system related? */
    final boolean system;

    /** Is this key is extended key? */
    public final boolean extended;

    /**
     * <p>
     * Native key.
     * </p>
     * 
     * @param code
     */
    private Key(int code) {
        this(code, code - 32, false);
    }

    /**
     * <p>
     * Native key.
     * </p>
     * 
     * @param nativeCode
     */
    private Key(int code, int nativeCode) {
        this(code, nativeCode, false);
    }

    /**
     * <p>
     * Native key.
     * </p>
     * 
     * @param nativeCode
     */
    private Key(int code, int nativeCode, boolean system) {
        this(code, nativeCode, system, false);
    }

    /**
     * <p>
     * Native key.
     * </p>
     * 
     * @param nativeCode
     */
    private Key(int code, int nativeCode, boolean system, boolean extended) {
        this.code = code;
        this.nativeCode = (short) nativeCode;
        this.system = system;
        this.extended = extended;
    }

    /**
     * @version 2017/02/11 23:25:49
     */
    public static enum With {
        Alt(SWT.ALT), Ctrl(SWT.CTRL), Shift(SWT.SHIFT);

        public final int mask;

        /**
         * Hide Constructor.
         * 
         * @param mask
         */
        private With(int mask) {
            this.mask = mask;
        }
    }
}

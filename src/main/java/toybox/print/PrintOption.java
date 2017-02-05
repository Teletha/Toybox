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

/**
 * @version 2011/11/11 15:12:19
 */
public final class PrintOption {

    /** The printer X direction offset. (unit: mm) */
    public double offsetX = 0;

    /** The printer Y direction offset. (unit: mm) */
    public double offsetY = 0;

    /** The rotation degree. */
    public int degree = 0;

    /** The number of copies. */
    public int copy = 1;
}

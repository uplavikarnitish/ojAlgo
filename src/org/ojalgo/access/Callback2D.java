/*
 * Copyright 1997-2016 Optimatika (www.optimatika.se)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.ojalgo.access;

import org.ojalgo.function.FunctionUtils;

@FunctionalInterface
public interface Callback2D<N extends Number>
{

    static <N extends Number> void onMatching(final Access2D<N> from, final Callback2D<N> through, final Mutate2D to)
    {
        final long tmpRows = FunctionUtils.min(from.countRows(), to.countRows());
        final long tmpCols = FunctionUtils.min(from.countColumns(), to.countColumns());
        for (long j = 0L; j < tmpCols; j++)
        {
            for (long i = 0L; i < tmpRows; i++)
            {
                through.call(from, i, j, to);
            }
        }
    }

    /**
     * @param r   Reader/Accessor/Getter
     * @param row Row
     * @param col Column
     * @param w   Writer/Mutator/Setter
     */
    void call(Access2D<N> r, long row, long col, Mutate2D w);

}

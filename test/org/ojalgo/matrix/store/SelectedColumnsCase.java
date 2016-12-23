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
package org.ojalgo.matrix.store;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.random.Uniform;

public class SelectedColumnsCase extends NonPhysicalTest {

    public SelectedColumnsCase() {
        super();
    }

    public SelectedColumnsCase(final String arg0) {
        super(arg0);
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        final int tmpRowDim = Uniform.randomInteger(1, 9);
        final int tmpColDim = Uniform.randomInteger(1, 9);

        final BasicMatrix tmpBase = NonPhysicalTest.makeRandomMatrix(tmpRowDim, tmpColDim);

        final int[] tmpCols = new int[Uniform.randomInteger(1, tmpColDim)];
        for (int i = 0; i < tmpCols.length; i++) {
            tmpCols[i] = Uniform.randomInteger(tmpColDim);
        }

        myBigStore = new ColumnsStore<>(BigDenseStore.FACTORY.copy(tmpBase), tmpCols);
        myComplexStore = new ColumnsStore<>(ComplexDenseStore.FACTORY.copy(tmpBase), tmpCols);
        myPrimitiveStore = new ColumnsStore<>(PrimitiveDenseStore.FACTORY.copy(tmpBase), tmpCols);
    }

}

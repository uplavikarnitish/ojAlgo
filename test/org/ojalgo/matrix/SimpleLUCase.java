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
package org.ojalgo.matrix;

import java.math.BigDecimal;

import org.ojalgo.TestUtils;
import org.ojalgo.matrix.decomposition.LU;
import org.ojalgo.type.context.NumberContext;

/**
 * Gilbert Strang, Linear Algebra and its Applications III, Problem 3.6.15
 *
 * @author apete
 */
public class SimpleLUCase extends BasicMatrixTest {

    public static BigMatrix getMtrxL() {
        final BigMatrix tmpMtrx = BigMatrix.FACTORY.rows(new double[][] { { 1.0, 0.0 }, { 0.0, 1.0 }, { 1.0, 1.0 } });
        return tmpMtrx.enforce(DEFINITION);
    }

    public static BigMatrix getMtrxU() {
        final BigMatrix tmpMtrx = BigMatrix.FACTORY.rows(new double[][] { { 1.0, -1.0, 0.0 }, { 0.0, 1.0, -1.0 } });
        return tmpMtrx.enforce(DEFINITION);
    }

    public static BigMatrix getOrginal() {
        final BigMatrix tmpMtrx = BigMatrix.FACTORY.rows(new double[][] { { 1.0, -1.0, 0.0 }, { 0.0, 1.0, -1.0 }, { 1.0, 0.0, -1.0 } });
        return tmpMtrx.enforce(DEFINITION);
    }

    public SimpleLUCase() {
        super();
    }

    public SimpleLUCase(final String arg0) {
        super(arg0);
    }

    @Override
    public void testData() {

        myExpMtrx = SimpleLUCase.getOrginal();
        myActMtrx = SimpleLUCase.getMtrxL().multiply(SimpleLUCase.getMtrxU());

        TestUtils.assertEquals(myExpMtrx, myActMtrx, EVALUATION);
    }

    @Override
    public void testProblem() {

        // PLDU

        final LU<BigDecimal> tmpLU = LU.BIG.make();
        tmpLU.decompose(SimpleLUCase.getOrginal().toBigStore());

        tmpLU.equals(SimpleLUCase.getOrginal().toBigStore(), EVALUATION);
    }

    @Override
    protected void setUp() throws Exception {

        DEFINITION = new NumberContext(7, 1);
        EVALUATION = new NumberContext(7, 9);

        myBigAA = SimpleLUCase.getMtrxL();
        myBigAX = SimpleLUCase.getMtrxU();
        myBigAB = SimpleLUCase.getOrginal();

        myBigI = BasicMatrixTest.getIdentity(myBigAA.countRows(), myBigAA.countColumns(), DEFINITION);
        myBigSafe = BasicMatrixTest.getSafe(myBigAA.countRows(), myBigAA.countColumns(), DEFINITION);

        super.setUp();
    }

}

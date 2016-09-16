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
package org.ojalgo.matrix.decomposition;

import static org.ojalgo.constant.PrimitiveMath.*;

import java.math.BigDecimal;

import org.ojalgo.array.Array1D;
import org.ojalgo.array.PrimitiveArray;
import org.ojalgo.function.PrimitiveFunction;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.ElementsSupplier;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.context.NumberContext;

/**
 * Orginalet, sedan ett tag Based on SVDnew2, but with transposing so that calculations are always made on a
 * matrix that "isAspectRationNormal". Based on SVDnew5, but with Rotation replaced by the new alternative.
 *
 * @author apete
 */
abstract class SVDnew32<N extends Number & Comparable<N>> extends SingularValueDecomposition<N>
{

    static final class Big extends SVDnew32<BigDecimal>
    {

        Big()
        {
            super(BigDenseStore.FACTORY, new BidiagonalDecomposition.Big());
        }

    }

    static final class Complex extends SVDnew32<ComplexNumber>
    {

        Complex()
        {
            super(ComplexDenseStore.FACTORY, new BidiagonalDecomposition.Complex());
        }

    }

    static final class Primitive extends SVDnew32<Double>
    {

        Primitive()
        {
            super(PrimitiveDenseStore.FACTORY, new BidiagonalDecomposition.Primitive());
        }

    }

    /**
     * ≈ 1.6E-291
     */
    @Deprecated
    static final double TINY = PrimitiveFunction.POW.invoke(2.0, -966.0);

    static void doCase1(final double[] s, final double[] e, final int p, final int k, final DecompositionStore<?> mtrxQ2)
    {

        double f = e[p - 2];
        e[p - 2] = ZERO;

        double t;
        double cs;
        double sn;

        for (int j = p - 2; j >= k; j--)
        {

            t = PrimitiveFunction.HYPOT.invoke(s[j], f);
            cs = s[j] / t;
            sn = f / t;

            s[j] = t;
            if (j != k)
            {
                f = -sn * e[j - 1];
                e[j - 1] = cs * e[j - 1];
            }

            if (mtrxQ2 != null)
            {
                mtrxQ2.rotateRight(p - 1, j, cs, sn);
            }
        }
    }

    static void doCase2(final double[] s, final double[] e, final int p, final int k, final DecompositionStore<?> mtrxQ1)
    {

        double f = e[k - 1];
        e[k - 1] = ZERO;

        double t;
        double cs;
        double sn;

        for (int j = k; j < p; j++)
        {

            t = PrimitiveFunction.HYPOT.invoke(s[j], f);
            cs = s[j] / t;
            sn = f / t;

            s[j] = t;
            f = -sn * e[j];
            e[j] = cs * e[j];

            if (mtrxQ1 != null)
            {
                mtrxQ1.rotateRight(k - 1, j, cs, sn);
            }
        }
    }

    static void doCase3(final double[] s, final double[] e, final int p, final int k, final DecompositionStore<?> mtrxQ1, final DecompositionStore<?> mtrxQ2)
    {

        final int indPm1 = p - 1;
        final int indPm2 = p - 2;

        // Calculate the shift.
        final double scale = PrimitiveFunction.MAX.invoke(PrimitiveFunction.MAX.invoke(PrimitiveFunction.MAX.invoke(PrimitiveFunction.MAX.invoke(PrimitiveFunction.ABS.invoke(s[indPm1]), PrimitiveFunction.ABS.invoke(s[indPm2])), PrimitiveFunction.ABS.invoke(e[indPm2])), PrimitiveFunction.ABS.invoke(s[k])),
                PrimitiveFunction.ABS.invoke(e[k]));

        final double sPm1 = s[indPm1] / scale;
        final double sPm2 = s[indPm2] / scale;
        final double ePm2 = e[indPm2] / scale;
        final double sK = s[k] / scale;
        final double eK = e[k] / scale;

        final double b = (((sPm2 + sPm1) * (sPm2 - sPm1)) + (ePm2 * ePm2)) / TWO;
        final double c = (sPm1 * ePm2) * (sPm1 * ePm2);
        double shift = ZERO;
        if ((c != ZERO) || (b != ZERO))
        {
            shift = PrimitiveFunction.SQRT.invoke((b * b) + c);
            if (b < ZERO)
            {
                shift = -shift;
            }
            shift = c / (b + shift);
        }

        double f = ((sK + sPm1) * (sK - sPm1)) + shift;
        double g = sK * eK;

        double t;
        double cs;
        double sn;

        // Chase zeros.
        for (int j = k; j < indPm1; j++)
        {

            t = PrimitiveFunction.HYPOT.invoke(f, g);
            cs = f / t;
            sn = g / t;
            if (j != k)
            {
                e[j - 1] = t;
            }
            f = (cs * s[j]) + (sn * e[j]);
            e[j] = (cs * e[j]) - (sn * s[j]);
            g = sn * s[j + 1];
            s[j + 1] = cs * s[j + 1];

            if (mtrxQ2 != null)
            {
                mtrxQ2.rotateRight(j + 1, j, cs, sn);

            }

            t = PrimitiveFunction.HYPOT.invoke(f, g);
            cs = f / t;
            sn = g / t;
            s[j] = t;
            f = (cs * e[j]) + (sn * s[j + 1]);
            s[j + 1] = (-sn * e[j]) + (cs * s[j + 1]);
            g = sn * e[j + 1];
            e[j + 1] = cs * e[j + 1];

            if (mtrxQ1 != null)
            {
                mtrxQ1.rotateRight(j + 1, j, cs, sn);

            }
        }

        e[indPm2] = f;
    }

    static void doCase4(final double[] s, final int k, final DecompositionStore<?> mtrxQ1, final DecompositionStore<?> mtrxQ2)
    {

        final int tmpDiagDim = s.length;

        // Make the singular values positive.
        final double tmpSk = s[k];
        if (tmpSk < ZERO)
        {
            s[k] = -tmpSk;

            if (mtrxQ2 != null)
            {
                //aQ2.modifyColumn(0, k, PrimitiveFunction.NEGATE);
                mtrxQ2.negateColumn(k);

            }
        } else if (tmpSk == ZERO)
        {
            s[k] = ZERO; // To get rid of negative zeros
        }

        // Order the singular values.
        int tmpK = k;

        while (tmpK < (tmpDiagDim - 1))
        {
            if (s[tmpK] >= s[tmpK + 1])
            {
                break;
            }
            final double t = s[tmpK];
            s[tmpK] = s[tmpK + 1];
            s[tmpK + 1] = t;

            if (mtrxQ1 != null)
            {
                mtrxQ1.exchangeColumns(tmpK + 1, tmpK);
            }
            if (mtrxQ2 != null)
            {
                mtrxQ2.exchangeColumns(tmpK + 1, tmpK);
            }

            tmpK++;
        }
    }

    static Array1D<Double> toDiagonal(final DiagonalAccess<?> bidiagonal, final DecompositionStore<?> mtrxQ1, final DecompositionStore<?> mtrxQ2)
    {

        final int tmpDiagDim = bidiagonal.mainDiagonal.size();

        final double[] s = bidiagonal.mainDiagonal.toRawCopy1D(); // s
        final double[] e = new double[tmpDiagDim]; // e
        final int tmpOffLength = bidiagonal.superdiagonal.size();
        for (int i = 0; i < tmpOffLength; i++)
        {
            e[i] = bidiagonal.superdiagonal.doubleValue(i);
        }

        // Main iteration loop for the singular values.
        int kase;
        int k;
        int p = tmpDiagDim;
        while (p > 0)
        {

            //
            // This section of the program inspects for negligible elements in the s and e arrays.
            // On completion the variables kase and k are set as follows:
            //
            // kase = 1     if s[p] and e[k-1] are negligible and k<p                           => deflate negligible s[p]
            // kase = 2     if s[k] is negligible and k<p                                       => split at negligible s[k]
            // kase = 3     if e[k-1] is negligible, k<p, and s(k)...s(p) are not negligible    => perform QR-step
            // kase = 4     if e[p-1] is negligible                                             => convergence.
            //

            kase = 0;
            k = 0;

            for (k = p - 2; k >= -1; k--)
            {
                if (k == -1)
                {
                    break;
                }
                if (PrimitiveFunction.ABS.invoke(e[k]) <= (SVDnew32.TINY + (MACHINE_EPSILON * (PrimitiveFunction.ABS.invoke(s[k]) + PrimitiveFunction.ABS.invoke(s[k + 1])))))
                {
                    e[k] = ZERO;
                    break;
                }
            }
            if (k == (p - 2))
            {
                kase = 4;
            } else
            {
                int ks;
                for (ks = p - 1; ks >= k; ks--)
                {
                    if (ks == k)
                    {
                        break;
                    }
                    final double t = (ks != p ? PrimitiveFunction.ABS.invoke(e[ks]) : ZERO) + (ks != (k + 1) ? PrimitiveFunction.ABS.invoke(e[ks - 1]) : ZERO);
                    if (PrimitiveFunction.ABS.invoke(s[ks]) <= (SVDnew32.TINY + (MACHINE_EPSILON * t)))
                    {
                        s[ks] = ZERO;
                        break;
                    }
                }
                if (ks == k)
                {
                    kase = 3;
                } else if (ks == (p - 1))
                {
                    kase = 1;
                } else
                {
                    kase = 2;
                    k = ks;
                }
            }
            k++;

            switch (kase)
            { // Perform the task indicated by kase.

                case 1: // Deflate negligible s[p]

                    SVDnew32.doCase1(s, e, p, k, mtrxQ2);
                    break;

                case 2: // Split at negligible s[k]

                    SVDnew32.doCase2(s, e, p, k, mtrxQ1);
                    break;

                case 3: // Perform QR-step.

                    SVDnew32.doCase3(s, e, p, k, mtrxQ1, mtrxQ2);
                    break;

                case 4: // Convergence

                    SVDnew32.doCase4(s, k, mtrxQ1, mtrxQ2);
                    p--;
                    break;

                default:

                    throw new IllegalStateException();

            } // switch
        } // while

        //return new PrimitiveArray(s).asArray1D();
        return Array1D.PRIMITIVE.wrap(PrimitiveArray.wrap(s));
    }

    protected SVDnew32(final DecompositionStore.Factory<N, ? extends DecompositionStore<N>> factory, final BidiagonalDecomposition<N> bidiagonal)
    {
        super(factory, bidiagonal);
    }

    public boolean equals(final MatrixStore<N> other, final NumberContext context)
    {
        return MatrixUtils.equals(other, this, NumberContext.getGeneral(6));
    }

    public boolean isOrdered()
    {
        return true;
    }

    public boolean isSolvable()
    {
        return this.isComputed();
    }

    @Override
    protected boolean doCompute(final ElementsSupplier<N> matrix, final boolean singularValuesOnly, final boolean fullSize)
    {

        this.computeBidiagonal(matrix, fullSize);

        final DiagonalAccess<N> tmpBidiagonal = this.getBidiagonalAccessD();

        final DecompositionStore<N> tmpQ1 = singularValuesOnly ? null : this.getBidiagonalQ1();
        final DecompositionStore<N> tmpQ2 = singularValuesOnly ? null : this.getBidiagonalQ2();

        final Array1D<Double> tmpDiagonal = SVDnew32.toDiagonal(tmpBidiagonal, tmpQ1, tmpQ2);

        this.setSingularValues(tmpDiagonal);

        return this.computed(true);
    }

    @Override
    protected MatrixStore<N> makeD()
    {
        return this.wrap(new DiagonalAccess<>(this.getSingularValues(), null, null, ZERO)).get();
    }

    @Override
    protected MatrixStore<N> makeQ1()
    {
        return this.getBidiagonalQ1();
    }

    @Override
    protected MatrixStore<N> makeQ2()
    {
        return this.getBidiagonalQ2();
    }

    @Override
    protected Array1D<Double> makeSingularValues()
    {
        throw new IllegalStateException("Should never have to be called!");
    }
}

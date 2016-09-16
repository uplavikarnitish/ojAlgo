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
package org.ojalgo.matrix.task;

import java.math.BigDecimal;

import org.ojalgo.access.Access2D;
import org.ojalgo.access.Structure2D;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.Cholesky;
import org.ojalgo.matrix.decomposition.DecompositionStore;
import org.ojalgo.matrix.decomposition.LU;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.scalar.ComplexNumber;

public interface SolverTask<N extends Number> extends MatrixTask<N>
{

    public static abstract class Factory<N extends Number>
    {

        public final SolverTask<N> make(final MatrixStore<N> templateBody, final MatrixStore<N> templateRHS)
        {
            return this.make(templateBody, templateRHS, MatrixUtils.isHermitian(templateBody), false);
        }

        public abstract SolverTask<N> make(MatrixStore<N> templateBody, MatrixStore<N> templateRHS, boolean symmetric, boolean positiveDefinite);

    }

    public static final Factory<BigDecimal> BIG = new Factory<BigDecimal>()
    {

        @Override
        public SolverTask<BigDecimal> make(final MatrixStore<BigDecimal> templateBody, final MatrixStore<BigDecimal> templateRHS, final boolean symmetric,
                                           final boolean positiveDefinite)
        {
            if (symmetric && positiveDefinite)
            {
                return Cholesky.BIG.make(templateBody);
            } else if (templateBody.isSquare())
            {
                return LU.BIG.make(templateBody);
            } else if (templateBody.isTall())
            {
                return QR.BIG.make(templateBody);
            } else
            {
                return SingularValue.BIG.make(templateBody);
            }
        }

    };

    public static final Factory<ComplexNumber> COMPLEX = new Factory<ComplexNumber>()
    {

        @Override
        public SolverTask<ComplexNumber> make(final MatrixStore<ComplexNumber> templateBody, final MatrixStore<ComplexNumber> templateRHS,
                                              final boolean symmetric, final boolean positiveDefinite)
        {
            if (symmetric && positiveDefinite)
            {
                return Cholesky.COMPLEX.make(templateBody);
            } else if (templateBody.isSquare())
            {
                return LU.COMPLEX.make(templateBody);
            } else if (templateBody.isTall())
            {
                return QR.COMPLEX.make(templateBody);
            } else
            {
                return SingularValue.COMPLEX.make(templateBody);
            }
        }

    };

    public static final Factory<Double> PRIMITIVE = new Factory<Double>()
    {

        @Override
        public SolverTask<Double> make(final MatrixStore<Double> templateBody, final MatrixStore<Double> templateRHS, final boolean symmetric,
                                       final boolean positiveDefinite)
        {

            final boolean tmpVectorRHS = templateRHS.countColumns() == 1L;

            final long tmpColDim = templateBody.countColumns();

            if (symmetric)
            {

                if (!tmpVectorRHS)
                {
                    return positiveDefinite ? Cholesky.PRIMITIVE.make(templateBody) : LU.PRIMITIVE.make(templateBody);
                } else if (tmpColDim == 1l)
                {
                    return AbstractSolver.FULL_1X1;
                } else if (tmpColDim == 2l)
                {
                    return AbstractSolver.SYMMETRIC_2X2;
                } else if (tmpColDim == 3l)
                {
                    return AbstractSolver.SYMMETRIC_3X3;
                } else if (tmpColDim == 4l)
                {
                    return AbstractSolver.SYMMETRIC_4X4;
                } else if (tmpColDim == 5l)
                {
                    return AbstractSolver.SYMMETRIC_5X5;
                } else
                {
                    return positiveDefinite ? Cholesky.PRIMITIVE.make(templateBody) : LU.PRIMITIVE.make(templateBody);
                }

            } else if (templateBody.isSquare())
            {

                if (!tmpVectorRHS)
                {
                    return LU.PRIMITIVE.make(templateBody);
                } else if (tmpColDim == 1l)
                {
                    return AbstractSolver.FULL_1X1;
                } else if (tmpColDim == 2l)
                {
                    return AbstractSolver.FULL_2X2;
                } else if (tmpColDim == 3l)
                {
                    return AbstractSolver.FULL_3X3;
                } else if (tmpColDim == 4l)
                {
                    return AbstractSolver.FULL_4X4;
                } else if (tmpColDim == 5l)
                {
                    return AbstractSolver.FULL_5X5;
                } else
                {
                    return LU.PRIMITIVE.make(templateBody);
                }

            } else if (templateBody.isTall())
            {

                if (tmpVectorRHS && (tmpColDim <= 5))
                {
                    return AbstractSolver.LEAST_SQUARES;
                } else
                {
                    return QR.PRIMITIVE.make(templateBody);
                }

            } else
            {

                return SingularValue.PRIMITIVE.make(templateBody);
            }
        }

    };

    /**
     * <p>
     * Will create a {@linkplain DecompositionStore} instance suitable for use with
     * {@link #solve(Access2D, Access2D, DecompositionStore)}. The dimensions of the returned instance is not
     * specified by this interface - it is specified by the behaviour/requirements of each implementation.
     * </p>
     * <p>
     * When solving an equation system [A][X]=[B] ([mxn][nxb]=[mxb]) the preallocated memory/matrix will
     * typically be either mxb or nxb.
     * </p>
     *
     * @param templateBody
     * @param templateRHS
     */
    DecompositionStore<N> preallocate(Structure2D templateBody, Structure2D templateRHS);

    /**
     * [A][X]=[B] or [body][return]=[rhs]
     */
    default MatrixStore<N> solve(final Access2D<?> body, final Access2D<?> rhs) throws TaskException
    {
        return this.solve(body, rhs, this.preallocate(body, rhs));
    }

    /**
     * <p>
     * Exactly how (if at all) a specific implementation makes use of <code>preallocated</code> is not
     * specified by this interface. It must be documented for each implementation.
     * </p>
     * <p>
     * Should produce the same results as calling {@link #solve(Access2D, Access2D)}.
     * </p>
     * <p>
     * Use {@link #preallocate(Structure2D, Structure2D)} to obtain a suitbale <code>preallocated</code>.
     * </p>
     *
     * @param rhs          The Right Hand Side, wont be modfied
     * @param preallocated Preallocated memory for the results, possibly some intermediate results. You must
     *                     assume this is modified, but you cannot assume it will contain the full/final/correct solution.
     * @return The solution
     */
    MatrixStore<N> solve(Access2D<?> body, Access2D<?> rhs, DecompositionStore<N> preallocated) throws TaskException;

}

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

import org.ojalgo.access.Access2D;
import org.ojalgo.access.Structure2D;
import org.ojalgo.function.PrimitiveFunction;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.store.ElementsSupplier;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.store.RawStore;
import org.ojalgo.matrix.store.operation.DotProduct;
import org.ojalgo.matrix.task.TaskException;
import org.ojalgo.type.context.NumberContext;

final class RawCholesky extends RawDecomposition implements Cholesky<Double>
{

    private boolean mySPD = false;

    /**
     * Not recommended to use this constructor directly. Consider using the static factory method
     * {@linkplain org.ojalgo.matrix.decomposition.Cholesky#make(Access2D)} instead.
     */
    RawCholesky()
    {
        super();
    }

    public Double calculateDeterminant(final Access2D<?> matrix)
    {

        final double[][] retVal = this.reset(matrix, false);

        this.doDecompose(retVal, matrix);

        return this.getDeterminant();
    }

    public boolean checkAndCompute(final MatrixStore<Double> matrix)
    {

        mySPD = MatrixUtils.isHermitian(matrix);

        if (mySPD)
        {

            final double[][] retVal = this.reset(matrix, false);

            return this.doDecompose(retVal, matrix);

        } else
        {

            return this.computed(false);
        }
    }

    public boolean decompose(final ElementsSupplier<Double> matrix)
    {

        final double[][] retVal = this.reset(matrix, false);

        final RawStore tmpRawInPlaceStore = this.getRawInPlaceStore();

        matrix.supplyTo(tmpRawInPlaceStore);

        return this.doDecompose(retVal, tmpRawInPlaceStore);
    }

    public boolean equals(final MatrixStore<Double> matrix, final NumberContext context)
    {
        return MatrixUtils.equals(matrix, this, context);
    }

    public Double getDeterminant()
    {

        final double[][] tmpData = this.getRawInPlaceData();

        final int tmpMinDim = this.getMinDim();

        double retVal = ONE;
        double tmpVal;
        for (int ij = 0; ij < tmpMinDim; ij++)
        {
            tmpVal = tmpData[ij][ij];
            retVal *= tmpVal * tmpVal;
        }

        return retVal;
    }

    public MatrixStore<Double> getInverse()
    {
        final int tmpRowDim = this.getRowDim();
        return this.doGetInverse(this.allocate(tmpRowDim, tmpRowDim));
    }

    public MatrixStore<Double> getInverse(final DecompositionStore<Double> preallocated)
    {
        return this.doGetInverse((PrimitiveDenseStore) preallocated);
    }

    public MatrixStore<Double> getL()
    {
        return this.getRawInPlaceStore().logical().triangular(false, false).get();
    }

    @Override
    public MatrixStore<Double> invert(final Access2D<?> original, final DecompositionStore<Double> preallocated) throws TaskException
    {

        final double[][] retVal = this.reset(original, false);

        this.doDecompose(retVal, original);

        if (this.isSolvable())
        {
            return this.getInverse(preallocated);
        } else
        {
            throw TaskException.newNotInvertible();
        }
    }

    public boolean isSolvable()
    {
        return this.isComputed() && this.isSPD();
    }

    public boolean isSPD()
    {
        return mySPD;
    }

    public DecompositionStore<Double> preallocate(final Structure2D template)
    {
        return this.allocate(template.countRows(), template.countRows());
    }

    public DecompositionStore<Double> preallocate(final Structure2D templateBody, final Structure2D templateRHS)
    {
        return this.allocate(templateBody.countRows(), templateRHS.countColumns());
    }

    public MatrixStore<Double> reconstruct()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MatrixStore<Double> solve(final Access2D<?> body, final Access2D<?> rhs, final DecompositionStore<Double> preallocated) throws TaskException
    {

        final double[][] retVal = this.reset(body, false);

        this.doDecompose(retVal, body);

        if (this.isSolvable())
        {

            preallocated.fillMatching(rhs);

            return this.doSolve(preallocated);

        } else
        {
            throw TaskException.newNotSolvable();
        }
    }

    public MatrixStore<Double> solve(final ElementsSupplier<Double> rhs)
    {
        final DecompositionStore<Double> tmpPreallocated = this.allocate(rhs.countRows(), rhs.countColumns());
        return this.solve(rhs, tmpPreallocated);
    }

    @Override
    public MatrixStore<Double> solve(final ElementsSupplier<Double> rhs, final DecompositionStore<Double> preallocated)
    {

        rhs.supplyTo(preallocated);

        return this.doSolve(preallocated);
    }

    public MatrixStore<Double> solve(final MatrixStore<Double> rhs, final DecompositionStore<Double> preallocated)
    {

        preallocated.fillMatching(rhs);

        return this.doSolve(preallocated);
    }

    private boolean doDecompose(final double[][] data, final Access2D<?> input)
    {

        final int tmpDiagDim = this.getRowDim();
        mySPD = (this.getColDim() == tmpDiagDim);

        double[] tmpRowIJ;
        double[] tmpRowI;

        // Main loop.
        for (int ij = 0; ij < tmpDiagDim; ij++)
        { // For each row/column, along the diagonal
            tmpRowIJ = data[ij];

            final double tmpD = tmpRowIJ[ij] = PrimitiveFunction.SQRT.invoke(PrimitiveFunction.MAX.invoke(input.doubleValue(ij, ij) - DotProduct.invoke(tmpRowIJ, 0, tmpRowIJ, 0, 0, ij), ZERO));
            mySPD &= (tmpD > ZERO);

            for (int i = ij + 1; i < tmpDiagDim; i++)
            { // Update column below current row
                tmpRowI = data[i];

                tmpRowI[ij] = (input.doubleValue(i, ij) - DotProduct.invoke(tmpRowI, 0, tmpRowIJ, 0, 0, ij)) / tmpD;
            }
        }

        return this.computed(true);
    }

    private MatrixStore<Double> doGetInverse(final PrimitiveDenseStore preallocated)
    {

        final RawStore tmpBody = this.getRawInPlaceStore();

        preallocated.substituteForwards(tmpBody, false, false, true);
        preallocated.substituteBackwards(tmpBody, false, true, true);

        return preallocated.logical().hermitian(false).get();
    }

    private MatrixStore<Double> doSolve(final DecompositionStore<Double> preallocated)
    {

        final RawStore tmpBody = this.getRawInPlaceStore();

        preallocated.substituteForwards(tmpBody, false, false, false);
        preallocated.substituteBackwards(tmpBody, false, true, false);

        return preallocated;
    }

}

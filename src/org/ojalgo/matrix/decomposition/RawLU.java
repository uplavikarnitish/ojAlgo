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
import org.ojalgo.array.ArrayUtils;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.PrimitiveFunction;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.PrimitiveAggregator;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.store.ElementsSupplier;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.store.RawStore;
import org.ojalgo.matrix.store.operation.DotProduct;
import org.ojalgo.matrix.task.TaskException;
import org.ojalgo.type.context.NumberContext;

final class RawLU extends RawDecomposition implements LU<Double>
{

    private Pivot myPivot;

    /**
     * Not recommended to use this constructor directly. Consider using the static factory method
     * {@linkplain org.ojalgo.matrix.decomposition.LU#make(Access2D)} instead.
     */
    RawLU()
    {
        super();
    }

    public Double calculateDeterminant(final Access2D<?> matrix)
    {

        final double[][] tmpData = this.reset(matrix, false);

        this.getRawInPlaceStore().fillMatching(matrix);

        this.doDecompose(tmpData);

        return this.getDeterminant();
    }

    public boolean computeWithoutPivoting(final ElementsSupplier<Double> matrix)
    {

        final double[][] tmpData = this.reset(matrix, false);

        matrix.supplyTo(this.getRawInPlaceStore());

        return this.doDecompose(tmpData);
    }

    public boolean decompose(final ElementsSupplier<Double> matrix)
    {

        final double[][] tmpData = this.reset(matrix, false);

        matrix.supplyTo(this.getRawInPlaceStore());

        return this.doDecompose(tmpData);
    }

    public boolean equals(final MatrixStore<Double> aStore, final NumberContext context)
    {
        return MatrixUtils.equals(aStore, this, context);
    }

    public Double getDeterminant()
    {
        final int m = this.getRowDim();
        final int n = this.getColDim();
        if (m != n)
        {
            throw new IllegalArgumentException("RawStore must be square.");
        }
        final double[][] LU = this.getRawInPlaceData();
        double d = myPivot.signum();
        for (int j = 0; j < n; j++)
        {
            d *= LU[j][j];
        }
        return d;
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
        return this.getRawInPlaceStore().logical().triangular(false, true).get();
    }

    public int[] getPivotOrder()
    {
        return myPivot.getOrder();
    }

    public int getRank()
    {

        int retVal = 0;

        final MatrixStore<Double> tmpU = this.getU();
        final int tmpMinDim = (int) Math.min(tmpU.countRows(), tmpU.countColumns());

        final AggregatorFunction<Double> tmpLargest = PrimitiveAggregator.LARGEST.get();
        tmpU.visitDiagonal(0L, 0L, tmpLargest);
        final double tmpLargestValue = tmpLargest.doubleValue();

        for (int ij = 0; ij < tmpMinDim; ij++)
        {
            if (!tmpU.isSmall(ij, ij, tmpLargestValue))
            {
                retVal++;
            }
        }

        return retVal;
    }

    public MatrixStore<Double> getU()
    {
        return this.getRawInPlaceStore().logical().triangular(true, false).get();
    }

    @Override
    public MatrixStore<Double> invert(final Access2D<?> original, final DecompositionStore<Double> preallocated) throws TaskException
    {

        final double[][] tmpData = this.reset(original, false);

        this.getRawInPlaceStore().fillMatching(original);

        this.doDecompose(tmpData);

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
        return this.isSquareAndNotSingular();
    }

    public boolean isSquareAndNotSingular()
    {
        return (this.getRowDim() == this.getColDim()) && this.isNonsingular();
    }

    public DecompositionStore<Double> preallocate(final Structure2D template)
    {
        return this.allocate(template.countRows(), template.countRows());
    }

    public DecompositionStore<Double> preallocate(final Structure2D templateBody, final Structure2D templateRHS)
    {
        return this.allocate(templateBody.countRows(), templateRHS.countColumns());
    }

    @Override
    public void reset()
    {

        super.reset();

        myPivot = null;
    }

    @Override
    public MatrixStore<Double> solve(final Access2D<?> body, final Access2D<?> rhs, final DecompositionStore<Double> preallocated) throws TaskException
    {

        final double[][] tmpData = this.reset(body, false);

        this.getRawInPlaceStore().fillMatching(body);

        this.doDecompose(tmpData);

        if (this.isSolvable())
        {

            MatrixStore.PRIMITIVE.makeWrapper(rhs).row(myPivot.getOrder()).supplyTo(preallocated);

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

        rhs.get().logical().row(myPivot.getOrder()).supplyTo(preallocated);

        return this.doSolve(preallocated);
    }

    public MatrixStore<Double> solve(final MatrixStore<Double> rhs, final DecompositionStore<Double> preallocated)
    {

        rhs.logical().row(myPivot.getOrder()).supplyTo(preallocated);

        return this.doSolve(preallocated);
    }

    /**
     * Use a "left-looking", dot-product, Crout/Doolittle algorithm, essentially copied from JAMA.
     *
     * @see org.ojalgo.matrix.decomposition.MatrixDecomposition#decompose(ElementsSupplier)
     */
    private boolean doDecompose(final double[][] data)
    {

        final int tmpRowDim = this.getRowDim();
        final int tmpColDim = this.getColDim();

        myPivot = new Pivot(tmpRowDim);

        final double[] tmpColJ = new double[tmpRowDim];

        // Outer loop.
        for (int j = 0; j < tmpColDim; j++)
        {

            // Make a copy of the j-th column to localize references.
            for (int i = 0; i < tmpRowDim; i++)
            {
                tmpColJ[i] = data[i][j];
            }

            // Apply previous transformations.
            for (int i = 0; i < tmpRowDim; i++)
            {
                // Most of the time is spent in the following dot product.
                data[i][j] = tmpColJ[i] -= DotProduct.invoke(data[i], 0, tmpColJ, 0, 0, Math.min(i, j));
            }

            // Find pivot and exchange if necessary.
            int p = j;
            for (int i = j + 1; i < tmpRowDim; i++)
            {
                if (PrimitiveFunction.ABS.invoke(tmpColJ[i]) > PrimitiveFunction.ABS.invoke(tmpColJ[p]))
                {
                    p = i;
                }
            }
            if (p != j)
            {
                ArrayUtils.exchangeRows(data, j, p);
                myPivot.change(j, p);
            }

            // Compute multipliers.
            if (j < tmpRowDim)
            {
                final double tmpVal = data[j][j];
                if (tmpVal != ZERO)
                {
                    for (int i = j + 1; i < tmpRowDim; i++)
                    {
                        data[i][j] /= tmpVal;
                    }
                }
            }

        }

        return this.computed(true);
    }

    private MatrixStore<Double> doGetInverse(final PrimitiveDenseStore preallocated)
    {

        final int[] tmpPivotOrder = myPivot.getOrder();
        final int tmpRowDim = this.getRowDim();
        for (int i = 0; i < tmpRowDim; i++)
        {
            preallocated.set(i, tmpPivotOrder[i], PrimitiveMath.ONE);
        }

        final RawStore tmpBody = this.getRawInPlaceStore();

        preallocated.substituteForwards(tmpBody, true, false, !myPivot.isModified());

        preallocated.substituteBackwards(tmpBody, false, false, false);

        return preallocated;
    }

    private MatrixStore<Double> doSolve(final DecompositionStore<Double> preallocated)
    {

        final MatrixStore<Double> tmpBody = this.getRawInPlaceStore();

        preallocated.substituteForwards(tmpBody, true, false, false);

        preallocated.substituteBackwards(tmpBody, false, false, false);

        return preallocated;
    }

    /**
     * Is the matrix nonsingular?
     *
     * @return true if U, and hence A, is nonsingular.
     */
    boolean isNonsingular()
    {
        final int n = this.getColDim();

        final double[][] LU = this.getRawInPlaceData();
        for (int j = 0; j < n; j++)
        {
            if (LU[j][j] == 0)
            {
                return false;
            }
        }
        return true;
    }
}

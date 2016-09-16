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

import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.task.DeterminantTask;
import org.ojalgo.matrix.task.InverterTask;
import org.ojalgo.matrix.task.SolverTask;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.context.NumberContext;

/**
 * PrimitiveMatrix
 *
 * @author apete
 */
public final class PrimitiveMatrix extends AbstractMatrix<Double, PrimitiveMatrix>
{

    public static final BasicMatrix.Factory<PrimitiveMatrix> FACTORY = new MatrixFactory<>(PrimitiveMatrix.class, PrimitiveDenseStore.FACTORY);

    public static Builder<PrimitiveMatrix> getBuilder(final int aLength)
    {
        return FACTORY.getBuilder(aLength);
    }

    public static Builder<PrimitiveMatrix> getBuilder(final int aRowDim, final int aColDim)
    {
        return FACTORY.getBuilder(aRowDim, aColDim);
    }

    /**
     * This method is for internal use only - YOU should NOT use it!
     */
    PrimitiveMatrix(final MatrixStore<Double> aStore)
    {
        super(aStore);
    }

    public PrimitiveMatrix enforce(final NumberContext context)
    {
        return this.modify(context.getPrimitiveFunction());
    }

    public BigDecimal toBigDecimal(final int row, final int column)
    {
        return new BigDecimal(this.getStore().doubleValue(row, column));
    }

    public ComplexNumber toComplexNumber(final int row, final int column)
    {
        return ComplexNumber.valueOf(this.getStore().doubleValue(row, column));
    }

    @Override
    public PhysicalStore<Double> toPrimitiveStore()
    {
        return this.getStore().copy();
    }

    public String toString(final int row, final int col)
    {
        return Double.toString(this.doubleValue(row, col));
    }

    @SuppressWarnings("unchecked")
    @Override
    MatrixStore<Double> cast(final Access1D<?> matrix)
    {
        if (matrix instanceof PrimitiveMatrix)
        {
            return ((PrimitiveMatrix) matrix).getStore();
        } else if (matrix instanceof PrimitiveDenseStore)
        {
            return (PrimitiveDenseStore) matrix;
        } else if ((matrix instanceof MatrixStore) && !this.isEmpty() && (matrix.get(0) instanceof Double))
        {
            return (MatrixStore<Double>) matrix;
        } else if (matrix instanceof Access2D<?>)
        {
            return this.getStore().physical().copy((Access2D<?>) matrix);
        } else
        {
            return this.getStore().physical().columns(matrix);
        }
    }

    @Override
    DeterminantTask<Double> getDeterminantTask(final MatrixStore<Double> template)
    {
        return DeterminantTask.PRIMITIVE.make(template, this.isHermitian(), false);
    }

    @SuppressWarnings("unchecked")
    @Override
    MatrixFactory<Double, PrimitiveMatrix> getFactory()
    {
        return (MatrixFactory<Double, PrimitiveMatrix>) FACTORY;
    }

    @Override
    InverterTask<Double> getInverterTask(final MatrixStore<Double> base)
    {
        return InverterTask.PRIMITIVE.make(base, this.isHermitian(), false);
    }

    @Override
    SolverTask<Double> getSolverTask(final MatrixStore<Double> templateBody, final MatrixStore<Double> templateRHS)
    {
        return SolverTask.PRIMITIVE.make(templateBody, templateRHS, this.isHermitian(), false);
    }

}

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

import java.math.BigDecimal;
import java.util.Arrays;

import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.access.AccessUtils;
import org.ojalgo.access.ElementView2D;
import org.ojalgo.array.SparseArray;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.store.PhysicalStore.ColumnsRegion;
import org.ojalgo.matrix.store.PhysicalStore.FillByMultiplying;
import org.ojalgo.matrix.store.PhysicalStore.LimitRegion;
import org.ojalgo.matrix.store.PhysicalStore.OffsetRegion;
import org.ojalgo.matrix.store.PhysicalStore.RowsRegion;
import org.ojalgo.matrix.store.PhysicalStore.TransposedRegion;
import org.ojalgo.matrix.store.operation.MultiplyBoth;
import org.ojalgo.scalar.ComplexNumber;

public final class SparseStore<N extends Number> extends FactoryStore<N> implements ElementsConsumer<N>
{

    public static interface Factory<N extends Number>
    {

        SparseStore<N> make(long rowsCount, long columnsCount);

    }

    public static final SparseStore.Factory<BigDecimal> BIG = (rowsCount, columnsCount) -> SparseStore.makeBig((int) rowsCount, (int) columnsCount);

    public static final SparseStore.Factory<ComplexNumber> COMPLEX = (rowsCount, columnsCount) -> SparseStore.makeComplex((int) rowsCount, (int) columnsCount);

    public static final SparseStore.Factory<Double> PRIMITIVE = (rowsCount, columnsCount) -> SparseStore.makePrimitive((int) rowsCount, (int) columnsCount);

    public static SparseStore<BigDecimal> makeBig(final int rowsCount, final int columnsCount)
    {
        return new SparseStore<>(BigDenseStore.FACTORY, rowsCount, columnsCount, SparseArray.makeBig(rowsCount * columnsCount));
    }

    public static SparseStore<ComplexNumber> makeComplex(final int rowsCount, final int columnsCount)
    {
        return new SparseStore<>(ComplexDenseStore.FACTORY, rowsCount, columnsCount, SparseArray.makeComplex(rowsCount * columnsCount));
    }

    public static SparseStore<Double> makePrimitive(final int rowsCount, final int columnsCount)
    {
        return new SparseStore<>(PrimitiveDenseStore.FACTORY, rowsCount, columnsCount, SparseArray.makePrimitive(rowsCount * columnsCount));
    }

    private final SparseArray<N> myElements;
    private final int[] myFirsts;
    private final int[] myLimits;
    private final FillByMultiplying<N> myMultiplyer;

    private SparseStore(final org.ojalgo.matrix.store.PhysicalStore.Factory<N, ?> factory, final int rowsCount, final int columnsCount)
    {
        super(factory, rowsCount, columnsCount);
        myElements = null;
        myFirsts = null;
        myLimits = null;
        myMultiplyer = null;
        ProgrammingError.throwForIllegalInvocation();
    }

    SparseStore(final PhysicalStore.Factory<N, ?> factory, final int rowsCount, final int columnsCount, final SparseArray<N> elements)
    {

        super(factory, rowsCount, columnsCount);

        myElements = elements;
        myFirsts = new int[rowsCount];
        myLimits = new int[rowsCount];
        Arrays.fill(myFirsts, columnsCount);
        // Arrays.fill(myLimits, 0); // Behövs inte, redan 0

        final Class<? extends Number> tmpType = factory.scalar().zero().getNumber().getClass();
        if (tmpType.equals(Double.class))
        {
            myMultiplyer = (FillByMultiplying<N>) MultiplyBoth.getPrimitive(rowsCount, columnsCount);
        } else if (tmpType.equals(ComplexNumber.class))
        {
            myMultiplyer = (FillByMultiplying<N>) MultiplyBoth.getComplex(rowsCount, columnsCount);
        } else if (tmpType.equals(BigDecimal.class))
        {
            myMultiplyer = (FillByMultiplying<N>) MultiplyBoth.getBig(rowsCount, columnsCount);
        } else
        {
            myMultiplyer = null;
        }
    }

    public void add(final long row, final long col, final double addend)
    {
        if (addend != PrimitiveMath.ZERO)
        {
            myElements.add(AccessUtils.index(myFirsts.length, row, col), addend);
            this.updateNonZeros(row, col);
        }
    }

    public void add(final long row, final long col, final Number addend)
    {
        myElements.add(AccessUtils.index(myFirsts.length, row, col), addend);
        this.updateNonZeros(row, col);
    }

    public double doubleValue(final long row, final long col)
    {
        return myElements.doubleValue(AccessUtils.index(myFirsts.length, row, col));
    }

    public void empty()
    {
        myElements.empty();
        Arrays.fill(myFirsts, (int) this.countColumns());
        Arrays.fill(myLimits, 0);
    }

    public void fillByMultiplying(final Access1D<N> left, final Access1D<N> right)
    {
        myMultiplyer.invoke(this, left, (int) (left.count() / this.countRows()), right);
    }

    public void fillOne(final long row, final long col, final Access1D<?> values, final long valueIndex)
    {
        this.set(row, col, values.get(valueIndex));
    }

    public void fillOne(final long row, final long col, final N value)
    {
        myElements.fillOne(AccessUtils.index(myFirsts.length, row, col), value);
        this.updateNonZeros(row, col);
    }

    public void fillOne(final long row, final long col, final NullaryFunction<N> supplier)
    {
        myElements.fillOne(AccessUtils.index(myFirsts.length, row, col), supplier);
        this.updateNonZeros(row, col);
    }

    public int firstInColumn(final int col)
    {

        final int tmpRowDim = myFirsts.length;

        final int tmpRangeFirst = tmpRowDim * col;
        final int tmpRangeLimit = tmpRowDim * (col + 1);

        final long tmpFirstInRange = myElements.firstInRange(tmpRangeFirst, tmpRangeLimit);

        if (tmpRangeFirst == tmpFirstInRange)
        {
            return 0;
        } else
        {
            return (int) (tmpFirstInRange % tmpRowDim);
        }
    }

    public int firstInRow(final int row)
    {
        return myFirsts[row];
    }

    public N get(final long row, final long col)
    {
        return myElements.get(AccessUtils.index(myFirsts.length, row, col));
    }

    @Override
    public int limitOfColumn(final int col)
    {

        final int tmpRowDim = myFirsts.length;

        final int tmpRangeFirst = tmpRowDim * col;
        final int tmpRangeLimit = tmpRangeFirst + tmpRowDim;

        final long tmpLimitOfRange = myElements.limitOfRange(tmpRangeFirst, tmpRangeLimit);

        if (tmpRangeLimit == tmpLimitOfRange)
        {
            return tmpRowDim;
        } else
        {
            return (int) tmpLimitOfRange % tmpRowDim;
        }
    }

    @Override
    public int limitOfRow(final int row)
    {
        return myLimits[row];
    }

    public void modifyAll(final UnaryFunction<N> modifier)
    {
        final long tmpLimit = this.count();
        if (this.isPrimitive())
        {
            for (long i = 0L; i < tmpLimit; i++)
            {
                this.set(i, modifier.invoke(this.doubleValue(i)));
            }
        } else
        {
            for (long i = 0L; i < tmpLimit; i++)
            {
                this.set(i, modifier.invoke(this.get(i)));
            }
        }
    }

    public void modifyMatching(final Access1D<N> left, final BinaryFunction<N> function)
    {
        final long tmpLimit = Math.min(left.count(), this.count());
        if (this.isPrimitive())
        {
            for (long i = 0L; i < tmpLimit; i++)
            {
                this.set(i, function.invoke(left.doubleValue(i), this.doubleValue(i)));
            }
        } else
        {
            for (long i = 0L; i < tmpLimit; i++)
            {
                this.set(i, function.invoke(left.get(i), this.get(i)));
            }
        }
    }

    public void modifyMatching(final BinaryFunction<N> function, final Access1D<N> right)
    {
        final long tmpLimit = Math.min(this.count(), right.count());
        if (this.isPrimitive())
        {
            for (long i = 0L; i < tmpLimit; i++)
            {
                this.set(i, function.invoke(this.doubleValue(i), right.doubleValue(i)));
            }
        } else
        {
            for (long i = 0L; i < tmpLimit; i++)
            {
                this.set(i, function.invoke(this.get(i), right.get(i)));
            }
        }
    }

    public void modifyOne(final long row, final long col, final UnaryFunction<N> modifier)
    {
        if (this.isPrimitive())
        {
            this.set(row, col, modifier.invoke(this.doubleValue(row, col)));
        } else
        {
            this.set(row, col, modifier.invoke(this.get(row, col)));
        }
    }

    public void multiply(final Access1D<N> right, final ElementsConsumer<N> target)
    {

        if (this.isPrimitive())
        {

            final long tmpRightStructure = this.countColumns();
            final long tmpRightColumns = (int) target.countColumns();

            if (target instanceof SparseStore)
            {
                ((SparseStore<?>) target).empty();
            } else
            {
                target.fillAll(this.physical().scalar().zero().getNumber());
            }

            for (final ElementView2D<N, ?> tmpNonzero : this.nonzeros())
            {
                final long tmpRow = tmpNonzero.row();
                final long tmpCol = tmpNonzero.column();
                final double tmpValue = tmpNonzero.doubleValue();

                final long tmpFirst = MatrixUtils.firstInRow(right, tmpRow, 0L);
                final long tmpLimit = MatrixUtils.limitOfRow(right, tmpRow, tmpRightColumns);

                for (long j = tmpFirst; j < tmpLimit; j++)
                {
                    target.add(tmpRow, j, tmpValue * right.doubleValue(AccessUtils.index(tmpRightStructure, tmpCol, j)));
                }
            }

        } else
        {

            super.multiply(right, target);
        }

    }

    /**
     * Marked as deprecated because of it's preliminary design - not because it will be removed.
     *
     * @deprecated v40
     */
    @Deprecated
    public ElementView2D<N, ?> nonzeros()
    {
        return new Access2D.ElementView<N>(myElements.nonzeros(), this.countRows());
    }

    public final ElementsConsumer<N> regionByColumns(final int... columns)
    {
        return new ColumnsRegion<>(this, myMultiplyer, columns);
    }

    public final ElementsConsumer<N> regionByLimits(final int rowLimit, final int columnLimit)
    {
        return new LimitRegion<>(this, myMultiplyer, rowLimit, columnLimit);
    }

    public final ElementsConsumer<N> regionByOffsets(final int rowOffset, final int columnOffset)
    {
        return new OffsetRegion<>(this, myMultiplyer, rowOffset, columnOffset);
    }

    public final ElementsConsumer<N> regionByRows(final int... rows)
    {
        return new RowsRegion<>(this, myMultiplyer, rows);
    }

    public final ElementsConsumer<N> regionByTransposing()
    {
        return new TransposedRegion<>(this, myMultiplyer);
    }

    public void set(final long row, final long col, final double value)
    {
        myElements.set(AccessUtils.index(myFirsts.length, row, col), value);
        this.updateNonZeros(row, col);
    }

    public void set(final long row, final long col, final Number value)
    {
        myElements.set(AccessUtils.index(myFirsts.length, row, col), value);
        this.updateNonZeros(row, col);
    }

    private void updateNonZeros(final long row, final long col)
    {
        this.updateNonZeros((int) row, (int) col);
    }

    @Override
    protected void addNonZerosTo(final ElementsConsumer<N> consumer)
    {
        myElements.supplyNonZerosTo(consumer);
    }

    void updateNonZeros(final int row, final int col)
    {
        myFirsts[row] = Math.min(col, myFirsts[row]);
        myLimits[row] = Math.max(col + 1, myLimits[row]);
    }

}

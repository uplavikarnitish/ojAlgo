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

import org.ojalgo.function.VoidFunction;
import org.ojalgo.scalar.Scalar;

/**
 * N-dimensional accessor methods
 *
 * @author apete
 * @see Access1D
 */
public interface AccessAnyD<N extends Number> extends StructureAnyD, Access1D<N>
{

    public interface Elements extends StructureAnyD, Access1D.Elements
    {

        /**
         * @see Scalar#isAbsolute()
         */
        boolean isAbsolute(long[] reference);

        /**
         * @see Scalar#isSmall(double)
         */
        boolean isSmall(long[] reference, double comparedTo);

    }

    public interface IndexOf extends StructureAnyD, Access1D.IndexOf
    {

    }

    public interface Sliceable<N extends Number> extends StructureAnyD, Access1D.Sliceable<N>
    {

        Access1D<N> slice(final long[] first, final int dimension);

    }

    public interface Visitable<N extends Number> extends StructureAnyD, Access1D.Visitable<N>
    {

        void visitOne(long[] reference, VoidFunction<N> visitor);

    }

    /**
     * Will pass through each matching element position calling the {@code through} function. What happens is
     * entirely dictated by how you implement the callback.
     */
    default double doubleValue(final long index)
    {
        return this.doubleValue(AccessUtils.reference(index, this.shape()));
    }

    double doubleValue(long[] ref);

    default N get(final long index)
    {
        return this.get(AccessUtils.reference(index, this.shape()));
    }

    N get(long[] ref);

    default void passMatching(final CallbackAnyD<N> through, final MutateAnyD to)
    {
        CallbackAnyD.onMatching(this, through, to);
    }

}

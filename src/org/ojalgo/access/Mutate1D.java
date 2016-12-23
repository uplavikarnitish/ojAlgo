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

import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.FunctionUtils;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.function.UnaryFunction;

/**
 * 1-dimensional mutator methods
 *
 * @author apete
 */
public interface Mutate1D extends Structure1D
{

    interface BiModifiable<N extends Number> extends Structure1D
    {

        void modifyMatching(final Access1D<N> left, final BinaryFunction<N> function);

        void modifyMatching(final BinaryFunction<N> function, final Access1D<N> right);

    }

    /**
     * Fills the target
     *
     * @author apete
     */
    interface Fillable<N extends Number> extends Structure1D
    {

        default void fillAll(final N value)
        {
            final long tmpLimit = this.count();
            for (long i = 0L; i < tmpLimit; i++)
            {
                this.fillOne(i, value);
            }
        }

        default void fillAll(final NullaryFunction<N> supplier)
        {
            final long tmpLimit = this.count();
            for (long i = 0L; i < tmpLimit; i++)
            {
                this.fillOne(i, supplier);
            }
        }

        /**
         * <p>
         * Will fill the elements of [this] with the corresponding input values, and in the process (if
         * necessary) convert the elements to the correct type:
         * </p>
         * <code>this(i) = values(i)</code>
         */
        default void fillMatching(final Access1D<?> values)
        {
            final long tmpLimit = FunctionUtils.min(this.count(), values.count());
            for (long i = 0L; i < tmpLimit; i++)
            {
                this.fillOne(i, values, i);
            }
        }

        /**
         * <p>
         * Will fill the elements of [this] with the results of element wise invocation of the input binary
         * funtion:
         * </p>
         * <code>this(i) = function.invoke(left(i),right(i))</code>
         *
         * @deprecated v41 Use {@link BiModifiable#modifyMatching(Access1D, BinaryFunction)} or
         * {@link BiModifiable#modifyMatching(BinaryFunction, Access1D)} instaed
         */
        @Deprecated
        default void fillMatching(final Access1D<N> left, final BinaryFunction<N> function, final Access1D<N> right)
        {
            final long tmpLimit = FunctionUtils.min(left.count(), right.count(), this.count());
            for (long i = 0L; i < tmpLimit; i++)
            {
                this.fillOne(i, function.invoke(left.get(i), right.get(i)));
            }
        }

        /**
         * <p>
         * Will fill the elements of [this] with the results of element wise invocation of the input unary
         * funtion:
         * </p>
         * <code>this(i) = function.invoke(arguments(i))</code>
         *
         * @deprecated v41 Use {@link Modifiable#modifyAll(UnaryFunction)} instaed
         */
        @Deprecated
        default void fillMatching(final UnaryFunction<N> function, final Access1D<N> arguments)
        {
            final long tmpLimit = FunctionUtils.min(this.count(), arguments.count());
            for (long i = 0L; i < tmpLimit; i++)
            {
                this.fillOne(i, function.invoke(arguments.get(i)));
            }
        }

        void fillOne(long index, final Access1D<?> values, long valueIndex);

        void fillOne(long index, N value);

        void fillOne(long index, NullaryFunction<N> supplier);

        /**
         * @deprecated v41 Use {@link #fillOne(long, Access1D, long)} instead
         */
        @Deprecated
        default void fillOneMatching(final long index, final Access1D<?> values, final long valueIndex)
        {
            this.fillOne(index, values, valueIndex);
        }

        default void fillRange(final long first, final long limit, final N value)
        {
            for (long i = first; i < limit; i++)
            {
                this.fillOne(i, value);
            }
        }

        default void fillRange(final long first, final long limit, final NullaryFunction<N> supplier)
        {
            for (long i = first; i < limit; i++)
            {
                this.fillOne(i, supplier);
            }
        }
    }

    interface Modifiable<N extends Number> extends Structure1D
    {

        default void modifyAll(final UnaryFunction<N> modifier)
        {
            final long tmpLimit = this.count();
            for (long i = 0L; i < tmpLimit; i++)
            {
                this.modifyOne(i, modifier);
            }
        }

        void modifyOne(long index, UnaryFunction<N> modifier);

        default void modifyRange(final long first, final long limit, final UnaryFunction<N> modifier)
        {
            for (long i = first; i < limit; i++)
            {
                this.modifyOne(i, modifier);
            }
        }

    }

    void add(long index, double addend);

    void add(long index, Number addend);

    /**
     * Will pass through each matching element position calling the {@code through} function. What happens is
     * entirely dictated by how you implement the callback.
     */
    default <N extends Number> void passMatching(final Access1D<N> from, final Callback1D<N> through)
    {
        Callback1D.onMatching(from, through, this);
    }

    void set(long index, double value);

    void set(long index, Number value);

}

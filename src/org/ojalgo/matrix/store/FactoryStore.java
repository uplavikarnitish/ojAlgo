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

import org.ojalgo.ProgrammingError;

abstract class FactoryStore<N extends Number> extends AbstractStore<N>
{

    private final PhysicalStore.Factory<N, ?> myFactory;

    @SuppressWarnings("unused")
    private FactoryStore(final int rowsCount, final int columnsCount)
    {

        this(null, rowsCount, columnsCount);

        ProgrammingError.throwForIllegalInvocation();
    }

    protected FactoryStore(final PhysicalStore.Factory<N, ?> factory, final int rowsCount, final int columnsCount)
    {

        super(rowsCount, columnsCount);

        myFactory = factory;
    }

    /**
     * @deprecated Use {@link #physical()} instead
     */
    public final PhysicalStore.Factory<N, ?> factory()
    {
        return physical();
    }

    public final PhysicalStore.Factory<N, ?> physical()
    {
        return myFactory;
    }

}

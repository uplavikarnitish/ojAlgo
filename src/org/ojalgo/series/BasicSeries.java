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
package org.ojalgo.series;

import java.util.Collection;
import java.util.SortedMap;

import org.ojalgo.function.UnaryFunction;
import org.ojalgo.series.primitive.DataSeries;
import org.ojalgo.type.ColourData;
import org.ojalgo.type.keyvalue.KeyValue;

/**
 * A BasicSeries is a {@linkplain SortedMap} with:
 * <ul>
 * <li>Sligthly restricted type parameters</li>
 * <li>The option to set a name and colour</li>
 * <li>A few additional methods to help access and modify series entries</li>
 * </ul>
 *
 * @author apete
 */
public interface BasicSeries<K extends Comparable<K>, V extends Number> extends SortedMap<K, V>
{

    BasicSeries<K, V> colour(ColourData colour);

    V firstValue();

    ColourData getColour();

    DataSeries getDataSeries();

    String getName();

    double[] getPrimitiveValues();

    V lastValue();

    /**
     * @deprecated v41
     */
    @Deprecated
    void modifyAll(UnaryFunction<V> function);

    BasicSeries<K, V> name(String name);

    void putAll(Collection<? extends KeyValue<? extends K, ? extends V>> data);

}

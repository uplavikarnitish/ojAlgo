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
package org.ojalgo.finance.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ojalgo.ProgrammingError;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.netio.ResourceLocator;
import org.ojalgo.series.CalendarDateSeries;
import org.ojalgo.type.CalendarDateUnit;
import org.ojalgo.type.TypeCache;

public abstract class DataSource<DP extends DatePrice>
{

    protected static final boolean DEBUG = false;

    private final CalendarDateUnit myResolution;
    private final ResourceLocator myResourceLocator = new ResourceLocator();
    private final String mySymbol;

    @SuppressWarnings("unused")
    private DataSource()
    {

        this(null, null);

        ProgrammingError.throwForIllegalInvocation();
    }

    protected DataSource(final String symbol, final CalendarDateUnit resolution)
    {

        super();

        mySymbol = symbol;
        myResolution = resolution;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof DataSource))
        {
            return false;
        }
        final DataSource<?> other = (DataSource<?>) obj;
        if (myResolution != other.myResolution)
        {
            return false;
        }
        if (!this.getClass().equals(other.getClass()))
        {
            return false;
        }
        if (mySymbol == null)
        {
            if (other.mySymbol != null)
            {
                return false;
            }
        } else if (!mySymbol.equals(other.mySymbol))
        {
            return false;
        }
        return true;
    }

    public List<DP> getHistoricalPrices()
    {
        return this.getHistoricalPrices(myResourceLocator.getStreamReader());
    }

    public List<DP> getHistoricalPrices(final BufferedReader reader)
    {

        final ArrayList<DP> retVal = new ArrayList<>();

        String tmpLine;
        DP tmpHistPrice;
        try
        {
            tmpLine = reader.readLine();

            if (DEBUG)
            {
                BasicLogger.debug(tmpLine);
            }

            while ((tmpLine = reader.readLine()) != null)
            {

                if (DEBUG)
                {
                    BasicLogger.debug(tmpLine);
                }

                tmpHistPrice = this.parse(tmpLine);
                retVal.add(tmpHistPrice);
            }
            reader.close();
        } catch (final IOException anException)
        {
            anException.printStackTrace();
        }

        Collections.sort(retVal);

        return retVal;
    }

    public CalendarDateSeries<Double> getPriceSeries()
    {
        return this.getPriceSeries(myResourceLocator.getStreamReader());
    }

    public CalendarDateSeries<Double> getPriceSeries(final BufferedReader reader)
    {

        final CalendarDateSeries<Double> retVal = new CalendarDateSeries<Double>(myResolution).name(mySymbol);

        for (final DatePrice tmpDatePrice : this.getHistoricalPrices(reader))
        {
            retVal.put(tmpDatePrice.getKey(), tmpDatePrice.getValue());
        }

        return retVal;
    }

    public CalendarDateUnit getResolution()
    {
        return myResolution;
    }

    public String getSymbol()
    {
        return mySymbol;
    }

    public TypeCache<? extends List<DP>> getSymbolCache(final long purgeIntervalMeassure, final CalendarDateUnit purgeIntervalUnit)
    {
        return new TypeCache<List<DP>>(purgeIntervalMeassure, purgeIntervalUnit)
        {

            @Override
            protected List<DP> recreateCache()
            {
                return DataSource.this.getHistoricalPrices();
            }
        };
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((myResolution == null) ? 0 : myResolution.hashCode());
        result = (prime * result) + ((mySymbol == null) ? 0 : mySymbol.hashCode());
        return result;
    }

    protected String addQueryParameter(final String key, final String value)
    {
        return myResourceLocator.addQueryParameter(key, value);
    }

    protected abstract DP parse(String line);

    protected void setHost(final String host)
    {
        myResourceLocator.setHost(host);
    }

    protected void setPath(final String path)
    {
        myResourceLocator.setPath(path);
    }

}

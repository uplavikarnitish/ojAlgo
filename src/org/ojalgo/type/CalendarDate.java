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
package org.ojalgo.type;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * <p>
 * Originally, long before Java 8 and its new Date and Time API, this class was designed to provide an
 * immutable complement to the existing {@linkplain Date} and {@linkplain Calendar} and to have easy/direct
 * access to the underlying millisecond value.
 * </p>
 * <p>
 * In terms of the newer API it most closely corresponds to an {@linkplain Instant}, but does not have its
 * nanosecond granularity. It has been retrofitted to implement the {@linkplain Temporal} and
 * {@linkplain TemporalAdjuster} interfaces.
 * </p>
 *
 * @author apete
 * @see CalendarDateDuration
 * @see CalendarDateUnit
 */
public final class CalendarDate implements Temporal, Comparable<CalendarDate>
{

    static final int NANOS_PER_SECOND = 1_000_000_000;
    static final long SECONDS_PER_DAY = 24L * 60L * 60L;

    public static CalendarDate from(final TemporalAccessor temporal)
    {
        Objects.requireNonNull(temporal, "temporal");
        if (temporal instanceof CalendarDate)
        {
            return (CalendarDate) temporal;
        } else if (temporal instanceof Instant)
        {
            return new CalendarDate(((Instant) temporal).toEpochMilli());
        } else
        {
            try
            {
                final long tmpSeconds = temporal.getLong(ChronoField.INSTANT_SECONDS);
                final int tmpMillisOfSecond = temporal.get(ChronoField.MILLI_OF_SECOND);
                return new CalendarDate((tmpSeconds * 1000L) + tmpMillisOfSecond);
            } catch (final DateTimeException ex)
            {
                throw new DateTimeException("Unable to obtain CalendarDate from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(),
                        ex);
            }
        }
    }

    public static CalendarDate make(final Calendar calendar, final CalendarDateUnit resolution)
    {
        return new CalendarDate(resolution.toTimeInMillis(calendar));
    }

    public static CalendarDate make(final CalendarDateUnit resolution)
    {
        return new CalendarDate(resolution.toTimeInMillis(System.currentTimeMillis()));
    }

    public static CalendarDate make(final Date date, final CalendarDateUnit resolution)
    {
        return new CalendarDate(resolution.toTimeInMillis(date));
    }

    public static CalendarDate make(final long timeInMIllis, final CalendarDateUnit resolution)
    {
        return new CalendarDate(resolution.toTimeInMillis(timeInMIllis));
    }

    static long millis(final TemporalAccessor temporal)
    {
        if (temporal instanceof CalendarDate)
        {
            return ((CalendarDate) temporal).millis;
        } else if (temporal instanceof Instant)
        {
            return ((Instant) temporal).toEpochMilli();
        } else
        {
            try
            {
                final long tmpSeconds = temporal.getLong(ChronoField.INSTANT_SECONDS);
                final int tmpMillisOfSecond = temporal.get(ChronoField.MILLI_OF_SECOND);
                return (tmpSeconds * 1000L) + tmpMillisOfSecond;
            } catch (final DateTimeException ex)
            {
                throw new DateTimeException("No millis!");
            }
        }
    }

    public final long millis;

    public CalendarDate()
    {

        super();

        millis = System.currentTimeMillis();
    }

    public CalendarDate(final Calendar calendar)
    {

        super();

        millis = calendar.getTimeInMillis();
    }

    public CalendarDate(final Date date)
    {

        super();

        millis = date.getTime();
    }

    public CalendarDate(final long timeInMillis)
    {

        super();

        millis = timeInMillis;
    }

    public CalendarDate(final String anSqlString)
    {

        super();

        final boolean tmpDatePart = anSqlString.indexOf('-') >= 0;
        final boolean tmpTimePart = anSqlString.indexOf(':') >= 0;

        if (tmpDatePart && tmpTimePart)
        {
            millis = StandardType.SQL_DATETIME.parse(anSqlString).getTime();
        } else if (tmpDatePart && !tmpTimePart)
        {
            millis = StandardType.SQL_DATE.parse(anSqlString).getTime();
        } else if (!tmpDatePart && tmpTimePart)
        {
            millis = StandardType.SQL_TIME.parse(anSqlString).getTime();
        } else
        {
            millis = 0L;
        }
    }

    public int compareTo(final CalendarDate ref)
    {
        return Long.signum(millis - ref.millis);
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
        if (!(obj instanceof CalendarDate))
        {
            return false;
        }
        final CalendarDate other = (CalendarDate) obj;
        if (millis != other.millis)
        {
            return false;
        }
        return true;
    }

    public CalendarDate filter(final CalendarDateUnit resolution)
    {
        if (resolution.isCalendarUnit())
        {
            return new CalendarDate(resolution.toTimeInMillis(this.toCalendar()));
        } else
        {
            return new CalendarDate(resolution.toTimeInMillis(millis));
        }
    }

    public long getLong(final TemporalField field)
    {
        if (field instanceof ChronoField)
        {
            if (field == ChronoField.INSTANT_SECONDS)
            {
                return millis / 1000L;
            } else
            {
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        } else
        {
            return field.getFrom(this);
        }
    }

    @Override
    public int hashCode()
    {
        return (int) (millis ^ (millis >>> 32));
    }

    public boolean isSupported(final TemporalField field)
    {
        if (field instanceof ChronoField)
        {
            return (field == ChronoField.INSTANT_SECONDS) || (field == ChronoField.MILLI_OF_SECOND);
        } else
        {
            return field.isSupportedBy(this);
        }
    }

    public boolean isSupported(final TemporalUnit unit)
    {
        if (unit instanceof CalendarDateUnit)
        {
            return true;
        } else if (unit instanceof ChronoUnit)
        {
            return unit.isTimeBased() || (unit == ChronoUnit.DAYS);
        } else if (unit != null)
        {
            return unit.isSupportedBy(this);
        } else
        {
            return false;
        }
    }

    public Temporal plus(final long amountToAdd, final TemporalUnit unit)
    {
        if (unit instanceof CalendarDateUnit)
        {
            return this.step((int) amountToAdd, (CalendarDateUnit) unit);
        } else if (unit instanceof ChronoUnit)
        {
            return this.toInstant().plus(amountToAdd, unit);
        } else
        {
            return unit.addTo(this, amountToAdd);
        }
    }

    /**
     * Only steps with the int part of {@linkplain CalendarDateDuration#measure} .
     */
    public CalendarDate step(final CalendarDateDuration aStepDuration)
    {
        return this.step((int) aStepDuration.measure, aStepDuration.unit);
    }

    public CalendarDate step(final CalendarDateUnit aStepUnit)
    {
        return this.step(1, aStepUnit);
    }

    public CalendarDate step(final int aStepCount, final CalendarDateUnit aStepUnit)
    {
        if (aStepUnit.isCalendarUnit())
        {
            return new CalendarDate(aStepUnit.step(this.toCalendar(), aStepCount));
        } else
        {
            return new CalendarDate(aStepUnit.step(millis, aStepCount));
        }
    }

    public Calendar toCalendar()
    {
        final GregorianCalendar retVal = new GregorianCalendar();
        retVal.setTimeInMillis(millis);
        return retVal;
    }

    public Calendar toCalendar(final Locale locale)
    {
        final GregorianCalendar retVal = new GregorianCalendar(locale);
        retVal.setTimeInMillis(millis);
        return retVal;
    }

    public Calendar toCalendar(final TimeZone zone)
    {
        final GregorianCalendar retVal = new GregorianCalendar(zone);
        retVal.setTimeInMillis(millis);
        return retVal;
    }

    public Calendar toCalendar(final TimeZone zone, final Locale locale)
    {
        final GregorianCalendar retVal = new GregorianCalendar(zone, locale);
        retVal.setTimeInMillis(millis);
        return retVal;
    }

    public Date toDate()
    {
        return new Date(millis);
    }

    public Instant toInstant()
    {
        return Instant.ofEpochMilli(millis);
    }

    public LocalDate toLocalDate()
    {
        return this.toLocalDate(ZoneOffset.UTC);
    }

    public LocalDate toLocalDate(final ZoneOffset offset)
    {
        final long tmpSeconds = Math.floorDiv(millis, 1000L);
        final long tmpLocalSeconds = tmpSeconds + offset.getTotalSeconds();
        final long tmpLocalDay = Math.floorDiv(tmpLocalSeconds, CalendarDate.SECONDS_PER_DAY);
        return LocalDate.ofEpochDay(tmpLocalDay);
    }

    public LocalDateTime toLocalDateTime()
    {
        return this.toLocalDateTime(ZoneOffset.UTC);
    }

    public LocalDateTime toLocalDateTime(final ZoneOffset offset)
    {
        final long tmpSeconds = Math.floorDiv(millis, 1000L);
        final int tmpNanos = (int) Math.floorMod(millis, 1000L);
        return LocalDateTime.ofEpochSecond(tmpSeconds, tmpNanos, offset);
    }

    public LocalTime toLocalTime()
    {
        return this.toLocalTime(ZoneOffset.UTC);
    }

    public LocalTime toLocalTime(final ZoneOffset offset)
    {

        final long tmpSeconds = Math.floorDiv(millis, 1000L);

        final int tmpNanos = (int) Math.floorMod(millis, 1000L);

        final long tmpLocalSeconds = tmpSeconds + offset.getTotalSeconds();

        final int tmpSecondOfDay = (int) Math.floorMod(tmpLocalSeconds, CalendarDate.SECONDS_PER_DAY);

        final int tmpNanoOfDay = (tmpSecondOfDay * CalendarDate.NANOS_PER_SECOND) + tmpNanos;

        return LocalTime.ofNanoOfDay(tmpNanoOfDay);

    }

    /**
     * @deprecated v39
     */
    @Deprecated
    public Date toSqlDate()
    {
        final LocalDate tmpDateOnly = this.toLocalDate();
        final int tmpYear = tmpDateOnly.getYear() - 1900;
        final int tmpMonth = tmpDateOnly.getMonthValue() - 1;
        final int tmpDayOfMonth = tmpDateOnly.getDayOfMonth();
        return new Date(tmpYear, tmpMonth, tmpDayOfMonth);
    }

    /**
     * @deprecated v39
     */
    @Deprecated
    public Date toSqlTime()
    {
        final LocalTime tmpTimeOnly = this.toLocalTime();
        final int tmpYear = 0;
        final int tmpMonth = 0;
        final int tmpDate = 1;
        final int tmpHour = tmpTimeOnly.getHour();
        final int tmpMinute = tmpTimeOnly.getMinute();
        final int tmpSecond = tmpTimeOnly.getSecond();
        return new Date(tmpYear, tmpMonth, tmpDate, tmpHour, tmpMinute, tmpSecond);
    }

    /**
     * @deprecated v39
     */
    @Deprecated
    public Date toSqlTimestamp()
    {
        return new Date(millis);
    }

    @Override
    public String toString()
    {
        return StandardType.SQL_DATETIME.format(this.toDate());
    }

    public long toTimeInMillis(final CalendarDateUnit resolution)
    {
        if (resolution.isCalendarUnit())
        {
            return resolution.toTimeInMillis(this.toCalendar());
        } else
        {
            return resolution.toTimeInMillis(millis);
        }
    }

    public long until(final Temporal endExclusive, final TemporalUnit unit)
    {
        if (unit instanceof CalendarDateUnit)
        {
            return ((CalendarDateUnit) unit).count(millis, CalendarDate.millis(endExclusive));
        } else if (unit instanceof ChronoUnit)
        {
            return this.toInstant().until(endExclusive, unit);
        } else
        {
            return unit.between(this, endExclusive);
        }
    }

    public CalendarDate with(final TemporalField field, final long newValue)
    {
        if (field instanceof ChronoField)
        {
            if (field == ChronoField.INSTANT_SECONDS)
            {
                final long tmpMillisOfSecond = millis % 1000L;
                return new CalendarDate((newValue * 1000L) + tmpMillisOfSecond);
            } else if (field == ChronoField.MILLI_OF_SECOND)
            {
                final long tmpSeconds = millis / 1000L;
                return new CalendarDate((tmpSeconds * 1000L) + newValue);
            } else
            {
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        } else
        {
            return field.adjustInto(this, newValue);
        }
    }

}

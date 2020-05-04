package com.gachon.priend.data.datetime;

import android.icu.util.Calendar;

/**
 * A class representing date.
 *
 * @author 유근혁
 * @since May 4th 2020
 */
public final class Date implements Comparable<Date> {

    /**
     * @return Date instance represents now
     */
    public static Date getNow() {
        Calendar now = Calendar.getInstance();
        return new Date(now.getTimeInMillis());
    }

    private Calendar core;

    /**
     * Create instance from year, month, day value.
     *
     * @param year Year of the date
     * @param month Month of the date
     * @param day Day of month of the date
     */
    public Date(int year, int month, int day) {
        core = Calendar.getInstance();

        setYear(year);
        setMonth(month);
        setDay(day);
    }

    /**
     * Create instance from time in milli seconds
     *
     * @param timeInMillis Time in milli seconds of the date
     */
    public Date(long timeInMillis) {
        core = Calendar.getInstance();
        core.setTimeInMillis(timeInMillis);
    }

    // 겟 하는거 범위 생각해서 1~12월, 1~31로 리턴하게 조정하기

    /**
     * Get the value of year
     *
     * @return Value of year in [0, inf)
     */
    public int getYear() {
        return core.get(Calendar.YEAR);
    }

    /**
     * Set the value of year.
     *
     * @param year The value of year in [0, inf)
     */
    public void setYear(int year) {
        if (year < 0) {
            year = 0;
        }
        core.set(Calendar.YEAR, year);
    }

    /**
     * Get the value of month
     *
     * @return The value of month in [1, 12]
     */
    public int getMonth() {
        return core.get(Calendar.MONTH) + 1;
    }

    /**
     * Set the value of month
     *
     * @param month The value of month in [0, 12]
     */
    public void setMonth(int month) {
        if (month < 1) {
            month = 1;
        } else if (month > 12) {
            month = 12;
        }
        core.set(Calendar.MONTH, month - 1);
    }

    /**
     * Get the value of day
     *
     * @return The value of day in [1, 31]
     */
    public int getDay() {
        return core.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Set the value of day
     *
     * @param day The value of day in [1, 31]
     */
    public void setDay(int day) {
        if (day < 1) {
            day = 1;
        } else if (day > 31) {
            day = 31;
        }
        core.set(Calendar.DAY_OF_MONTH, day);
    }

    /**
     * Get time in milli seconds
     *
     * @return Time in milli seconds
     */
    public long toMillis() {
        core.set(getYear(), getMonth(), getDay(), 0, 0, 0);
        core.set(Calendar.MILLISECOND, 0);

        return core.getTimeInMillis();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Date) {
            return getYear() == ((Date) other).getYear() && getMonth() == ((Date) other).getMonth() && getDay() == ((Date) other).getDay();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getYear() | (getMonth() << 8) | (getDay() << 8);
    }

    @Override
    public int compareTo(Date o) {
        return (int)(this.toMillis() - o.toMillis());
    }
}

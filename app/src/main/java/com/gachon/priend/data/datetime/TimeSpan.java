package com.gachon.priend.data.datetime;

/**
 * A class that represents term between two dates.
 *
 * @author 유근혁
 * @since May 4th 2020
 */
public final class TimeSpan {

    private Date from;
    private Date to;
    private Date subtraction;

    /**
     * Create instance with from-date and to-date.
     *
     * @param from The from-date
     * @param to The to-date
     */
    public TimeSpan(Date from, Date to) {
        this.from = from;
        this.to = to;

        subtraction = new Date(to.toMillis() - from.toMillis());
    }

    /**
     * Get the from-date
     *
     * @return The from-date
     */
    public Date getFrom() {
        return from;
    }

    /**
     * Set the from date
     *
     * @param from The from-date
     * @throws NullPointerException thrown if 'from' is null
     */
    public void setFrom(Date from) throws NullPointerException {
        if (from == null) {
            throw new NullPointerException("from");
        }

        this.from = from;
        subtraction = new Date(to.toMillis() - from.toMillis());
    }

    /**
     * Get the to-date
     *
     * @return The to-date
     */
    public Date getTo() {
        return to;
    }

    /**
     * Set the to-date
     *
     * @param to The to-date
     * @throws NullPointerException thrown if 'to' is null
     */
    public void setTo(Date to) throws NullPointerException {
        if (to == null) {
            throw new NullPointerException("to");
        }

        this.to = to;
        subtraction = new Date(to.toMillis() - from.toMillis());
    }

    /**
     * Get the year value
     *
     * @return The year value
     */
    public int getYear() {
        return subtraction.getYear() - 1970;
    }

    /**
     * Get the month value
     *
     * @return The month value
     */
    public int getMonth() {
        return subtraction.getMonth();
    }

    /**
     * Get the day value
     *
     * @return The day value
     */
    public int getDay() {
        return subtraction.getDay() - 1;
    }
}

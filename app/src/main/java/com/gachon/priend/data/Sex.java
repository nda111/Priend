package com.gachon.priend.data;

public enum Sex {
    MALE(true, false), FEMALE(false, false), NEUTERED(true, true), SPAYED(false, true);

    /**
     * Get corresponding sex with short value
     *
     * @param s The short value
     * @return Corresponding sex
     */
    public static Sex fromShort(short s) {
        return evaluate((s & 0b01) != 0, (s & 0b10) != 0);
    }

    /**
     * Get corresponding sex with sex and neutered or not
     *
     * @param isMale Stands for weather it's male
     * @param isNeutered Stands it have been neutered
     * @return Corresponding sex
     */
    public static Sex evaluate(boolean isMale, boolean isNeutered) {
        if (isMale) {
            if (isNeutered) {
                return NEUTERED;
            } else {
                return MALE;
            }
        } else {
            if (isNeutered) {
                return SPAYED;
            } else {
                return FEMALE;
            }
        }
    }

    private boolean isMale;
    private boolean neuteredOrSpayed;

    Sex(boolean isMale, boolean neuteredOrSpayed) {
        this.isMale = isMale;
        this.neuteredOrSpayed = neuteredOrSpayed;
    }

    /**
     * Get the corresponding short value
     *
     * @return The short value
     */
    public short toShort() {
        return (short)((isMale ? 0b01 : 0) | (neuteredOrSpayed ? 0b10 : 0));
    }
}

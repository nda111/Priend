package com.gachon.priend.data.numeric;

/**
 * A class that contains static methods that convert an integer into byte array of corresponding length and invert.
 *
 * @author 유근혁
 * @since May 5th 2020
 */
public class Converter {

    /**
     * Convert 16bit-integer into a byte array
     *
     * @param s 16bit-integer
     * @return A byte array
     */
    public static byte[] integerToByteArray(short s) {
        return new byte[]{
                (byte) (s >> 8),
                (byte) (s & 0xFF)
        };
    }

    /**
     * Convert 32bit-integer into a byte array
     *
     * @param i 32bit-integer
     * @return A byte array
     */
    public static byte[] integerToByteArray(int i) {
        return new byte[]{
                (byte) (i >> 24),
                (byte) ((i >> 16) & 0xFF),
                (byte) ((i >> 8) & 0xFF),
                (byte) (i & 0xFF)
        };
    }

    /**
     * Convert 64bit-integer into a byte array
     *
     * @param l 64bit-integer
     * @return A byte array
     */
    public static byte[] integerToByteArray(long l) {
        return new byte[]{
                (byte) (l >> 56),
                (byte) ((l >> 48) & 0xFF),
                (byte) ((l >> 40) & 0xFF),
                (byte) ((l >> 32) & 0xFF),
                (byte) ((l >> 24) & 0xFF),
                (byte) ((l >> 16) & 0xFF),
                (byte) ((l >> 8) & 0xFF),
                (byte) (l & 0xFF)
        };
    }

    /**
     * Convert 2-bytes byte array into a 16-bit integer
     *
     * @param bytes A byte array to convert
     * @return A 16-bit integer
     */
    public static Short byteArrayToInt16OrNull(byte[] bytes) {
        if (bytes == null || bytes.length != 2) {
            return null;
        }

        return (short) ((bytes[0] << 8) | bytes[1]);
    }

    /**
     * Convert 4-bytes byte array into a 32-bit integer
     *
     * @param bytes A byte array to convert
     * @return A 32-bit integer
     */
    public static Integer byteArrayToInt32OrNull(byte[] bytes) {
        if (bytes == null || bytes.length != 4) {
            return null;
        }

        return (bytes[0] << 24) | (bytes[1] << 16) | (bytes[2] << 8) | bytes[3];
    }

    /**
     * Convert 8-bytes byte array into a 64-bit integer
     *
     * @param bytes A byte array to convert
     * @return A 46-bit integer
     */
    public static Long byteArrayToInt64OrNull(byte[] bytes) {
        if (bytes == null || bytes.length != 8) {
            return null;
        }

        return ((long)bytes[0] << 56) | ((long)bytes[1] << 48) | ((long)bytes[2] << 40) | ((long)bytes[3] << 32) | (bytes[4] << 24) | (bytes[5] << 16) | (bytes[6] << 8) | bytes[7];
    }
}

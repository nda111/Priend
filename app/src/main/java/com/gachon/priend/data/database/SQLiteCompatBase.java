package com.gachon.priend.data.database;

import androidx.annotation.NonNull;

/**
 * An interface that represents an object that can be written in database
 *
 * @param <TData> The type of the object
 */
public abstract class SQLiteCompatBase<TData, TID, THelper extends ISQLiteClassBridge<TData, TID> > {

    /**
     * Copy data from an instance
     *
     * @param data The data to clone
     */
    public abstract void copyFrom(@NonNull TData data);

    /**
     * Read an instance from database
     * @param helper A database helper instance
     * @param identifier An identifier of which willing to read from database
     * @return True if success, False if nothing was found from database
     */
    public boolean tryRead(THelper helper, TID identifier) {

        TData data = helper.readOrNull(identifier);
        if (data == null) {
            return false;
        } else {
            copyFrom(data);
            return true;
        }
    }

    /**
     * Write an instance to database
     * @param helper A database helper instance
     * @param data The instance that willing to write on the database
     * @return True if success, False otherwise e.g. constraint violation
     */
    boolean tryWrite(THelper helper, TData data) {

        return helper.tryWrite(data);
    }
}

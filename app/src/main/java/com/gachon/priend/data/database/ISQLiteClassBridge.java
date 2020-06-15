package com.gachon.priend.data.database;

import androidx.annotation.NonNull;

/**
 * An instance that implements read/write and clear method
 *
 * @param <TData> The type of data
 * @param <TID> The type of identifier of the data
 */
public interface ISQLiteClassBridge<TData, TID> {

    /**
     * Read data from the database
     *
     * @param identifier The identifier for the data
     * @return The instance from the database if exists, {null} otherwise
     */
    TData readOrNull(TID identifier);

    /**
     * Write an instance on the database
     *
     * @param data The data to write
     * @return True if success, False otherwise e.g. constraint violation
     */
    boolean tryWrite(@NonNull TData data);

    /**
     * Delete a row from the database
     *
     * @param identifier The identifier for a row to delete from database
     */
    void delete(@NonNull TID identifier);

    /**
     * Remove every rows from the databases
     */
    void clear();
}

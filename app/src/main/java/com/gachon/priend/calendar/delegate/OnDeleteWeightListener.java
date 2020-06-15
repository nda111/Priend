package com.gachon.priend.calendar.delegate;

import androidx.annotation.NonNull;

import com.gachon.priend.data.datetime.Date;

/**
 * An event listener that acts when the weight list item deleted
 *
 * @author 유근혁
 * @since June 2nd 2020
 */
public interface OnDeleteWeightListener {

    /**
     * When the item deleted
     * @param date A date that associated with the item
     */
    void onDelete(@NonNull Date date);
}

package com.gachon.priend.calendar.delegate;

import com.gachon.priend.calendar.Memo;

/**
 * A event listener interface for show memo command
 */
public interface ShowMemoListener {

    /**
     * A callback for show memo command
     * @param memo The memo selected
     */
    void showMemo(Memo memo);
}

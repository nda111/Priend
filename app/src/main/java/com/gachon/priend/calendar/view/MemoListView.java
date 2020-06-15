package com.gachon.priend.calendar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.priend.R;
import com.gachon.priend.calendar.Memo;
import com.gachon.priend.calendar.delegate.ShowMemoListener;
import com.google.android.material.button.MaterialButton;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * A recycler view that lists memos
 *
 * @author 유근혁
 * @since May 21st 2020
 */
public final class MemoListView extends RecyclerView {

    /**
     * A data class that holds list item and its components
     *
     * @author 유근혁
     * @since May 21st 2020
     */
    private final class MemoViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView contentTextView;
        private LinearLayout photoContainer;
        private ImageView photoImageView;
        private MaterialButton collapseButton;

        private boolean collapsed = true;

        /**
         * Create an instance with the view
         * @param itemView The item view instance
         */
        MemoViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.item_memo_text_view_title);
            contentTextView = itemView.findViewById(R.id.item_memo_text_view_text);
            photoImageView = itemView.findViewById(R.id.item_memo_image_view_photo);
            photoContainer = itemView.findViewById(R.id.item_memo_layout_photo);
            collapseButton = itemView.findViewById(R.id.item_memo_button_collapse);
        }

        /**
         * Display data on the item view
         * @param memo A memo to display
         */
        void onBind(final Memo memo) {
            titleTextView.setText(memo.getTitle());
            contentTextView.setText(memo.getText());
            photoContainer.setVisibility(View.GONE);

            if (memo.getPhoto() != null) {
                photoImageView.setImageBitmap(memo.getPhoto());

                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (collapsed) {
                            collapsed = false;
                            photoContainer.setVisibility(View.VISIBLE);
                        } else {
                            onShowMemoListener.showMemo(memo);
                        }
                    }
                });

                collapseButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        collapsed = true;
                        photoContainer.setVisibility(View.GONE);
                    }
                });
            } else {
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onShowMemoListener.showMemo(memo);
                    }
                });
            }
        }
    }

    /**
     * An adapter for {MemoListView}
     *
     * @author 유근혁
     * @since May 21st 2020
     */
    private final class MemoAdapter extends RecyclerView.Adapter<MemoViewHolder> {

        private List<Memo> memos;

        /**
         * Create an instance with a list of memo
         *
         * @param memos The list memo
         */
        public MemoAdapter(@NonNull List<Memo> memos) {
            this.memos = memos;
        }

        @NonNull
        @Override
        public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo_list, parent);
            return new MemoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {
            holder.onBind(memos.get(position));
        }

        @Override
        public int getItemCount() {
            return memos.size();
        }
    }

    private LinkedList<Memo> memos = new LinkedList<>();

    private ShowMemoListener onShowMemoListener = null;

    private void refreshList() {
        MemoAdapter adapter = new MemoAdapter(memos);
        setAdapter(adapter);
    }

    public MemoListView(@NonNull Context context) {
        super(context);
    }

    public MemoListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Set event listener to show the memo detail
     *
     * @param onShowMemoListener The event listener
     */
    public void setOnShowMemoListener(ShowMemoListener onShowMemoListener) {
        this.onShowMemoListener = onShowMemoListener;
    }

    /**
     * Get event listener to show the memo detail
     *
     * @return The event listener
     */
    public ShowMemoListener getOnShowMemoListener() {
        return onShowMemoListener;
    }

    /**
     * Get an iterator instance for memo list
     *
     * @return The iterator for memo list
     */
    public ListIterator<Memo> getMemoIterator() {
        return memos.listIterator();
    }

    /**
     * Append a new memo on the list
     *
     * @param memo The memo to append
     */
    public void addMemo(Memo memo) {
        memos.addLast(memo);
        refreshList();
    }

    /**
     * Remove a memo from the list
     *
     * @param memo The memo to delete
     * @return True, if success, false, otherwise
     */
    public boolean removeMemo(Memo memo) {
        boolean result = memos.remove(memo);
        refreshList();

        return result;
    }

    /**
     * Clear all items from the list
     */
    public void clearMemo() {
        memos.clear();
        refreshList();
    }
}

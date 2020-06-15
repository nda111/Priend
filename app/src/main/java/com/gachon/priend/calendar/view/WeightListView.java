package com.gachon.priend.calendar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.priend.R;
import com.gachon.priend.calendar.WeightCommit;
import com.gachon.priend.calendar.delegate.OnDeleteWeightListener;
import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.entity.Animal;

import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;

/**
 * A view that shows simple chart of weight
 *
 * @author 유근혁
 * @since June 2nd 2020
 */
public final class WeightListView extends RecyclerView {

    /**
     * A view holder that contains weight items
     *
     * @author 유근혁
     * @since June 2nd 2020
     */
    private final class WeightViewHolder extends RecyclerView.ViewHolder {

        private TextView weightTextView;
        private TextView dateTextView;
        private ImageButton deleteButton;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);

            weightTextView = itemView.findViewById(R.id.item_weight_list_text_view_weight);
            dateTextView = itemView.findViewById(R.id.item_weight_list_text_view_date);
            deleteButton = itemView.findViewById(R.id.item_weight_list_image_button_delete);
        }

        /**
         * Bind the data with the item view
         *
         * @param date The date value of the measurement
         * @param weight Measured weight value in kg
         */
        public void onBind(@NonNull Date date, double weight) {
            final String kgUnit = getResources().getString(R.string.weight_chart_unit_kg);
            final Locale locale = getResources().getConfiguration().getLocales().get(0);

            weightTextView.setText(String.format("%s %s", weight, kgUnit));
            dateTextView.setText(date.toString(locale));
        }
    }

    /**
     * An adapter class that contains weight-date pairs as a map.
     *
     * @author 유근혁
     * @since June 2nd 2020
     */
    private final class WeightAdapter extends RecyclerView.Adapter<WeightViewHolder> {

        private Date[] keyArray;
        private TreeMap<Date, Double> data;
        private OnDeleteWeightListener onDeleteWeightListener = null;

        /**
         * Initialize an instance with a sort of data and an on delete listener
         *
         * @param data The weight data that associated with its date
         * @param onDeleteWeightListener An event listener when the delete button was clicked
         */
        public WeightAdapter(@NonNull TreeMap<Date, Double> data, OnDeleteWeightListener onDeleteWeightListener) {

            this.data = data;
            this.keyArray = (Date[])this.data.keySet().toArray();

            this.onDeleteWeightListener = onDeleteWeightListener;
        }

        @NonNull
        @Override
        public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weight_list, parent);
            return new WeightViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {

            final Date key = keyArray[position];
            holder.onBind(key, Objects.requireNonNull(data.get(key)));

            ImageButton deleteButton = holder.itemView.findViewById(R.id.item_weight_list_image_button_delete);
            deleteButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onDeleteWeightListener != null) {

                        onDeleteWeightListener.onDelete(key);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private WeightCommit commit = null;
    private OnDeleteWeightListener onDeleteWeightListener = null;

    public WeightListView(@NonNull Context context) {
        super(context);
    }

    public WeightListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void refreshAdapter() {

        WeightAdapter adapter = new WeightAdapter(this.commit.getApplied(), this.onDeleteWeightListener);
        setAdapter(adapter);
    }

    /**
     * Set the on delete weight listener
     *
     * @return An event listener that called when the delete button on the item was deleted
     */
    public OnDeleteWeightListener getOnDeleteWeightListener() {

        return onDeleteWeightListener;
    }

    /**
     * Get the on delete weight listener
     * @param onDeleteWeightListener An event listener that called when the delete button on the item was deleted
     */
    public void setOnDeleteWeightListener(OnDeleteWeightListener onDeleteWeightListener) {

        this.onDeleteWeightListener = onDeleteWeightListener;
        refreshAdapter();
    }

    /**
     * Get a commit value of the changes
     *
     * @return A commit object
     */
    public WeightCommit getCommit() {

        return this.commit;
    }

    /**
     * Set the initial data with animal
     *
     * @param data The tree data of weight
     * @param animalId The specifier of an animal
     */
    public void setInitialData(@NonNull TreeMap<Date, Double> data, int animalId) {

        this.commit = new WeightCommit(data, animalId);
    }

    /**
     * Insert or update the weight with the date and weight value
     *
     * @param date The date when the weight was measured
     * @param weight The weight value
     */
    public void insertOrUpdateWeight(@NonNull Date date, double weight) {

        this.commit.insertOrUpdate(date, weight);
    }

    /**
     * Delete the weight with the date
     *
     * @param date The date when the weight was measured
     * @return True if successfully deleted, False if there is no weight that associated with given date or failed with exception
     */
    public boolean deleteWeight(@NonNull Date date) {

        return this.commit.delete(date);
    }
}

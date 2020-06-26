package com.gachon.priend.calendar;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.priend.R;
import com.gachon.priend.calendar.delegate.OnDeleteWeightListener;
import com.gachon.priend.data.datetime.Date;

import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;

/**
 * An adapter class that contains weight-date pairs as a map.
 *
 * @author 유근혁
 * @since June 2nd 2020
 */
public final class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

    /**
     * A view holder that contains weight items
     *
     * @author 유근혁
     * @since June 2nd 2020
     */
    public final class WeightViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView weightTextView;
        private TextView dateTextView;
        private ImageButton deleteButton;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            weightTextView = itemView.findViewById(R.id.item_weight_list_text_view_weight);
            dateTextView = itemView.findViewById(R.id.item_weight_list_text_view_date);
            deleteButton = itemView.findViewById(R.id.item_weight_list_image_button_delete);
        }

        /**
         * Bind the data with the item view
         *
         * @param date   The date value of the measurement
         * @param weight Measured weight value in kg
         */
        public void onBind(@NonNull Date date, double weight) {
            final String kgUnit = view.getContext().getResources().getString(R.string.weight_chart_unit_kg);
            final Locale locale = view.getContext().getResources().getConfiguration().getLocales().get(0);

            weightTextView.setText(String.format("%s %s", weight, kgUnit));
            dateTextView.setText(date.toString(locale));
        }
    }

    private Date[] keyArray;
    private TreeMap<Date, Double> data;
    private OnDeleteWeightListener onDeleteWeightListener;

    /**
     * Initialize an instance with a sort of data and an on delete listener
     *
     * @param data                   The weight data that associated with its date
     * @param onDeleteWeightListener An event listener when the delete button was clicked
     */
    public WeightAdapter(@NonNull TreeMap<Date, Double> data, OnDeleteWeightListener onDeleteWeightListener) {

        this.data = data;
        this.keyArray = new Date[data.size()];

        Object[] array = this.data.keySet().toArray();
        for (int i = 0; i < array.length; i++) {
            this.keyArray[i] = (Date) array[i];
        }

        this.onDeleteWeightListener = onDeleteWeightListener;
    }

    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_weight_list, parent, false);
        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {

        final Date key = keyArray[position];
        holder.onBind(key, Objects.requireNonNull(data.get(key)));

        ImageButton deleteButton = holder.itemView.findViewById(R.id.item_weight_list_image_button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
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

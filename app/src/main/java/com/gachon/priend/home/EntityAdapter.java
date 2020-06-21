package com.gachon.priend.home;

import android.content.Context;
import android.content.res.Resources;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.priend.R;
import com.gachon.priend.data.Sex;
import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.datetime.TimeSpan;
import com.gachon.priend.data.entity.Animal;
import com.gachon.priend.data.entity.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class EntityAdapter extends RecyclerView.Adapter<EntityAdapter.ViewHolder> {

    private class AnimalAdapter extends BaseAdapter {

        private ArrayList<Animal> animals;

        public AnimalAdapter(@NonNull ArrayList<Animal> animals) {

            this.animals = animals;
        }

        @Override
        public int getCount() {
            return animals.size();
        }

        @Override
        public Object getItem(int position) {
            return animals.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_home_animal, parent, false);

            final Animal animal = animals.get(position);
            final Resources resources = convertView.getResources();

            final ImageView sexImageView = convertView.findViewById(R.id.home_animal_image_view_sex);
            final Sex sex = animal.getSex();
            sexImageView.setImageResource(sex == Sex.MALE || sex == Sex.NEUTERED
                    ? R.drawable.ic_gender_male
                    : R.drawable.ic_gender_female);

            final TextView nameTextView = convertView.findViewById(R.id.home_animal_text_view_name);
            nameTextView.setText(animal.getName());

            final TextView ageTextView = convertView.findViewById(R.id.home_animal_text_view_age);
            final TimeSpan age = animal.getAge();
            final StringBuilder ageBuilder = new StringBuilder();
            if (age.getYear() > 0) {
                ageBuilder.append(age.getYear());
                ageBuilder.append(resources.getString(R.string.home_age_unit_year));
            } else if (0 < age.getMonth() && age.getMonth() < 12) {
                ageBuilder.append(age.getMonth());
                ageBuilder.append(resources.getString(R.string.home_age_unit_month));
            } else {
                ageBuilder.append(age.getDay());
                ageBuilder.append(resources.getString(R.string.home_age_unit_day));
            }
            ageTextView.setText(ageBuilder.toString());

            final Map.Entry<Date, Double> lastWeight = animal.getWeights().lastEntry();
            if (lastWeight != null) {
                final double weight = (int)(lastWeight.getValue() * 100) / 100.0;
                final Date date = lastWeight.getKey();

                final TextView weightTextView = convertView.findViewById(R.id.home_animal_text_view_weight);

                final String weightBuilder = weight +
                        resources.getString(R.string.home_weight_unit_kg) + " (" +
                        date.toString(resources.getConfiguration().getLocales().get(0)) + ')';
                weightTextView.setText(weightBuilder);
            }

            final ImageButton editButton = convertView.findViewById(R.id.home_animal_button_edit);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    eventHandler.editAnimal(animal);
                }
            });

            return convertView;
        }
    }

    public interface EventHandler {

        void addGroup();

        void addAnimal(@NonNull Group group);

        void editAnimal(@NonNull Animal animal);

        void onAnimalClicked(@NonNull Animal animal);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout groupContainer;
        public ImageView arrowImageView;
        public TextView nameTextView;
        public TextView idTextView;

        public ListView animalListView;
        public ImageButton addAnimalImageButton;

        public FrameLayout newGroupContainer;
        public ImageButton addGroupImageButton;

        private boolean isExpanded = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupContainer = itemView.findViewById(R.id.home_group_container_group);
            arrowImageView = itemView.findViewById(R.id.home_group_image_view_arrow);
            nameTextView = itemView.findViewById(R.id.home_group_text_view_name);
            idTextView = itemView.findViewById(R.id.home_group_text_view_id);

            animalListView = itemView.findViewById(R.id.home_group_list_view_animal);
            addAnimalImageButton = itemView.findViewById(R.id.home_group_image_button_animal_new);

            newGroupContainer = itemView.findViewById(R.id.home_group_container_group_new);
            addGroupImageButton = itemView.findViewById(R.id.home_group_image_button_group_new);
        }

        public void bind(final Group group, final ArrayList<Animal> animals) {
            isExpanded = false;

            if (group == null) {
                groupContainer.setVisibility(View.GONE);
                newGroupContainer.setVisibility(View.VISIBLE);

                addGroupImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (eventHandler != null) {
                            eventHandler.addGroup();
                        }
                    }
                });
            } else {
                groupContainer.setVisibility(View.VISIBLE);
                newGroupContainer.setVisibility(View.GONE);

                groupContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isExpanded) {
                            animalListView.setVisibility(View.GONE);
                            addAnimalImageButton.setVisibility(View.GONE);
                            arrowImageView.setImageResource(R.drawable.ic_expand_more_black_24dp);
                        } else {
                            animalListView.setVisibility(View.VISIBLE);
                            addAnimalImageButton.setVisibility(View.VISIBLE);
                            arrowImageView.setImageResource(R.drawable.ic_expand_less_black_24dp);
                        }

                        isExpanded = !isExpanded;
                    }
                });

                arrowImageView.setImageResource(R.drawable.ic_expand_more_black_24dp);

                nameTextView.setText(group.getName());

                final StringBuilder idBuilder = new StringBuilder('#');
                final String idString = Integer.toHexString(group.getId()).toUpperCase();
                for (int i = 0; i < 8 - idString.length(); i++) {
                    idBuilder.append('0');
                }
                idBuilder.append(idString);
                idTextView.setText(idBuilder.toString());

                final AnimalAdapter adapter = new AnimalAdapter(animals);
                animalListView.setAdapter(adapter);
                animalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (eventHandler != null) {
                            eventHandler.onAnimalClicked(animals.get(position));
                        }
                    }
                });

                addAnimalImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (eventHandler != null) {
                            eventHandler.addAnimal(group);
                        }
                    }
                });
            }
        }
    }

    private LinkedList<Group> groups;
    private HashMap<Group, ArrayList<Animal>> entries;

    private EventHandler eventHandler = null;

    public EntityAdapter(@NonNull LinkedList<Group> groups, @NonNull HashMap<Group, ArrayList<Animal>> entries) {
        this.groups = groups;
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position == groups.size()) {
            Log.d("MainActivity", "New Item Position: " + position);
            holder.bind(null, null);
        } else {
            final Group group = groups.get(position);
            holder.bind(group, entries.get(group));
        }
    }

    @Override
    public int getItemCount() {
        return groups == null ? 1 : groups.size() + 1;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
}

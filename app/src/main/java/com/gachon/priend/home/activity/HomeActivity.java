package com.gachon.priend.home.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gachon.priend.R;
import com.gachon.priend.data.database.AnimalDatabaseHelper;
import com.gachon.priend.data.database.Database;
import com.gachon.priend.data.database.GroupDatabaseHelper;
import com.gachon.priend.data.entity.Account;
import com.gachon.priend.data.entity.Animal;
import com.gachon.priend.data.entity.Group;
import com.gachon.priend.home.EntityAdapter;
import com.gachon.priend.home.request.EntityListRequest;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.membership.NowAccountManager;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class HomeActivity extends AppCompatActivity {

    public static final String INTENT_KEY_GROUP_ID = "group_id";
    public static final String INTENT_KEY_ANIMAL_ID = "animal_id";

    private SearchView entitySearchView;
    private RecyclerView entityRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        final Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.secondaryColor));

        final Resources resources = getResources();
        final Resources.Theme theme = getTheme();

        entitySearchView = findViewById(R.id.home_search_view_entity);
        entityRecyclerView = findViewById(R.id.home_recycler_view_entity);
        SpeedDialView dialView = findViewById(R.id.home_speed_dial_view_menu);
        SpeedDialActionItem settingsDialItem = new SpeedDialActionItem.Builder(R.id.home_speed_dial_item_settings, R.drawable.ic_settings_white_24dp)
                .setFabBackgroundColor(Color.WHITE)
                .setFabImageTintColor(resources.getColor(R.color.primaryColor, theme))
                .setLabel(R.string.home_text_menu_settings)
                .setLabelClickable(false)
                .create();
        SpeedDialActionItem hospitalDialItem = new SpeedDialActionItem.Builder(R.id.home_speed_dial_item_hospital, R.drawable.ic_local_hospital_white_24dp)
                .setFabBackgroundColor(Color.WHITE)
                .setFabImageTintColor(resources.getColor(R.color.primaryColor, theme))
                .setLabel(R.string.home_text_menu_hospitals)
                .setLabelClickable(false)
                .create();
        SpeedDialActionItem communityDialItem = new SpeedDialActionItem.Builder(R.id.home_speed_dial_item_community, R.drawable.ic_format_list_bulleted_white_24dp)
                .setFabBackgroundColor(Color.WHITE)
                .setFabImageTintColor(resources.getColor(R.color.primaryColor, theme))
                .setLabel(R.string.home_text_menu_community)
                .setLabelClickable(false)
                .create();

        /*
         * Initialize GUI Components
         */
        dialView.addActionItem(communityDialItem);
        dialView.addActionItem(hospitalDialItem);
        dialView.addActionItem(settingsDialItem);
        dialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {

                switch (actionItem.getId()) {

                    case R.id.home_speed_dial_item_settings:
                        // todo: goto add settings activity

                        break;

                    case R.id.home_speed_dial_item_hospital:
                        // todo: goto add map activity

                        break;

                    case R.id.home_speed_dial_item_community:
                        // todo: goto add community activity

                        break;
                }
                return false;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        entityRecyclerView.setLayoutManager(layoutManager);

        GroupDatabaseHelper groupDb = new GroupDatabaseHelper(this);
        groupDb.getWritableDatabase();

        AnimalDatabaseHelper animalDb = new AnimalDatabaseHelper(this);
        animalDb.downloadSpecies();
    }

    @Override
    protected void onResume() {
        super.onResume();

        downloadEntities();
    }

    private void downloadEntities() {

        final Account account = NowAccountManager.getAccountOrNull(this);

        new EntityListRequest(account).request(new RequestBase.ResponseListener<EntityListRequest.EResponse>() {
            @Override
            public void onResponse(final EntityListRequest.EResponse response, final Object[] args) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        switch (response) {

                            case OK:
                                final LinkedList<Group> groups = (LinkedList<Group>) args[0];
                                final HashMap<Group, ArrayList<Animal>> entries = (HashMap<Group, ArrayList<Animal>>) args[1];

                                final GroupDatabaseHelper groupDb = new GroupDatabaseHelper(HomeActivity.this);
                                final AnimalDatabaseHelper animalDb = new AnimalDatabaseHelper(HomeActivity.this);

                                animalDb.clear();
                                groupDb.clear();

                                for (Group group : groups) {
                                    groupDb.tryWrite(group);
                                    for (Animal animal : entries.get(group)) {
                                        animalDb.tryWrite(animal);
                                        groupDb.bindAnimal(animal, group, false);
                                    }
                                }

                                final EntityAdapter adapter = new EntityAdapter(groups, entries);
                                adapter.setEventHandler(new EntityAdapter.EventHandler() {

                                    @Override
                                    public void addGroup() {
                                        final Intent intent = new Intent(HomeActivity.this, GroupActivity.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void addAnimal(@NonNull Group group) {
                                        final Intent intent = new Intent(HomeActivity.this, AnimalActivity.class);
                                        intent.putExtra(INTENT_KEY_GROUP_ID, group.getId());

                                        startActivity(intent);
                                    }

                                    @Override
                                    public void editAnimal(@NonNull Animal animal) {
                                        final Intent intent = new Intent(HomeActivity.this, AnimalActivity.class);
                                        intent.putExtra(INTENT_KEY_ANIMAL_ID, animal.getId());

                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onAnimalClicked(@NonNull Animal animal) {
                                        // todo: goto calendar selection activity

                                    }
                                });
                                entityRecyclerView.setAdapter(adapter);

                                Toast.makeText(HomeActivity.this, R.string.home_message_download_ok, Toast.LENGTH_SHORT).show();
                                break;

                            case ACCOUNT_ERROR:
                                Toast.makeText(HomeActivity.this, R.string.home_message_error_account, Toast.LENGTH_LONG).show();
                                break;

                            case SERVER_ERROR:
                                Toast.makeText(HomeActivity.this, R.string.home_message_error_server, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            }
        });
    }
}

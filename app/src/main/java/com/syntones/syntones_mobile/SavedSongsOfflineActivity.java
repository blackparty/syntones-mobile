package com.syntones.syntones_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.syntones.model.SavedOfflineSongs;
import com.syntones.remote.DBHelper;

import java.util.ArrayList;

public class SavedSongsOfflineActivity extends AppCompatActivity {

    private ListView SavedSongsOfflineLv;
    private ArrayList<String> saved_offline_songs_list = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_songs_offline);

        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        username = sharedPrefUserInfo.getString("username", "");

        SavedSongsOfflineLv = (ListView) findViewById(R.id.lvSavedSongsOffline);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, saved_offline_songs_list);
        SavedSongsOfflineLv.setAdapter(arrayAdapter);

        insertSavedSongsOffline();
        displaySavedSongsOffline();
    }

    public void bottomBar(View view) {
        String btnText;

        btnText = ((Button) view).getText().toString();

        if (btnText.equals("Home")) {

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);

        } else if (btnText.equals("Search")) {

            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        } else if (btnText.equals("Your Library")) {

            Intent intent = new Intent(this, YourLibraryActivity.class);
            startActivity(intent);
        }

    }

    public void insertSavedSongsOffline() {

        DBHelper db = new DBHelper(this);

        ArrayList<SavedOfflineSongs> savedOfflineSongsArrayList = db.getAllSavedOfflineSongsFromUser(username);

        Log.d("SQL size", String.valueOf(savedOfflineSongsArrayList.size()));
        for (SavedOfflineSongs a : savedOfflineSongsArrayList) {


            arrayAdapter.add(a.getSongTitle() + " by " + a.getArtistName());
            arrayAdapter.notifyDataSetChanged();


        }
    }

    public void displaySavedSongsOffline() {
        SavedSongsOfflineLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }


}

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
import android.widget.ImageView;
import android.widget.ListView;

import com.syntones.model.SavedOfflineSongs;
import com.syntones.remote.DBHelper;

import java.util.ArrayList;

public class SavedSongsOfflineActivity extends AppCompatActivity {

    private ListView SavedSongsOfflineLv;
    private ArrayList<String> saved_offline_songs_list = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private String username;
    private ImageView BackToLibIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_songs_offline);

        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        username = sharedPrefUserInfo.getString("username", "");

        SavedSongsOfflineLv = (ListView) findViewById(R.id.lvSavedSongsOffline);
        BackToLibIv = (ImageView) findViewById(R.id.ivBackToLib);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, saved_offline_songs_list);
        SavedSongsOfflineLv.setAdapter(arrayAdapter);

        insertSavedSongsOffline();
        displaySavedSongsOffline();

        BackToLibIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedSongsOfflineActivity.this, YourLibraryActivity.class);
                startActivity(intent);
            }
        });
    }


    public void insertSavedSongsOffline() {

        DBHelper db = new DBHelper(this);

        ArrayList<SavedOfflineSongs> savedOfflineSongsArrayList = db.getAllSavedOfflineSongsFromUser(username);

        Log.d("SQL size", String.valueOf(savedOfflineSongsArrayList.size()));

        final SharedPreferences sharedPrefPlayedSongInfo = getSharedPreferences("playedSongInfo", 0);
        final SharedPreferences.Editor editorPlayedSongInfo = sharedPrefPlayedSongInfo.edit();

        String[] song_ids = new String[savedOfflineSongsArrayList.size()];
        String[] song_urls = new String[savedOfflineSongsArrayList.size()];
        String[] song_titles = new String[savedOfflineSongsArrayList.size()];
        String[] song_artists = new String[savedOfflineSongsArrayList.size()];
        String[] song_lyrics = new String[savedOfflineSongsArrayList.size()];

        editorPlayedSongInfo.putInt("song_url_array" + "_size", song_urls.length);

        int b = 0;
        for (SavedOfflineSongs a : savedOfflineSongsArrayList) {

            song_ids[b] = a.getSongId();
            song_urls[b] = a.getFilePath();
            song_titles[b] = a.getSongTitle();
            song_artists[b] = a.getArtistName();
            song_lyrics[b] = a.getLyrics();


            editorPlayedSongInfo.putString("song_id_array" + "_" + b, song_ids[b]);
            editorPlayedSongInfo.putString("song_url_array" + "_" + b, song_urls[b]);
            editorPlayedSongInfo.putString("song_titles_array" + "_" + b, song_titles[b]);
            editorPlayedSongInfo.putString("song_artists_array" + "_" + b, song_artists[b]);
            editorPlayedSongInfo.putString("song_lyrics_array" + "_" + b, song_lyrics[b]);

            arrayAdapter.add(a.getSongTitle() + " by " + a.getArtistName());
            arrayAdapter.notifyDataSetChanged();

            b++;
        }

        editorPlayedSongInfo.apply();
    }

    public void displaySavedSongsOffline() {
        SavedSongsOfflineLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String song = String.valueOf(parent.getItemAtPosition(position));

                SharedPreferences sharedPrefSongInfo = getSharedPreferences("songInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorSongInfo = sharedPrefSongInfo.edit();

                String[] song_info = song.split("\\s(by)\\s");

                editorSongInfo.putString("songTitle", song_info[0]);
                editorSongInfo.putString("artistName", song_info[1]);

                editorSongInfo.apply();

                SharedPreferences sharedPrefActivityInfo = getSharedPreferences("activityInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorActivityInfo = sharedPrefActivityInfo.edit();
                editorActivityInfo.putString("activityState", "SavedOffline");
                editorActivityInfo.apply();

                Intent intent = new Intent(SavedSongsOfflineActivity.this, PlayerActivity.class);
                startActivity(intent);
            }
        });
    }


}

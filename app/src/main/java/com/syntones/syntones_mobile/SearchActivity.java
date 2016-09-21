package com.syntones.syntones_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.syntones.model.Song;
import com.syntones.remote.SyntonesWebAPI;
import com.syntones.response.SongListResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity {

    private ImageButton SearchBtn;
    private SearchActivity sContext;
    private ListView SearchResultLv;
    private ArrayAdapter<String> arrayAdapater;
    private ArrayList<String> songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchBtn = (ImageButton) findViewById(R.id.btnSearchRes);
        SearchResultLv = (ListView) findViewById(R.id.lvSearchResult);

        arrayAdapater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, songs);
        SearchResultLv.setAdapter(arrayAdapater);

        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBtn();
            }
        });

        SearchResultLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String song = String.valueOf(parent.getItemAtPosition(position));

                SharedPreferences sharedPrefSongInfo = getSharedPreferences("songInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorSongInfo = sharedPrefSongInfo.edit();

                String[] song_info = song.split("\\s(by)\\s");

                editorSongInfo.putString("songTitle", song_info[0]);
                editorSongInfo.putString("artistName", song_info[1]);

                editorSongInfo.commit();

                SharedPreferences sharedPrefActivityInfo = getSharedPreferences("activityInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorActivityInfo = sharedPrefActivityInfo.edit();
                editorActivityInfo.putString("activityState", "SearchActivity");
                editorActivityInfo.commit();

                Intent intent = new Intent(SearchActivity.this, SongInfoActivity.class);
                startActivity(intent);

            }
        });
    }

    public void searchBtn() {

        final SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

        syntonesWebAPI.getAllSongsFromDB().enqueue(new Callback<SongListResponse>() {
            @Override
            public void onResponse(Call<SongListResponse> call, Response<SongListResponse> response) {

                SongListResponse songListResponse = response.body();
                List<Song> songList = songListResponse.getSongs();
                for (Song s : songList) {
                    arrayAdapater.add(s.getSongTitle() + "\nby " + s.getArtist().getArtistName());
                    arrayAdapater.notifyDataSetChanged();

                }


                Log.e("Song List Response:", songListResponse.getMessage().getMessage());
            }


            @Override
            public void onFailure(Call<SongListResponse> call, Throwable t) {


                Log.e("Failed", String.valueOf(t.getMessage()));

            }
        });

    }

    public void bottomBar(View view) {
        String btnText;

        btnText = ((Button) view).getText().toString();

        if (btnText.equals("Home")) {

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);

        } else if (btnText.equals("Your Library")) {

            Intent intent = new Intent(this, YourLibraryActivity.class);
            startActivity(intent);
        }

    }
}

package com.syntones.syntones_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.syntones.model.Song;
import com.syntones.remote.SyntonesWebAPI;
import com.syntones.response.SongListResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSongsActivity extends AppCompatActivity {
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> songs_list = new ArrayList<>();
    private ArrayList<String> selected_list = new ArrayList<>();
    private ListView ListOfSongsLv;
    private Button AddAllSongsBtn, CancelAddSongsBtn;
    private AddSongsActivity sContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_songs);

        SharedPreferences sharedPrefEditSong = getSharedPreferences("editInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorEditSong = sharedPrefEditSong.edit();

        editorEditSong.putBoolean("editStatus", true);
        editorEditSong.apply();

        ListOfSongsLv = (ListView) findViewById(R.id.lvListOfSongs);
        AddAllSongsBtn = (Button) findViewById(R.id.btnAddAllSongs);
        CancelAddSongsBtn = (Button) findViewById(R.id.btnCancelAddSongs);


        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, songs_list);
        ListOfSongsLv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ListOfSongsLv.setAdapter(arrayAdapter);

        insertSongList();
        displaySongList();

        AddAllSongsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        CancelAddSongsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddSongsActivity.this, ViewPlayListActivity.class);
                startActivity(intent);
            }
        });
    }

    public void insertSongList() {

        SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

        syntonesWebAPI.getAllSongsFromDB().enqueue(new Callback<SongListResponse>() {
            @Override
            public void onResponse(Call<SongListResponse> call, Response<SongListResponse> response) {
                SongListResponse songListResponse = response.body();
                List<Song> songList = songListResponse.getSongs();

                for(Song a : songList){
                    arrayAdapter.add(a.getSongTitle() + " by " + a.getArtist().getArtistName());
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<SongListResponse> call, Throwable t) {

            }
        });

    }

    public void displaySongList(){
        ListOfSongsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SparseBooleanArray checked = ListOfSongsLv.getCheckedItemPositions();


                for (int a = 0; a < checked.size(); a++) {
                    position = checked.keyAt(a);

                    if (checked.valueAt(a)) {

                        selected_list.add(arrayAdapter.getItem(position));
                    }


                }

            }
        });
    }


}

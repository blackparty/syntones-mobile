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
import android.widget.EditText;
import android.widget.ListView;

import com.syntones.model.Playlist;
import com.syntones.model.Song;
import com.syntones.model.Tag;
import com.syntones.model.User;
import com.syntones.remote.SyntonesWebAPI;
import com.syntones.response.GeneratePlaylistResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenerateBySongsActivity extends AppCompatActivity {

    private EditText GeneratedPlaylistNameEt;
    private Button SaveGeneratedPlaylistBtn, CancelGeneratedPlaylistBtn;
    private ListView GeneratedSongsByLv;
    private ArrayList<String> songs_by_artist_list = new ArrayList<>();
    private ArrayList<String> songs_by_tag_list = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private GenerateBySongsActivity sContext;
    private String artistName, tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_by_songs);

        GeneratedPlaylistNameEt = (EditText) findViewById(R.id.etGeneratedPlaylistName);
        SaveGeneratedPlaylistBtn = (Button) findViewById(R.id.btnSaveGeneratedPlaylist);
        CancelGeneratedPlaylistBtn = (Button) findViewById(R.id.btnCancelGeneratedPlaylist);
        GeneratedSongsByLv = (ListView) findViewById(R.id.lvGeneratedSongsBy);

        Bundle extras = getIntent().getExtras();
        final String generateBy = extras.get("generateBy").toString();


        if (generateBy.equals("Artists")) {

           artistName = extras.get("artistInfo").toString();
            Log.d("ARTIST NAME", artistName);
            insertGeneratedSongsByArtist(artistName);

            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, songs_by_artist_list);
            GeneratedSongsByLv.setAdapter(arrayAdapter);

        } else if (generateBy.equals("Tags")) {

            tag = extras.get("tagInfo").toString();
            insertGeneratedSongsByTags(tag);

            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, songs_by_tag_list);
            GeneratedSongsByLv.setAdapter(arrayAdapter);

        }

        this.displayGeneratedSongs();


        SaveGeneratedPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(artistName!=null){
                    tag="none";
                }else{
                    artistName="none";
                }
                saveGeneratedPlaylist(generateBy, artistName, tag);
            }
        });

        CancelGeneratedPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelGeneratedPlaylist();
            }
        });
    }

    public void insertGeneratedSongsByArtist(String artistName) {

        SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

        syntonesWebAPI.generatePlaylistByArtist(artistName).enqueue(new Callback<GeneratePlaylistResponse>() {
            @Override
            public void onResponse(Call<GeneratePlaylistResponse> call, Response<GeneratePlaylistResponse> response) {
                GeneratePlaylistResponse generatePlaylistResponse = response.body();
                List<Song> songList = generatePlaylistResponse.getSongs();

                for (Song a : songList) {
                    arrayAdapter.add(a.getSongTitle() + " by " + a.getArtist().getArtistName());
                    arrayAdapter.notifyDataSetChanged();
                }

                Log.e("GP Response", generatePlaylistResponse.getMessage().getMessage());

            }

            @Override
            public void onFailure(Call<GeneratePlaylistResponse> call, Throwable t) {

            }
        });
    }

    public void displayGeneratedSongs() {

        GeneratedSongsByLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GenerateBySongsActivity.this, PlayListActivity.class);
                startActivity(intent);
            }
        });
    }

    public void insertGeneratedSongsByTags(String tag) {

        SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

        Tag tagObject = new Tag();
        tagObject.setTag(tag);
        syntonesWebAPI.generatePlaylistByTags(tagObject).enqueue(new Callback<GeneratePlaylistResponse>() {
            @Override
            public void onResponse(Call<GeneratePlaylistResponse> call, Response<GeneratePlaylistResponse> response) {
                GeneratePlaylistResponse generatePlaylistResponse = response.body();
                List<Song> songList = generatePlaylistResponse.getSongs();

                for (Song a : songList) {
                    arrayAdapter.add(a.getSongTitle() + " by " + a.getArtist().getArtistName());
                    arrayAdapter.notifyDataSetChanged();
                }

//                Log.e("GP Response", generatePlaylistResponse.getMessage().getMessage());

            }

            @Override
            public void onFailure(Call<GeneratePlaylistResponse> call, Throwable t) {

            }
        });
    }


    public void saveGeneratedPlaylist(String generateBy, String artistName, String tag) {

        final SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);
        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String username = sharedPrefUserInfo.getString("username", "");
        final User user = new User();
        user.setUsername(username);
        final Tag tagObject = new Tag();
        tagObject.setTag(tag);
        if (generateBy.equals("Artists")) {

            syntonesWebAPI.generatePlaylistByArtist(artistName).enqueue(new Callback<GeneratePlaylistResponse>() {
                @Override
                public void onResponse(Call<GeneratePlaylistResponse> call, Response<GeneratePlaylistResponse> response) {
                    GeneratePlaylistResponse generatePlaylistResponse = response.body();
                    List<Song> songList = generatePlaylistResponse.getSongs();
                    ArrayList<Song> generatedSongList = new ArrayList<>();


                    Playlist playlist = new Playlist();
                    playlist.setUser(user);
                    playlist.setPlaylistName(GeneratedPlaylistNameEt.getText().toString());

                    for (Song a : songList) {
                        Song song = new Song();
                        song.setSongId(a.getSongId());
                        song.setSongTitle(a.getSongTitle());
                        song.setArtistName(a.getArtist().getArtistName());
                        song.setLyrics(a.getLyrics());
                        generatedSongList.add(song);

                    }

                    playlist.setSongs(generatedSongList);

                    syntonesWebAPI.saveGeneratedPlaylist(playlist).enqueue(new Callback<GeneratePlaylistResponse>() {
                        @Override
                        public void onResponse(Call<GeneratePlaylistResponse> call, Response<GeneratePlaylistResponse> response) {
                            Intent intent = new Intent(GenerateBySongsActivity.this, PlayListActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<GeneratePlaylistResponse> call, Throwable t) {

                        }
                    });


                    Log.e("GP Response", generatePlaylistResponse.getMessage().getMessage());

                }

                @Override
                public void onFailure(Call<GeneratePlaylistResponse> call, Throwable t) {

                }
            });
        } else if (generateBy.equals("Tags")) {
            syntonesWebAPI.generatePlaylistByTags(tagObject).enqueue(new Callback<GeneratePlaylistResponse>() {
                @Override
                public void onResponse(Call<GeneratePlaylistResponse> call, Response<GeneratePlaylistResponse> response) {
                    GeneratePlaylistResponse generatePlaylistResponse = response.body();
                    List<Song> songList = generatePlaylistResponse.getSongs();
                    ArrayList<Song> generatedSongList = new ArrayList<>();
                    Playlist playlist = new Playlist();
                    playlist.setUser(user);
                    playlist.setPlaylistName(GeneratedPlaylistNameEt.getText().toString());

                    for (Song a : songList) {
                        Song song = new Song();
                        song.setSongId(a.getSongId());
                        song.setSongTitle(a.getSongTitle());
                        song.setArtistName(a.getArtist().getArtistName());
                        song.setLyrics(a.getLyrics());

                        generatedSongList.add(song);
                    }

                    playlist.setSongs(generatedSongList);

                    syntonesWebAPI.saveGeneratedPlaylist(playlist).enqueue(new Callback<GeneratePlaylistResponse>() {
                        @Override
                        public void onResponse(Call<GeneratePlaylistResponse> call, Response<GeneratePlaylistResponse> response) {
                            Intent intent = new Intent(GenerateBySongsActivity.this, PlayListActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<GeneratePlaylistResponse> call, Throwable t) {

                        }
                    });
//                    Log.e("GP Response", generatePlaylistResponse.getMessage().getMessage());

                }

                @Override
                public void onFailure(Call<GeneratePlaylistResponse> call, Throwable t) {

                }
            });
        }

    }

    public void cancelGeneratedPlaylist() {

        Intent intent = new Intent(GenerateBySongsActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}

package com.syntones.syntones_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.syntones.model.Playlist;
import com.syntones.model.PlaylistSong;
import com.syntones.model.Song;
import com.syntones.model.User;
import com.syntones.remote.SyntonesWebAPI;
import com.syntones.response.PlaylistSongsResponse;
import com.syntones.response.RemovePlaylistResponse;
import com.syntones.response.SongListResponse;

import java.util.ArrayList;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPlayListActivity extends AppCompatActivity {

    private TextView PlaylistNameTv;
    private ListView ViewPlaylistLv;
    private Button EditSongBtn, RemoveSongBtn;
    private ViewPlayListActivity sContext;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_play_list);

        PlaylistNameTv = (TextView) findViewById(R.id.tvPlaylistName);
        ViewPlaylistLv = (ListView) findViewById(R.id.lvViewPlaylist);
        EditSongBtn = (Button) findViewById(R.id.btnEditSong);
        RemoveSongBtn = (Button) findViewById(R.id.btnRemoveSong);

        SharedPreferences sharedPrefPlaylistInfo = getSharedPreferences("playlistInfo", Context.MODE_PRIVATE);
        String playlist_name = sharedPrefPlaylistInfo.getString("playlistName", "");

        PlaylistNameTv.setText(playlist_name);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, songs);
        ViewPlaylistLv.setAdapter(arrayAdapter);


        insertSongList();


        displayViewSongList();


        EditSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSong(v);
            }
        });

        RemoveSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSong(v);
            }
        });

    }


    public void editSong(View view) {

        if (EditSongBtn.getText().equals("Edit")) {
            displaySongList();
            EditSongBtn.setText("Done");
            RemoveSongBtn.setVisibility(view.VISIBLE);
        } else if (EditSongBtn.getText().equals("Done")) {
            displayViewSongList();
            EditSongBtn.setText("Edit");
            RemoveSongBtn.setVisibility(view.INVISIBLE);
        }

    }

    public void displayViewSongList() {
        ViewPlaylistLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                editorActivityInfo.putString("activityState", "Playlist");
                editorActivityInfo.apply();

                Intent intent = new Intent(ViewPlayListActivity.this, PlayerActivity.class);
                startActivity(intent);
                Toast.makeText(getBaseContext(), song, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void displaySongList() {
        ViewPlaylistLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    public void removeSong(View view) {
        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences sharedPrefPlaylistInfo = getSharedPreferences("playlistInfo", Context.MODE_PRIVATE);

        final String playlist_id = sharedPrefPlaylistInfo.getString("playlistId", "");
        String username = sharedPrefUserInfo.getString("username", "");
        final User user = new User();
        user.setUsername(username);
        Map<String, String> data = new HashMap<>();

        data.put("id", playlist_id);
        data.put("username", username);
        final SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);
        int position = ViewPlaylistLv.getCheckedItemPosition();
        if (position > -1) {
            final String[] song = String.valueOf(ViewPlaylistLv.getItemAtPosition(position)).split("\\s(by)\\s");

            syntonesWebAPI.getSongsPlaylist(data).enqueue(new Callback<PlaylistSongsResponse>() {
                @Override
                public void onResponse(Call<PlaylistSongsResponse> call, Response<PlaylistSongsResponse> response) {
                    final PlaylistSongsResponse playlistSongsResponse = response.body();
                    final Playlist playlist = new Playlist();
                    playlist.setPlaylistId(Long.parseLong(playlist_id));
                    playlist.setSongs(playlistSongsResponse.getPlaylist().getSongs());

                    for (Song a : playlist.getSongs()) {
                        if (a.getSongTitle().equals(song[0]) && a.getArtist().getArtistName().equals(song[1])) {

                            PlaylistSong playlistSong = new PlaylistSong();
                            playlistSong.setPlaylistId(Long.parseLong(playlist_id));
                            playlistSong.setSongId(a.getSongId());
                            playlistSong.setUser(user);
                            syntonesWebAPI.removeSongFromPlaylist(playlistSong).enqueue(new Callback<RemovePlaylistResponse>() {
                                @Override
                                public void onResponse(Call<RemovePlaylistResponse> call, Response<RemovePlaylistResponse> response) {

                                    RemovePlaylistResponse removePlaylistResponse = response.body();

                                    Log.e("Remove Playlist ", removePlaylistResponse.getMessage().getMessage());
                                }

                                @Override
                                public void onFailure(Call<RemovePlaylistResponse> call, Throwable t) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<PlaylistSongsResponse> call, Throwable t) {

                }
            });

            arrayAdapter.remove(songs.get(position));


            arrayAdapter.notifyDataSetChanged();
        }
    }

    public void insertSongList() {

        final SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);
        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences sharedPrefPlaylistInfo = getSharedPreferences("playlistInfo", Context.MODE_PRIVATE);

        final String playlist_id = sharedPrefPlaylistInfo.getString("playlistId", "");
        String username = sharedPrefUserInfo.getString("username", "");

        Map<String, String> data = new HashMap<>();

        data.put("id", playlist_id);
        data.put("username", username);

        final SharedPreferences sharedPrefPlayedSongInfo = getSharedPreferences("playedSongInfo", 0);
        final SharedPreferences.Editor editorPlayedSongInfo = sharedPrefPlayedSongInfo.edit();
        editorPlayedSongInfo.clear();
        editorPlayedSongInfo.apply();

        syntonesWebAPI.getSongsPlaylist(data).enqueue(new Callback<PlaylistSongsResponse>() {
            @Override
            public void onResponse(Call<PlaylistSongsResponse> call, Response<PlaylistSongsResponse> response) {

                final PlaylistSongsResponse playlistSongsResponse = response.body();
                final Playlist playlist = new Playlist();


                syntonesWebAPI.getAllSongsFromDB().enqueue(new Callback<SongListResponse>() {
                    @Override
                    public void onResponse(Call<SongListResponse> call, Response<SongListResponse> response) {

                        SongListResponse songListResponse = response.body();


                        playlist.setPlaylistId(Long.parseLong(playlist_id));
                        playlist.setSongs(playlistSongsResponse.getPlaylist().getSongs());
                        String[] song_ids = new String[playlist.getSongs().size()];
                        String[] song_urls = new String[playlist.getSongs().size()];
                        String[] song_titles = new String[playlist.getSongs().size()];
                        String[] song_artists = new String[playlist.getSongs().size()];
                        String[] song_lyrics = new String[playlist.getSongs().size()];
                        editorPlayedSongInfo.putInt("song_url_array" + "_size", song_urls.length);

                        int b = 0;
                        for (Song a : playlist.getSongs()) {
                            song_ids[b] = String.valueOf(a.getSongId());
                            song_urls[b] = String.valueOf(a.getFilePath());
                            song_titles[b] = a.getSongTitle();
                            song_artists[b] = a.getArtist().getArtistName();
                            song_lyrics[b] = a.getLyrics();

                            editorPlayedSongInfo.putString("song_id_array" + "_" + b, song_ids[b]);
                            editorPlayedSongInfo.putString("song_url_array" + "_" + b, song_urls[b]);
                            editorPlayedSongInfo.putString("song_titles_array" + "_" + b, song_titles[b]);
                            editorPlayedSongInfo.putString("song_artists_array" + "_" + b, song_artists[b]);
                            editorPlayedSongInfo.putString("song_lyrics_array" + "_" + b, song_lyrics[b]);

                            arrayAdapter.add(a.getSongTitle() + "\nby " + a.getArtist().getArtistName());
                            arrayAdapter.notifyDataSetChanged();
                            b++;
                        }
                        editorPlayedSongInfo.apply();
                        playlistSongsResponse.setPlaylist(playlist);

                        Log.e("Song List Response:", songListResponse.getMessage().getMessage());
                    }


                    @Override
                    public void onFailure(Call<SongListResponse> call, Throwable t) {


                        Log.e("Failed", String.valueOf(t.getMessage()));

                    }
                });


            }

            @Override
            public void onFailure(Call<PlaylistSongsResponse> call, Throwable t) {

            }
        });

    }

}

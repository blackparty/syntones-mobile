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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.syntones.model.Playlist;
import com.syntones.model.Song;
import com.syntones.remote.SyntonesWebAPI;
import com.syntones.response.PlaylistSongsResponse;
import com.syntones.response.SongListResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPlayListActivity extends AppCompatActivity {

    TextView PlaylistNameTv;
    ListView ViewPlaylistLv;
    private ViewPlayListActivity sContext;

    private ArrayAdapter<String> arrayAdapater;
    private ArrayList<String> songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_play_list);

        PlaylistNameTv = (TextView) findViewById(R.id.tvPlaylistName);
        ViewPlaylistLv = (ListView) findViewById(R.id.lvViewPlaylist);

        SharedPreferences sharedPrefPlaylistInfo = getSharedPreferences("playlistInfo", Context.MODE_PRIVATE);
        String playlist_name = sharedPrefPlaylistInfo.getString("playlistName", "");

        PlaylistNameTv.setText(playlist_name);

        arrayAdapater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, songs);
        ViewPlaylistLv.setAdapter(arrayAdapater);

        this.displaySongList();

        ViewPlaylistLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                editorActivityInfo.putString("activityState", "Playlist");
                editorActivityInfo.commit();

                Intent intent = new Intent(ViewPlayListActivity.this, SongInfoActivity.class);
                startActivity(intent);
                Toast.makeText(getBaseContext(), song, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void displaySongList() {

        final SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);
        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences sharedPrefPlaylistInfo = getSharedPreferences("playlistInfo", Context.MODE_PRIVATE);

        final String playlist_id = sharedPrefPlaylistInfo.getString("playlistId", "");
        String username = sharedPrefUserInfo.getString("username", "");

        Map<String, String> data = new HashMap<>();

        data.put("id", playlist_id);
        data.put("username", username);

        syntonesWebAPI.getSongsPlaylist(data).enqueue(new Callback<PlaylistSongsResponse>() {
            @Override
            public void onResponse(Call<PlaylistSongsResponse> call, Response<PlaylistSongsResponse> response) {

                final PlaylistSongsResponse playlistSongsResponse = response.body();
                final Playlist playlist = new Playlist();


                syntonesWebAPI.getAllSongsFromDB().enqueue(new Callback<SongListResponse>() {
                    @Override
                    public void onResponse(Call<SongListResponse> call, Response<SongListResponse> response) {

                        SongListResponse songListResponse = response.body();
                        List<Song> songList = songListResponse.getSongs();


                        playlist.setPlaylistId(Long.parseLong(playlist_id));
                        playlist.setSongs(playlistSongsResponse.getPlaylist().getSongs());

                        for (Song a : playlist.getSongs()) {

                            arrayAdapater.add(a.getSongTitle() + "\nby" + a.getArtist().getArtistName());
                            arrayAdapater.notifyDataSetChanged();
                        }
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

package com.syntones.syntones_mobile;

import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.syntones.model.Playlist;
import com.syntones.model.PlaylistSong;
import com.syntones.model.User;
import com.syntones.remote.SyntonesWebAPI;
import com.syntones.response.LibraryResponse;
import com.syntones.response.PlaylistResponse;
import com.syntones.response.RemovePlaylistResponse;
import com.syntones.response.RemoveToPlaylistResponse;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayListActivity extends AppCompatActivity {
    private PlayListActivity sContext;
    ArrayList<String> play_lists = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    EditText PlayListNameEt;
    ListView PlaylistsLv;
    Button RemoveBtn, EditBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        PlaylistsLv = (ListView) findViewById(R.id.lvPlaylists);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, play_lists);
        PlaylistsLv.setAdapter(arrayAdapter);
        RemoveBtn = (Button) findViewById(R.id.btnRemove);
        EditBtn = (Button) findViewById(R.id.btnEdit);

        this.displayPlaylist();


        SharedPreferences sharedPrefButtonInfo = getSharedPreferences("buttonInfo", Context.MODE_PRIVATE);
        String buttonStatus = sharedPrefButtonInfo.getString("buttonStatus", "");
        Bundle extras = getIntent().getExtras();

        if (buttonStatus.equals("addToPlaylist") && extras != null) {

            final long songId = Long.parseLong(extras.get("SongId").toString());

            PlaylistsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    play_lists.get(position);

                    final SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);
                    SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    final String playlist_name = String.valueOf(PlaylistsLv.getItemAtPosition(position));
                    String username = sharedPrefUserInfo.getString("username", "");
                    final User user = new User();
                    user.setUsername(username);
                    syntonesWebAPI.getPlaylistFromDB(user).enqueue(new Callback<PlaylistResponse>() {
                        @Override
                        public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {


                            PlaylistResponse playlistResponse = response.body();
                            List<Playlist> playlists = playlistResponse.getPlaylists();

                            for (Playlist a : playlists) {

                                if (a.getPlaylistName().equals(playlist_name)) {

                                    PlaylistSong playlistSong = new PlaylistSong();
                                    playlistSong.setSongId(songId);
                                    playlistSong.setPlaylistId(a.getPlaylistId());
                                    playlistSong.setUser(user);

                                    syntonesWebAPI.addToPlaylist(playlistSong);

                                    SyntonesWebAPI.Factory.getInstance(sContext).addToPlaylist(playlistSong).enqueue(new Callback<LibraryResponse>() {
                                        @Override
                                        public void onResponse(Call<LibraryResponse> call, Response<LibraryResponse> response) {
                                            LibraryResponse libraryResponse = response.body();

                                            Intent intent = new Intent(PlayListActivity.this, SongInfoActivity.class);

                                            startActivity(intent);
                                            Log.e("Library Response: ", libraryResponse.getMessage().getMessage());
                                        }

                                        @Override
                                        public void onFailure(Call<LibraryResponse> call, Throwable t) {

                                        }
                                    });


                                }
                            }

                            Log.e("Playlist Response: ", playlistResponse.getMessage().getMessage());
                        }

                        @Override
                        public void onFailure(Call<PlaylistResponse> call, Throwable t) {

                        }
                    });


                }
            });
        } else {

            PlaylistsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    play_lists.get(position);

                    final SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);
                    SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    final String playlist_name = String.valueOf(PlaylistsLv.getItemAtPosition(position));
                    String username = sharedPrefUserInfo.getString("username", "");
                    final User user = new User();
                    user.setUsername(username);
                    syntonesWebAPI.getPlaylistFromDB(user).enqueue(new Callback<PlaylistResponse>() {
                        @Override
                        public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {


                            PlaylistResponse playlistResponse = response.body();
                            List<Playlist> playlists = playlistResponse.getPlaylists();

                            for (Playlist a : playlists) {

                                if (a.getPlaylistName().equals(playlist_name)) {
                                    Intent intent = new Intent(PlayListActivity.this, ViewPlayListActivity.class);
                                    SharedPreferences sharedPrefPlaylistInfo = getSharedPreferences("playlistInfo", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editorPlaylistInfo = sharedPrefPlaylistInfo.edit();
                                    editorPlaylistInfo.putString("playlistName", playlist_name);
                                    editorPlaylistInfo.putString("playlistId", String.valueOf(a.getPlaylistId()));
                                    editorPlaylistInfo.commit();
                                    startActivity(intent);
                                }
                            }


                            Log.e("Playlist Response: ", playlistResponse.getMessage().getMessage());
                        }

                        @Override
                        public void onFailure(Call<PlaylistResponse> call, Throwable t) {

                        }
                    });


                }
            });


        }


        RemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePlayList();
            }
        });

    }

    public void displayPlaylist() {

        final SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);
        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        String username = sharedPrefUserInfo.getString("username", "");
        User user = new User();
        user.setUsername(username);
        syntonesWebAPI.getPlaylistFromDB(user).enqueue(new Callback<PlaylistResponse>() {
            @Override
            public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {


                PlaylistResponse playlistResponse = response.body();
                List<Playlist> playlists = playlistResponse.getPlaylists();

                if (playlists != null) {
                    for (Playlist a : playlists) {

                        arrayAdapter.add(a.getPlaylistName());
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
                Log.e("Playlist Response: ", playlistResponse.getMessage().getMessage());
            }

            @Override
            public void onFailure(Call<PlaylistResponse> call, Throwable t) {

            }
        });

    }

    public void editPlaylist(View view) {

        String btnText;
        Button editBtn, addBtn, removeBtn;

        btnText = ((Button) view).getText().toString();
        editBtn = (Button) findViewById(R.id.btnEdit);
        addBtn = (Button) findViewById(R.id.btnAdd);
        removeBtn = (Button) findViewById(R.id.btnRemove);

        if (btnText.equals("Edit")) {
            editBtn.setText("Done");
            addBtn.setVisibility(View.VISIBLE);
            removeBtn.setVisibility(View.VISIBLE);
        } else if (btnText.equals("Done")) {

            editBtn.setText("Edit");
            addBtn.setVisibility(View.INVISIBLE);
            removeBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void popUpAdd(View view) {

        final Dialog dialog = new Dialog(PlayListActivity.this);
        dialog.setTitle("New Playlist");
        dialog.setContentView(R.layout.add_play_list_dialog);
        dialog.show();

        final EditText PlayListNameEt = (EditText) dialog.findViewById(R.id.etPlayListName);
        Button AddPlayListBtn = (Button) dialog.findViewById(R.id.btnAddPlaylist);
        Button CancelBtn = (Button) dialog.findViewById(R.id.btnCancel);


        AddPlayListBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String play_list_name = PlayListNameEt.getText().toString();

                if (!play_list_name.isEmpty() && play_list_name.length() > 0) {

                    SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

                    Playlist playlist = new Playlist();

                    SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

                    String username = sharedPrefUserInfo.getString("username", "");

                    User user = new User(username);


                    playlist.setUser(user);
                    playlist.setPlaylistName(play_list_name);

                    syntonesWebAPI.createPlaylist(playlist);

                    SyntonesWebAPI.Factory.getInstance(sContext).createPlaylist(playlist).enqueue(new Callback<PlaylistResponse>() {

                        @Override
                        public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {

                            PlaylistResponse playlistResponse = response.body();

                            Log.e("Playlist Response: ", String.valueOf(playlistResponse.getMessage().getMessage()));
                        }

                        @Override
                        public void onFailure(Call<PlaylistResponse> call, Throwable t) {
                            Log.e("Failed", t.getMessage());
                        }
                    });
                    arrayAdapter.add(play_list_name);
                    arrayAdapter.notifyDataSetChanged();

                }
                dialog.cancel();
            }
        });
        CancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });


    }

    public void deletePlayList() {

        int position = PlaylistsLv.getCheckedItemPosition();
        if (position > -1) {
            final String playlist_name = String.valueOf(PlaylistsLv.getItemAtPosition(position));
            SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

            String username = sharedPrefUserInfo.getString("username", "");
            final User user = new User();
            user.setUsername(username);

            final SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

            syntonesWebAPI.getPlaylistFromDB(user).enqueue(new Callback<PlaylistResponse>() {
                @Override
                public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {


                    PlaylistResponse playlistResponse = response.body();
                    List<Playlist> playlists = playlistResponse.getPlaylists();

                    for (Playlist a : playlists) {

                        if (a.getPlaylistName().equals(playlist_name)) {
                            Playlist playlist = new Playlist();
                            playlist.setPlaylistId(a.getPlaylistId());
                            playlist.setUser(user);


                            syntonesWebAPI.removePlaylist(playlist);

                            SyntonesWebAPI.Factory.getInstance(sContext).removePlaylist(playlist).enqueue(new Callback<RemovePlaylistResponse>() {
                                @Override
                                public void onResponse(Call<RemovePlaylistResponse> call, Response<RemovePlaylistResponse> response) {

                                }

                                @Override
                                public void onFailure(Call<RemovePlaylistResponse> call, Throwable t) {

                                }
                            });
                        }
                    }

                    Log.e("Playlist Response: ", playlistResponse.getMessage().getMessage());
                }

                @Override
                public void onFailure(Call<PlaylistResponse> call, Throwable t) {

                }
            });


            arrayAdapter.remove(play_lists.get(position));


            arrayAdapter.notifyDataSetChanged();

        }
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
}

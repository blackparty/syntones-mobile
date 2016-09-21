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
import android.widget.TextView;
import android.widget.Toast;

import com.syntones.model.Playlist;
import com.syntones.model.User;
import com.syntones.remote.SyntonesWebAPI;
import com.syntones.response.PlaylistResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    TextView ProfUsernameTv;
    ListView ProfPlaylistLv;
    ArrayList<String> play_lists = new ArrayList<>();
    private ProfileActivity sContext;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProfUsernameTv = (TextView) findViewById(R.id.tvProfUsername);
        ProfPlaylistLv = (ListView) findViewById(R.id.lvProfPlaylists);

        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        String username = sharedPrefUserInfo.getString("username", "");

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, play_lists);
        ProfPlaylistLv.setAdapter(arrayAdapter);
        ProfUsernameTv.setText(username);
        displayPlaylist(username);


        ProfPlaylistLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                play_lists.get(position);

                final SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);
                SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                final String playlist_name = String.valueOf(ProfPlaylistLv.getItemAtPosition(position));
                String username = sharedPrefUserInfo.getString("username", "");
                final User user = new User();
                user.setUsername(username);
                syntonesWebAPI.getPlaylistFromDB(user).enqueue(new Callback<PlaylistResponse>() {
                    @Override
                    public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {


                        PlaylistResponse playlistResponse = response.body();
                        List<Playlist> playlists = playlistResponse.getPlaylists();

                        for (int a = 0; a < 4; a++) {

                            if (playlists.get(a).getPlaylistName().equals(playlist_name)) {
                                Intent intent = new Intent(ProfileActivity.this, ViewPlayListActivity.class);
                                SharedPreferences sharedPrefPlaylistInfo = getSharedPreferences("playlistInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editorPlaylistInfo = sharedPrefPlaylistInfo.edit();
                                editorPlaylistInfo.putString("playlistName", playlist_name);
                                editorPlaylistInfo.putString("playlistId", String.valueOf(playlists.get(a).getPlaylistId()));
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


    public void viewAllPlaylists(View view) {

        Intent intent = new Intent(this, PlayListActivity.class);
        startActivity(intent);
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

    public void displayPlaylist(String username) {

        final SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

        User user = new User();
        user.setUsername(username);
        syntonesWebAPI.getPlaylistFromDB(user).enqueue(new Callback<PlaylistResponse>() {
            @Override
            public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {


                PlaylistResponse playlistResponse = response.body();
                List<Playlist> playlists = playlistResponse.getPlaylists();

                for (int a = 0; a < 4; a++) {

                    arrayAdapter.add(playlists.get(a).getPlaylistName());
                    arrayAdapter.notifyDataSetChanged();
                }

                Log.e("Playlist Response: ", playlistResponse.getMessage().getMessage());
            }

            @Override
            public void onFailure(Call<PlaylistResponse> call, Throwable t) {

            }
        });

    }

    public void logOut(View view) {
        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorUserInfo = sharedPrefUserInfo.edit();

        editorUserInfo.clear();
        editorUserInfo.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

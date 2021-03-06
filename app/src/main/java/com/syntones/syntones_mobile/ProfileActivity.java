package com.syntones.syntones_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.syntones.model.Playlist;
import com.syntones.model.User;
import com.syntones.remote.SyntonesWebAPI;
import com.syntones.response.LogoutResponse;
import com.syntones.response.PlaylistResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView ProfUsernameTv;
    private Button MakeMeAPlaylistBtn;
    private ProfileActivity sContext;
    private ImageView SearchIv, YourLibraryIv;
    private static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProfUsernameTv = (TextView) findViewById(R.id.tvProfUsername);
        MakeMeAPlaylistBtn = (Button) findViewById(R.id.btnMakeMeAPlaylist);
        SearchIv = (ImageView) findViewById(R.id.ivSearch);
        YourLibraryIv = (ImageView) findViewById(R.id.ivYourLibrary);

        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        String username = sharedPrefUserInfo.getString("username", "");

        ProfUsernameTv.setText(username);




        MakeMeAPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeMeAPlaylistBtn();
            }
        });

        SearchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        YourLibraryIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, YourLibraryActivity.class);
                startActivity(intent);
            }
        });

    }

    public void makeMeAPlaylistBtn() {

        Intent intent = new Intent(this, GeneratePlaylistMenuActivity.class);
        startActivity(intent);


    }


    public void logOut(View view) {

        PlayerActivity playerActivity = new PlayerActivity();
        playerActivity.stopPlaying();

        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorUserInfo = sharedPrefUserInfo.edit();


        editorUserInfo.clear();
        editorUserInfo.apply();

        SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

        syntonesWebAPI.logout();

        SyntonesWebAPI.Factory.getInstance(sContext).logout().enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {

            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {

            }
        });

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

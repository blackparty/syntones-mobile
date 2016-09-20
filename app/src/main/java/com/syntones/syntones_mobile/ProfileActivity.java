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
    ArrayAdapter<String> arrayAdapater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProfUsernameTv = (TextView) findViewById(R.id.tvProfUsername);
        ProfPlaylistLv = (ListView) findViewById(R.id.lvProfPlaylists);

        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        String username = sharedPrefUserInfo.getString("username", "");
        ProfUsernameTv.setText(username);


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

    public void logOut(View view) {
        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorUserInfo = sharedPrefUserInfo.edit();

        editorUserInfo.clear();
        editorUserInfo.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

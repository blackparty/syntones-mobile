package com.syntones.syntones_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.syntones.model.Product;
import com.syntones.model.User;

import org.w3c.dom.Text;

public class YourLibraryActivity extends AppCompatActivity {

    private ImageView SearchIv, LibraryIv, ViewPlaylistsIv, ViewSavedSongsOfflineIv, UserIv;
    private TextView UsernameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_library);

        ViewPlaylistsIv = (ImageView) findViewById(R.id.ivViewPlaylists);
        ViewSavedSongsOfflineIv = (ImageView) findViewById(R.id.ivViewSavedSongsOffline);
        UserIv = (ImageView) findViewById(R.id.ivUser);
        SearchIv = (ImageView) findViewById(R.id.ivSearch);
        LibraryIv = (ImageView) findViewById(R.id.ivLibrary);
        UsernameTv = (TextView) findViewById(R.id.tvUsername);

        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String username = sharedPrefUserInfo.getString("username", "");
        UsernameTv.setText(username);

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
            Log.d("CONNECTION YL", "TRUE");
            Log.d("CONNECTION YL", "WIFI " + wifiInfo.isConnected());
            Log.d("CONNECTION YL", "MOBILE " + mobileInfo.isConnected());

        } else {
            Log.d("CONNECTION YL", "FALSE");
            Log.d("CONNECTION YL", "WIFI " + wifiInfo.isConnected());
            Log.d("CONNECTION YL", "MOBILE " + mobileInfo.isConnected());

        }

        SearchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(YourLibraryActivity.this, SearchActivity.class);
                startActivity(intent);

            }
        });

        ViewPlaylistsIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YourLibraryActivity.this, PlayListActivity.class);
                startActivity(intent);
            }
        });

        ViewSavedSongsOfflineIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YourLibraryActivity.this, SavedSongsOfflineActivity.class);
                startActivity(intent);
            }
        });

        UsernameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(YourLibraryActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        UserIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YourLibraryActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }


}

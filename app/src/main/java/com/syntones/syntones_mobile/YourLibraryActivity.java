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
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.syntones.model.Product;

import org.w3c.dom.Text;

public class YourLibraryActivity extends AppCompatActivity {


    private ListView RecentlyPlayedLv;
    private TextView ViewPlaylistsTv, ViewSavedSongsOfflineTv;
    private Button HomeBtn, SearchBtn, YourLibraryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_library);

        ViewPlaylistsTv = (TextView) findViewById(R.id.tvViewPlaylists);
        ViewSavedSongsOfflineTv = (TextView) findViewById(R.id.tvViewSavedSongsOffline);

        HomeBtn = (Button) findViewById(R.id.btnHome);
        SearchBtn = (Button) findViewById(R.id.btnSearch);
        YourLibraryBtn = (Button) findViewById(R.id.btnSearch);

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
            Log.d("CONNECTION YL", "TRUE");

        } else {
            Log.d("CONNECTION YL", "FALSE");
            HomeBtn.setClickable(false);
            SearchBtn.setClickable(false);


        }
        RecentlyPlayedLv = (ListView) findViewById(R.id.lvRecentlyPlayed);

//        ArrayAdapter<Product> arrayAdapter = new ArrayAdapter<Product>(this, android.R.layout.simple_list_item_1, items);
//        RecentlyPlayedLv.setAdapter(arrayAdapter);
        RecentlyPlayedLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String playlist = String.valueOf(parent.getItemAtPosition(position));
                Intent intent = new Intent(YourLibraryActivity.this, PlayListActivity.class);
                intent.putExtra("Info", playlist);
                startActivity(intent);
                Toast.makeText(getBaseContext(), playlist, Toast.LENGTH_SHORT).show();
            }


        });

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.yourlibrary, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
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
        }

    }

    public void viewPlayLists(View view) {

        Intent intent = new Intent(this, PlayListActivity.class);
        startActivity(intent);

    }

    public void viewSavedSongsOffline(View view) {

        Intent intent = new Intent(this, SavedSongsOfflineActivity.class);
        startActivity(intent);
    }


}

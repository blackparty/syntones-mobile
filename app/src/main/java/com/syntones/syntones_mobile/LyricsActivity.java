package com.syntones.syntones_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LyricsActivity extends AppCompatActivity {

    private Button HideLyricsBtn;
    private TextView LyricsTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics);

        HideLyricsBtn = (Button) findViewById(R.id.btnHideLyrics);
        LyricsTv = (TextView) findViewById(R.id.tvLyrics);
        SharedPreferences sharedPrefSongInfo = getSharedPreferences("songInfo", Context.MODE_PRIVATE);

        String song_lyrics = sharedPrefSongInfo.getString("songLyrics", "");

        LyricsTv.setText(song_lyrics);

        HideLyricsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLyrics();
            }
        });
    }

    public void hideLyrics() {
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);

    }

}

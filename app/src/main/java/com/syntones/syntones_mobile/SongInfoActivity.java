package com.syntones.syntones_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.syntones.model.Playlist;
import com.syntones.model.Song;
import com.syntones.model.ThreeItemSet;
import com.syntones.model.TwoItemSet;
import com.syntones.model.User;
import com.syntones.remote.SyntonesWebAPI;
import com.syntones.response.ListenResponse;
import com.syntones.response.SongListResponse;
import com.syntones.model.TemporaryDB;
import com.syntones.response.ThreeItemSetResponse;
import com.syntones.response.TwoItemSetResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongInfoActivity extends AppCompatActivity {


    private TextView SongTitleTv, ArtistNameTv, BackToPlaylist;
    private MediaPlayer mediaPlayer;
    private ImageView PlayBtnIv, PauseBtnIv, PreviousBtnIv, NextBtnIv;
    private int length, position;
    private Button ShowLyricsBtn, AddToPlaylistBtn;
    private SongInfoActivity sContext;
    private List<Song> songList;
    private int count = 0;
    private String song1, song2;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> songs = new ArrayList<>();
    private ListView BasketRecomLv;
    final String[] songs_urls = {"http://10.0.2.2/songUploaded/51501-502852.mp3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_info);


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        PlayBtnIv = (ImageView) findViewById(R.id.ivPlayBtn);
        PauseBtnIv = (ImageView) findViewById(R.id.ivPauseBtn);
        PreviousBtnIv = (ImageView) findViewById(R.id.ivPreviousBtn);
        NextBtnIv = (ImageView) findViewById(R.id.ivNextBtn);
        SongTitleTv = (TextView) findViewById(R.id.tvSongTitle);
        ArtistNameTv = (TextView) findViewById(R.id.tvArtistName);
        ShowLyricsBtn = (Button) findViewById(R.id.btnShowLyrics);
        AddToPlaylistBtn = (Button) findViewById(R.id.btnAddToPlaylist);
        BackToPlaylist = (TextView) findViewById(R.id.tvBackToSongList);
        BasketRecomLv = (ListView) findViewById(R.id.lvBasketRecom);


        PlayBtnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    playBtn();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        SongTitleTv.setText("O");
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, songs);
        BasketRecomLv.setAdapter(arrayAdapter);


    }


    public void playBtn() throws IOException {

        final int threshold = 50;


        Log.d("SONG URL:", songs_urls[0]);
        mediaPlayer.setDataSource(songs_urls[0]);
        mediaPlayer.prepare();
        mediaPlayer.start();


        PlayBtnIv.setVisibility(View.INVISIBLE);
        PauseBtnIv.setVisibility(View.VISIBLE);
    }


    public void pauseBtn(String[] songs_urls, String[] songs_titles, String[] songs_artists, int size) throws IOException {
        SongTitleTv.setText(songs_titles[position]);
        ArtistNameTv.setText(songs_artists[position]);


//        length = mediaPlayer.getCurrentPosition();
//        mediaPlayer.pause();
//        mediaPlayer.setDataSource(songs_urls[counter]);

        PlayBtnIv.setVisibility(View.VISIBLE);
        PauseBtnIv.setVisibility(View.INVISIBLE);
    }

    public void previousBtn(String[] songs_urls, String[] songs_titles, String[] songs_artists, int size) throws IOException {

        if (position > 0) {
            position = position - 1;

            if (position == 0) {

                position = 0;
            }
//            mediaPlayer.reset();

            SongTitleTv.setText(songs_titles[position]);
            ArtistNameTv.setText(songs_artists[position]);
            PlayBtnIv.setVisibility(View.VISIBLE);
            PauseBtnIv.setVisibility(View.INVISIBLE);
//            mediaPlayer.setDataSource(songs_urls[counter]);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
        }
    }

    public void nextBtn(String[] songs_urls, String[] songs_titles, String[] songs_artists, int size) throws IOException {
        if (position < songs_urls.length) {

            position = position + 1;

            if (position == songs_urls.length) {

                position = 0;
            }
//            mediaPlayer.reset();

            SongTitleTv.setText(songs_titles[position]);
            ArtistNameTv.setText(songs_artists[position]);
            PlayBtnIv.setVisibility(View.VISIBLE);
            PauseBtnIv.setVisibility(View.INVISIBLE);

//            mediaPlayer.setDataSource(songs_urls[counter]);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
        }
    }


}

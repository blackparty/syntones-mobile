package com.syntones.syntones_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.syntones.model.Playlist;
import com.syntones.model.Song;
import com.syntones.remote.SyntonesWebAPI;
import com.syntones.response.PlaylistSongsResponse;
import com.syntones.response.SongListResponse;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongInfoActivity extends AppCompatActivity {


    private TextView SongTitleTv, ArtistNameTv, BackToPlaylist;
    private MediaPlayer mediaPlayer;
    private ImageView PlayBtnIv, PauseBtnIv, PreviousBtnIv, NextBtnIv;
    private int counter, length;
    private Button ShowLyricsBtn, AddToPlaylistBtn;
    private SongInfoActivity sContext;
    private List<Song> songList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_info);

        SharedPreferences sharedPrefPlaylistInfo = getSharedPreferences("playlistInfo", Context.MODE_PRIVATE);
        long playlist_id = Long.parseLong(sharedPrefPlaylistInfo.getString("playlistId", ""));


        SharedPreferences sharedPrefActivityInfo = getSharedPreferences("activityInfo", Context.MODE_PRIVATE);
        final String activityState = sharedPrefActivityInfo.getString("activityState", "");

        SharedPreferences sharedPrefPlayedSongInfo = getSharedPreferences("playedSongInfo", 0);
        final int size = sharedPrefPlayedSongInfo.getInt("song_url_array" + "_size", 0);

        final String[] songs_urls = new String[size];
        final String[] songs_titles = new String[size];
        final String[] songs_artists = new String[size];
        final String[] songs_lyrics = new String[size];

        if (activityState.equals("Playlist")) {

            for (int a = 0; a < songs_urls.length; a++) {
                songs_urls[a] = sharedPrefPlayedSongInfo.getString("song_url_array" + "_" + a, null);
                songs_titles[a] = sharedPrefPlayedSongInfo.getString("song_titles_array" + "_" + a, null);
                songs_artists[a] = sharedPrefPlayedSongInfo.getString("song_artists_array" + "_" + a, null);
                songs_lyrics[a] = sharedPrefPlayedSongInfo.getString("song_lyrics_array" + "_" + a, null);
            }
        }


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


        SharedPreferences sharedPrefSongInfo = getSharedPreferences("songInfo", Context.MODE_PRIVATE);

        String songTitle = sharedPrefSongInfo.getString("songTitle", "");
        String artistName = sharedPrefSongInfo.getString("artistName", "");
        SongTitleTv.setText(songTitle);
        ArtistNameTv.setText(artistName);

        SharedPreferences sharedPrefButtonInfo = getSharedPreferences("buttonInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorButtonInfo = sharedPrefButtonInfo.edit();
        editorButtonInfo.putString("buttonStatus", "notAddedToPlaylist");
        editorButtonInfo.commit();

        PlayBtnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playBtn(songs_urls, songs_titles, songs_artists, size);
            }
        });

        PauseBtnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pauseBtn(songs_urls, songs_titles, songs_artists, size);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        PreviousBtnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    previousBtn(songs_urls, songs_titles, songs_artists, size);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        NextBtnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    nextBtn(songs_urls, songs_titles, songs_artists, size);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        BackToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToPlaylist();
            }
        });


        ShowLyricsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLyrics(activityState, songs_lyrics);
            }
        });

        AddToPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToPlaylist();
            }
        });

    }


    public void addToPlaylist() {

        SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

        syntonesWebAPI.getAllSongsFromDB().enqueue(new Callback<SongListResponse>() {
            @Override
            public void onResponse(Call<SongListResponse> call, Response<SongListResponse> response) {
                SongListResponse songListResponse = response.body();
                List<Song> songList = songListResponse.getSongs();
                for (Song a : songList) {

                    if (a.getSongTitle().equals(SongTitleTv.getText().toString()) && a.getArtist().getArtistName().equals(ArtistNameTv.getText().toString())) {

                        SharedPreferences sharedPrefButtonInfo = getSharedPreferences("buttonInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorButtonInfo = sharedPrefButtonInfo.edit();
                        editorButtonInfo.putString("buttonStatus", "addToPlaylist");
                        editorButtonInfo.commit();

                        Intent intent = new Intent(SongInfoActivity.this, PlayListActivity.class);
                        intent.putExtra("SongId", a.getSongId());
                        startActivity(intent);

                        Log.e("Song List Response ", songListResponse.getMessage().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<SongListResponse> call, Throwable t) {

            }
        });

    }

    public void playBtn(String[] songs_urls, String[] songs_titles, String[] songs_artists, int size) {

        SharedPreferences sharedPrefUserSession = getSharedPreferences("userSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorUserSession = sharedPrefUserSession.edit();
        long count = sharedPrefUserSession.getLong("sessionUser", 0);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        SongTitleTv.setText(songs_titles[counter]);
        ArtistNameTv.setText(songs_artists[counter]);

        Toast.makeText(getBaseContext(), String.valueOf(count) + " - " + String.valueOf(currentDateTimeString), Toast.LENGTH_SHORT).show();

//            mediaPlayer.setDataSource(songs_urls[counter]);
//
//            mediaPlayer.prepare();
//            mediaPlayer.start();


        PlayBtnIv.setVisibility(View.INVISIBLE);
        PauseBtnIv.setVisibility(View.VISIBLE);
    }


    public void pauseBtn(String[] songs_urls, String[] songs_titles, String[] songs_artists, int size) throws IOException {
        SongTitleTv.setText(songs_titles[counter]);
        ArtistNameTv.setText(songs_artists[counter]);

//        length = mediaPlayer.getCurrentPosition();
//        mediaPlayer.pause();
//        mediaPlayer.setDataSource(songs_urls[counter]);

        PlayBtnIv.setVisibility(View.VISIBLE);
        PauseBtnIv.setVisibility(View.INVISIBLE);
    }

    public void previousBtn(String[] songs_urls, String[] songs_titles, String[] songs_artists, int size) throws IOException {

        if (counter > 0) {
            counter = counter - 1;

            if (counter == 0) {

                counter = 0;
            }
//            mediaPlayer.reset();

            SongTitleTv.setText(songs_titles[counter]);
            ArtistNameTv.setText(songs_artists[counter]);

//            mediaPlayer.setDataSource(songs_urls[counter]);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
        }
    }

    public void nextBtn(String[] songs_urls, String[] songs_titles, String[] songs_artists, int size) throws IOException {
        if (counter < songs_urls.length) {

            counter = counter + 1;

            if (counter == songs_urls.length) {

                counter = 0;
            }
//            mediaPlayer.reset();

            SongTitleTv.setText(songs_titles[counter]);
            ArtistNameTv.setText(songs_artists[counter]);

//            mediaPlayer.setDataSource(songs_urls[counter]);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
        }
    }

    public void showLyrics(String activityState, String[] song_lyrics) {

        if (activityState.equals("Playlist")) {

            SharedPreferences sharedPrefSongInfo = getSharedPreferences("songInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorSongInfo = sharedPrefSongInfo.edit();
            editorSongInfo.putString("songLyrics", song_lyrics[counter]);
            editorSongInfo.commit();

            Intent intent = new Intent(SongInfoActivity.this, LyricsActivity.class);
            startActivity(intent);
        } else {
            final SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

            syntonesWebAPI.getAllSongsFromDB().enqueue(new Callback<SongListResponse>() {
                @Override
                public void onResponse(Call<SongListResponse> call, Response<SongListResponse> response) {
                    String song_lyrics;
                    SongListResponse songListResponse = response.body();
                    List<Song> songList = songListResponse.getSongs();
                    for (Song s : songList) {

                        if (s.getSongTitle().equals(SongTitleTv.getText().toString()) && s.getArtist().getArtistName().equals(ArtistNameTv.getText().toString())) {

                            song_lyrics = s.getLyrics();
                            SharedPreferences sharedPrefSongInfo = getSharedPreferences("songInfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editorSongInfo = sharedPrefSongInfo.edit();
                            editorSongInfo.putString("songLyrics", song_lyrics);
                            editorSongInfo.commit();

                            Toast.makeText(getBaseContext(), song_lyrics, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SongInfoActivity.this, LyricsActivity.class);
                            startActivity(intent);

                        }

                    }

                    Log.e("Song List Response:", songListResponse.getMessage().getMessage());
                }


                @Override
                public void onFailure(Call<SongListResponse> call, Throwable t) {


                    Log.e("Failed", String.valueOf(t.getMessage()));

                }
            });
        }

    }

    public void backToPlaylist() {
        SharedPreferences sharedPrefActivityInfo = getSharedPreferences("activityInfo", Context.MODE_PRIVATE);
        String activityState = sharedPrefActivityInfo.getString("activityState", "");

        if (activityState.equals("SearchActivity")) {
            Intent intent = new Intent(SongInfoActivity.this, SearchActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SongInfoActivity.this, PlayListActivity.class);
            startActivity(intent);
        }

    }

}

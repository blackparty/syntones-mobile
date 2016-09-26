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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_info);


        SharedPreferences sharedPrefPlaylistInfo = getSharedPreferences("playlistInfo", Context.MODE_PRIVATE);
        final long playlist_id = Long.parseLong(sharedPrefPlaylistInfo.getString("playlistId", ""));


        SharedPreferences sharedPrefActivityInfo = getSharedPreferences("activityInfo", Context.MODE_PRIVATE);
        final String activityState = sharedPrefActivityInfo.getString("activityState", "");

        SharedPreferences sharedPrefPlayedSongInfo = getSharedPreferences("playedSongInfo", 0);
        final int size = sharedPrefPlayedSongInfo.getInt("song_url_array" + "_size", 0);

        final String[] songs_urls = new String[size];
        final String[] songs_titles = new String[size];
        final String[] songs_artists = new String[size];
        final String[] songs_lyrics = new String[size];

        SharedPreferences sharedPrefSongInfo = getSharedPreferences("songInfo", Context.MODE_PRIVATE);
        String currentSongTitlePosition = sharedPrefSongInfo.getString("songTitle", "");
        String currentArtistNamePosition = sharedPrefSongInfo.getString("artistName", "");
        SharedPreferences.Editor editorSongInfo = sharedPrefSongInfo.edit();
        Log.d("ARTIST NAME", currentArtistNamePosition);
        if (activityState.equals("Playlist")) {

            for (int a = 0; a < songs_urls.length; a++) {


                songs_urls[a] = sharedPrefPlayedSongInfo.getString("song_url_array" + "_" + a, null);
                songs_titles[a] = sharedPrefPlayedSongInfo.getString("song_titles_array" + "_" + a, null);
                songs_artists[a] = sharedPrefPlayedSongInfo.getString("song_artists_array" + "_" + a, null);
                songs_lyrics[a] = sharedPrefPlayedSongInfo.getString("song_lyrics_array" + "_" + a, null);

                if (songs_titles[a].equals(currentSongTitlePosition) && songs_artists[a].equals(currentArtistNamePosition)) {
                    position = a;
                    Log.d("POSITION: ", String.valueOf(a));

                    editorSongInfo.clear();
                    editorSongInfo.commit();
                }

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
        BasketRecomLv = (ListView) findViewById(R.id.lvBasketRecom);


        SongTitleTv.setText(currentSongTitlePosition);
        ArtistNameTv.setText(currentArtistNamePosition);

        SharedPreferences sharedPrefButtonInfo = getSharedPreferences("buttonInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorButtonInfo = sharedPrefButtonInfo.edit();
        editorButtonInfo.putString("buttonStatus", "notAddedToPlaylist");
        editorButtonInfo.commit();

        PlayBtnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playBtn(songs_urls, songs_titles, songs_artists, size, playlist_id);
            }
        });

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, songs);
        BasketRecomLv.setAdapter(arrayAdapter);

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

                        Intent intent = new Intent(SongInfoActivity.this, PlayListActivity.class);
                        intent.putExtra("SongId", a.getSongId());
                        intent.putExtra("buttonStatus", "addToPlaylist");
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

    public void playBtn(final String[] songs_urls, String[] songs_titles, String[] songs_artists, int size, long playlist_id) {

        final int threshold = 50;
        SongTitleTv.setText(songs_titles[position]);
        ArtistNameTv.setText(songs_artists[position]);

        SharedPreferences sharedPrefStorage = getSharedPreferences("storage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorStorage = sharedPrefStorage.edit();

        SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

        count++;
        if (count == 1) {
            editorStorage.putString("song1", songs_urls[position]);
            editorStorage.commit();
            song1 = sharedPrefStorage.getString("song1", "");


        } else if (count == 2) {
            editorStorage.putString("song2", songs_urls[position]);
            editorStorage.commit();
            song1 = sharedPrefStorage.getString("song1", "");
            song2 = sharedPrefStorage.getString("song2", "");


        } else if (count == 3) {

            editorStorage.putString("song1", sharedPrefStorage.getString("song2", ""));
            editorStorage.putString("song2", songs_urls[position]);
            editorStorage.commit();
            song1 = sharedPrefStorage.getString("song1", "");
            song2 = sharedPrefStorage.getString("song2", "");
            count = 0;
        }

        Log.d("STORAGE", "SONG1: " + song1 + " SONG2: " + song2 + " COUNT: " + count);


        SyntonesWebAPI.Factory.getInstance(sContext).getTwoItemSet().enqueue(new Callback<TwoItemSetResponse>() {
            @Override
            public void onResponse(Call<TwoItemSetResponse> call, Response<TwoItemSetResponse> response) {
                TwoItemSetResponse twoItemSetResponse = response.body();
                List<TwoItemSet> twoItemSetList = twoItemSetResponse.getTwo_item_set_list();

                if (twoItemSetList != null) {

                    String[] track_id;
                    for (final TwoItemSet a : twoItemSetList) {
                        track_id = a.getTrack_id().split(",");

                        if (track_id[0].equals(song1)) {

                            if (a.getConfidence() >= threshold) {

                                SyntonesWebAPI.Factory.getInstance(sContext).getAllSongsFromDB().enqueue(new Callback<SongListResponse>() {
                                    @Override
                                    public void onResponse(Call<SongListResponse> call, Response<SongListResponse> response) {
                                        SongListResponse songListResponse = response.body();
                                        List<Song> songList = songListResponse.getSongs();

                                        for (Song b : songList) {

                                            if (b.getSongId() == Long.parseLong(a.getRecom_song())) {


                                                arrayAdapter.add(b.getSongTitle() + " by " + b.getArtist().getArtistName());
//                                                    arrayAdapter.notifyDataSetChanged();


                                                Log.d("TWO ITEM SET RECOM", a.getRecom_song() + " - " + b.getSongTitle() + " - " + b.getArtist().getArtistName() + " - " + song1 + " CONFIDENCE: " + String.valueOf(a.getConfidence()));
                                            }

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<SongListResponse> call, Throwable t) {

                                    }
                                });
                            }
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<TwoItemSetResponse> call, Throwable t) {

            }
        });


        SyntonesWebAPI.Factory.getInstance(sContext).getThreeItemSet().enqueue(new Callback<ThreeItemSetResponse>() {
            @Override
            public void onResponse(Call<ThreeItemSetResponse> call, Response<ThreeItemSetResponse> response) {
                ThreeItemSetResponse threeItemSetResponse = response.body();
                List<ThreeItemSet> threeItemSetList = threeItemSetResponse.getThree_item_set_list();

                if (threeItemSetList != null) {

                    String[] track_id;
                    for (final ThreeItemSet b : threeItemSetList) {

                        track_id = b.getTrack_id().split(",");

                        if (track_id[0].equals(song1) && track_id[1].equals(song2)) {
                            if (b.getConfidence() >= threshold) {
                                SyntonesWebAPI.Factory.getInstance(sContext).getAllSongsFromDB().enqueue(new Callback<SongListResponse>() {
                                    @Override
                                    public void onResponse(Call<SongListResponse> call, Response<SongListResponse> response) {
                                        SongListResponse songListResponse = response.body();
                                        List<Song> songList = songListResponse.getSongs();

                                        for (Song c : songList) {

                                            if (c.getSongId() == Long.parseLong(b.getRecom_song())) {
                                                if (c.getSongTitle() != null && c.getArtist().getArtistName() != null) {
                                                    arrayAdapter.add(c.getSongTitle() + " by " + c.getArtist().getArtistName());

                                                }

                                                Log.d("THREE ITEM SET RECOM", b.getRecom_song() + " - " + c.getSongTitle() + " - " + song1 + " | " + song2 + " CONFIDENCE: " + String.valueOf(b.getConfidence()));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<SongListResponse> call, Throwable t) {

                                    }
                                });
                            }

                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<ThreeItemSetResponse> call, Throwable t) {

            }
        });
        arrayAdapter.notifyDataSetChanged();
//        this.displayRecommendation();
        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String username = sharedPrefUserInfo.getString("username", "");


        List<TemporaryDB> temporaryDB_list = new ArrayList<>();

        TemporaryDB temporaryDB = new TemporaryDB();

        temporaryDB.setSong_id(Long.parseLong(songs_urls[position]));
        temporaryDB.setUser_id(username);

        temporaryDB_list.add(temporaryDB);

        syntonesWebAPI.listen(temporaryDB_list);

        SyntonesWebAPI.Factory.getInstance(sContext).listen(temporaryDB_list).enqueue(new Callback<ListenResponse>() {
            @Override
            public void onResponse(Call<ListenResponse> call, Response<ListenResponse> response) {

                ListenResponse listenResponse = response.body();

            }

            @Override
            public void onFailure(Call<ListenResponse> call, Throwable t) {

            }
        });

        if (playlist_id != 0) {

            Playlist playlist = new Playlist();
            User user = new User();

            user.setUsername(username);
            playlist.setPlaylistId(playlist_id);
            playlist.setUser(user);
            SyntonesWebAPI.Factory.getInstance(sContext).listenPlaylist(playlist).enqueue(new Callback<ListenResponse>() {
                @Override
                public void onResponse(Call<ListenResponse> call, Response<ListenResponse> response) {

                }

                @Override
                public void onFailure(Call<ListenResponse> call, Throwable t) {

                }
            });
        }


//            mediaPlayer.setDataSource(songs_urls[counter]);
//
//            mediaPlayer.prepare();
//            mediaPlayer.start();


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

    public void showLyrics(String activityState, String[] song_lyrics) {

        if (activityState.equals("Playlist")) {

            SharedPreferences sharedPrefSongInfo = getSharedPreferences("songInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorSongInfo = sharedPrefSongInfo.edit();
            editorSongInfo.putString("songLyrics", song_lyrics[position]);
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

    public void displayRecommendation() {
        BasketRecomLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                editorActivityInfo.putString("activityState", "SearchActivity");
                editorActivityInfo.commit();

                Intent intent = new Intent(SongInfoActivity.this, SongInfoActivity.class);
                startActivity(intent);

            }
        });
    }

}

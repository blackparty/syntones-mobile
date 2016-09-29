package com.syntones.syntones_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.syntones.model.Playlist;
import com.syntones.model.Song;
import com.syntones.model.TemporaryDB;
import com.syntones.model.ThreeItemSet;
import com.syntones.model.TwoItemSet;
import com.syntones.model.User;
import com.syntones.remote.SyntonesWebAPI;
import com.syntones.response.ListenResponse;
import com.syntones.response.SongListResponse;
import com.syntones.response.ThreeItemSetResponse;
import com.syntones.response.TwoItemSetResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    private PlayerActivity sContext;
    private int position, startTime, endTime, count = 0;
    private String song1, song2, username;
    private Button PreviousBtn, PlayBtn, PauseBtn, NextBtn, ShowLyricsBtn, AddtoPlaylistBtn;
    private TextView SongStartTv, SongEndTv, SongTitleTv, ArtistNameTv, BackToSongListTv;
    private SeekBar SongBarSb;
    private Handler myHandler = new Handler();
    private int counter = 0, nextSize;
    //    private final String song_urls[] = {"http://project-tango.org/Projects/TangoBand/Songs/files/01%20La%20Cumparsita.mp3", "http://project-tango.org/Projects/TangoBand/Songs/files/Silueta%20Portena.mp3"};
    private MediaPlayer mediaPlayer;
    private ListView BasketRecomLv;
    private Switch SaveOfflineS;
    private long playlist_id;
    private static int THRESHOLD = 50;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> songs = new ArrayList<>();
    private String[] nextId, nextUrl, nextSongTitle, nextArtistName;
    private static String IPADDRESS = "http://192.168.137.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //Buttons
        PreviousBtn = (Button) findViewById(R.id.btnPrev);
        PlayBtn = (Button) findViewById(R.id.btnPlay);
        PauseBtn = (Button) findViewById(R.id.btnPause);
        NextBtn = (Button) findViewById(R.id.btnNext);
        ShowLyricsBtn = (Button) findViewById(R.id.btnShowLyrics);
        AddtoPlaylistBtn = (Button) findViewById(R.id.btnAddToPlaylist);

        //TextViews
        ArtistNameTv = (TextView) findViewById(R.id.tvArtistName);
        SongStartTv = (TextView) findViewById(R.id.tvSongStart);
        SongEndTv = (TextView) findViewById(R.id.tvSongEnd);
        SongTitleTv = (TextView) findViewById(R.id.tvSongTitle);
        BackToSongListTv = (TextView) findViewById(R.id.tvBackToSongList);

        //SeekBars
        SongBarSb = (SeekBar) findViewById(R.id.sbSongBar);

        //ListViews
        BasketRecomLv = (ListView) findViewById(R.id.lvBasketRecom);

        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPrefUserInfo.getString("username", "");

        SharedPreferences sharedPrefActivityInfo = getSharedPreferences("activityInfo", Context.MODE_PRIVATE);
        final String activityState = sharedPrefActivityInfo.getString("activityState", "");
        SharedPreferences sharedPrefPlaylistInfo = getSharedPreferences("playlistInfo", Context.MODE_PRIVATE);
        if (!activityState.equals("SearchActivity")) {
            final long playlist_id = Long.parseLong(sharedPrefPlaylistInfo.getString("playlistId", ""));

        }else{
            PreviousBtn.setVisibility(View.INVISIBLE);
            NextBtn.setVisibility(View.INVISIBLE);
        }

        SharedPreferences.Editor editorPlaylistInfo = sharedPrefPlaylistInfo.edit();
        editorPlaylistInfo.clear();
        editorPlaylistInfo.apply();


        SharedPreferences sharedPrefPlayedSongInfo = getSharedPreferences("playedSongInfo", 0);
        SharedPreferences.Editor editorPlayedSongInfo = sharedPrefPlayedSongInfo.edit();
        final int size = sharedPrefPlayedSongInfo.getInt("song_url_array" + "_size", 0);
        nextSize = size;
        final String[] songs_urls = new String[size];
        final String[] songs_titles = new String[size];
        final String[] songs_artists = new String[size];
        final String[] songs_lyrics = new String[size];
        final String[] songs_ids = new String[size];

        SharedPreferences sharedPrefSongInfo = getSharedPreferences("songInfo", Context.MODE_PRIVATE);
        String currentSongTitlePosition = sharedPrefSongInfo.getString("songTitle", "");
        String currentArtistNamePosition = sharedPrefSongInfo.getString("artistName", "");
        SharedPreferences.Editor editorSongInfo = sharedPrefSongInfo.edit();

        Log.d("DLI NULL CYA UI", String.valueOf(size));

        Log.d("currentArtistName:", currentArtistNamePosition);

        for (int a = 0; a < size; a++) {


            songs_urls[a] = sharedPrefPlayedSongInfo.getString("song_url_array" + "_" + a, null);
            songs_titles[a] = sharedPrefPlayedSongInfo.getString("song_titles_array" + "_" + a, null);
            songs_artists[a] = sharedPrefPlayedSongInfo.getString("song_artists_array" + "_" + a, null);
            songs_lyrics[a] = sharedPrefPlayedSongInfo.getString("song_lyrics_array" + "_" + a, null);
            songs_ids[a] = sharedPrefPlayedSongInfo.getString("song_id_array" + "_" + a, null);
            Log.d("SONGURL:", IPADDRESS + songs_urls[a]);
            if (songs_titles[a].equals(currentSongTitlePosition) && songs_artists[a].equals(currentArtistNamePosition)) {
                counter = a;
                Log.d("POSITION: ", String.valueOf(a));

                editorSongInfo.clear();
                editorSongInfo.apply();
            }


        }


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        SongTitleTv.setText(songs_titles[counter]);
        ArtistNameTv.setText(songs_artists[counter]);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, songs);

        BasketRecomLv.setAdapter(arrayAdapter);

        SongBarSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar SongBarSb, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }
        });

        final SharedPreferences sharedControllerPref = getSharedPreferences("playerControls", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editorControllerPref = sharedControllerPref.edit();

        editorControllerPref.clear();
        editorControllerPref.apply();
        PreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editorControllerPref.putString("isPaused", "notPaused");
                    editorControllerPref.apply();
                    previous(size, songs_ids, songs_urls, songs_titles, songs_artists, playlist_id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        PlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    play(size, songs_ids, songs_urls, songs_titles, songs_artists, playlist_id);

                    PlayBtn.setVisibility(View.INVISIBLE);
                    PauseBtn.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        PauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editorControllerPref.putString("isPaused", "paused");
                editorControllerPref.apply();
                pause();

            }
        });

        NextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editorControllerPref.putString("isPaused", "notPaused");
                    editorControllerPref.apply();
                    next(size, songs_ids, songs_urls, songs_titles, songs_artists, playlist_id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        AddtoPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToPlaylist();
            }
        });

        ShowLyricsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLyrics(activityState, songs_lyrics);
            }
        });

        BackToSongListTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToPlaylist();
            }
        });
    }


    public void previous(int size, String[] songs_ids, String[] songs_urls, String[] songs_titles, String[] songs_artists, long playlist_id) throws IOException {

        if (counter > 0) {
            counter = counter - 1;

            if (counter == 0) {

                counter = 0;
            }

            SharedPreferences sharedPrefStorage = getSharedPreferences("storage", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorStorage = sharedPrefStorage.edit();



                count++;
                if (count == 1) {
                    Log.d("COUNT 1:", String.valueOf(counter));
                    editorStorage.putString("song1", songs_ids[counter]);
                    editorStorage.commit();
                    song1 = sharedPrefStorage.getString("song1", "");


                } else if (count == 2) {
                    Log.d("COUNT 2:", String.valueOf(counter));
                    editorStorage.putString("song2", songs_ids[counter]);
                    editorStorage.commit();
                    song1 = sharedPrefStorage.getString("song1", "");
                    song2 = sharedPrefStorage.getString("song2", "");


                } else if (count == 3) {

                    editorStorage.putString("song1", sharedPrefStorage.getString("song2", ""));
                    Log.d("COUNT 3:", String.valueOf(counter));
                    editorStorage.putString("song2", songs_ids[counter]);
                    editorStorage.commit();
                    song1 = sharedPrefStorage.getString("song1", "");
                    song2 = sharedPrefStorage.getString("song2", "");
                    count = 0;

                }

            mediaPlayer.reset();

            SongTitleTv.setText(songs_titles[counter]);
            PlayBtn.setVisibility(View.VISIBLE);
            PauseBtn.setVisibility(View.INVISIBLE);
            mediaPlayer.setDataSource(IPADDRESS + songs_urls[counter]);
            mediaPlayer.prepare();
            mediaPlayer.start();

            endTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            SongBarSb.setMax(endTime);

            SongEndTv.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) endTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) endTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) endTime)))
            );

            SongStartTv.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
            );

            SongBarSb.setProgress(startTime);
            myHandler.postDelayed(UpdateSongTime, 100);
            mediaPlayer.setOnCompletionListener(this);
            SongTitleTv.setText(songs_titles[counter]);
            ArtistNameTv.setText(songs_artists[counter]);
            PauseBtn.setVisibility(View.VISIBLE);
            PlayBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void play(int size, String[] songs_ids, String[] songs_urls, String[] songs_titles, String[] songs_artists, long playlist_id) throws IOException {
        SharedPreferences sharedControllerPref = getSharedPreferences("playerControls", Context.MODE_PRIVATE);
        String isPaused = sharedControllerPref.getString("isPaused", "");

        Log.d("PAUSED STATE:", isPaused);

        if (isPaused.equals("paused")) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
            mediaPlayer.start();
        } else {
            Log.d("PLAY:", IPADDRESS + songs_urls[counter]);
            mediaPlayer.setDataSource(IPADDRESS + songs_urls[counter]);
            mediaPlayer.prepare();
            mediaPlayer.start();


        }

        SharedPreferences sharedPrefStorage = getSharedPreferences("storage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorStorage = sharedPrefStorage.edit();

        SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

        SharedPreferences sharedPrefActivityInfo = getSharedPreferences("activityInfo", Context.MODE_PRIVATE);

        if (!sharedPrefActivityInfo.getString("activityState", "").equals("SearchActivity")) {

            count++;
            if (count == 1) {
                editorStorage.putString("song1", songs_ids[counter]);
                editorStorage.commit();
                song1 = sharedPrefStorage.getString("song1", "");


            } else if (count == 2) {
                editorStorage.putString("song2", songs_ids[counter]);
                editorStorage.commit();
                song1 = sharedPrefStorage.getString("song1", "");
                song2 = sharedPrefStorage.getString("song2", "");


            } else if (count == 3) {

                editorStorage.putString("song1", sharedPrefStorage.getString("song2", ""));
                editorStorage.putString("song2", songs_ids[counter]);
                editorStorage.commit();
                song1 = sharedPrefStorage.getString("song1", "");
                song2 = sharedPrefStorage.getString("song2", "");
                count = 0;
            }

            getTwoItemSet();
            getThreeItemSet();
            arrayAdapter.notifyDataSetChanged();
            saveToTemporaryDB(songs_ids[counter], username);
            saveToRecentlyPlayedPlaylist(username);


        }
        endTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
        SongBarSb.setMax(endTime);

        SongEndTv.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) endTime),
                TimeUnit.MILLISECONDS.toSeconds((long) endTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) endTime)))
        );

        SongStartTv.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
        );

        SongBarSb.setProgress(startTime);
        myHandler.postDelayed(UpdateSongTime, 100);

        if(size!=1){
            mediaPlayer.setOnCompletionListener(this);
        }
        PlayBtn.setVisibility(View.INVISIBLE);
        PauseBtn.setVisibility(View.VISIBLE);

    }


    public void pause() {
        mediaPlayer.pause();
        endTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
        SongBarSb.setMax(endTime);

        SongEndTv.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) endTime),
                TimeUnit.MILLISECONDS.toSeconds((long) endTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) endTime)))
        );

        SongStartTv.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
        PlayBtn.setVisibility(View.VISIBLE);
        PauseBtn.setVisibility(View.INVISIBLE);
    }

    public void onCompletion(MediaPlayer mediaPlayer) {

        try {

            next(nextSize, nextId, nextUrl, nextSongTitle, nextArtistName, playlist_id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void next(int size, String[] songs_ids, String[] songs_urls, String[] songs_titles, String[] songs_artists, long playlist_id) throws IOException {

        SharedPreferences sharedControllerPref = getSharedPreferences("playerControls", Context.MODE_PRIVATE);
        String isPaused = sharedControllerPref.getString("isPaused", "");

        SharedPreferences sharedPrefStorage = getSharedPreferences("storage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorStorage = sharedPrefStorage.edit();

        Log.d("POSITION NEXT: ", String.valueOf(counter));
        if (counter < size) {

            counter = counter + 1;

            if (counter == size) {

                counter = 0;
            }

            count++;
            if (count == 1) {
                editorStorage.putString("song1", songs_ids[counter]);
                editorStorage.commit();
                song1 = sharedPrefStorage.getString("song1", "");


            } else if (count == 2) {
                editorStorage.putString("song2", songs_ids[counter]);
                editorStorage.commit();
                song1 = sharedPrefStorage.getString("song1", "");
                song2 = sharedPrefStorage.getString("song2", "");


            } else if (count == 3) {

                editorStorage.putString("song1", sharedPrefStorage.getString("song2", ""));
                editorStorage.putString("song2", songs_ids[counter]);
                editorStorage.commit();
                song1 = sharedPrefStorage.getString("song1", "");
                song2 = sharedPrefStorage.getString("song2", "");
                count = 0;
            }
            mediaPlayer.reset();

            mediaPlayer.setDataSource(IPADDRESS + songs_urls[counter]);
            mediaPlayer.prepare();
            mediaPlayer.start();

            endTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            SongBarSb.setMax(endTime);

            SongEndTv.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) endTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) endTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) endTime)))
            );

            SongStartTv.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
            );

            SongBarSb.setProgress(startTime);
            myHandler.postDelayed(UpdateSongTime, 100);
            mediaPlayer.setOnCompletionListener(this);
            SongTitleTv.setText(songs_titles[counter]);
            ArtistNameTv.setText(songs_artists[counter]);
            PauseBtn.setVisibility(View.VISIBLE);
            PlayBtn.setVisibility(View.INVISIBLE);
            arrayAdapter.clear();
            arrayAdapter.notifyDataSetChanged();
            getTwoItemSet();
            getThreeItemSet();
            BasketRecomLv.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
            saveToTemporaryDB(songs_ids[counter], username);
            saveToRecentlyPlayedPlaylist(username);
        }


    }


    private void release() {
        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }


    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            SongStartTv.setText(String.format("%d:%d",

                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            SongBarSb.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);
        }
    };


    public void addToPlaylist() {

        SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

        syntonesWebAPI.getAllSongsFromDB().enqueue(new Callback<SongListResponse>() {
            @Override
            public void onResponse(Call<SongListResponse> call, Response<SongListResponse> response) {
                SongListResponse songListResponse = response.body();
                List<Song> songList = songListResponse.getSongs();
                for (Song a : songList) {

                    if (a.getSongTitle().equals(SongTitleTv.getText().toString()) && a.getArtist().getArtistName().equals(ArtistNameTv.getText().toString())) {

                        Intent intent = new Intent(PlayerActivity.this, PlayListActivity.class);
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

    public void saveToTemporaryDB(String songs_urls, String username) {

        List<TemporaryDB> temporaryDB_list = new ArrayList<>();

        TemporaryDB temporaryDB = new TemporaryDB();

        temporaryDB.setSong_id(Long.parseLong(songs_urls));
        temporaryDB.setUser_id(username);

        temporaryDB_list.add(temporaryDB);


        SyntonesWebAPI.Factory.getInstance(sContext).listen(temporaryDB_list).enqueue(new Callback<ListenResponse>() {
            @Override
            public void onResponse(Call<ListenResponse> call, Response<ListenResponse> response) {

                ListenResponse listenResponse = response.body();

            }

            @Override
            public void onFailure(Call<ListenResponse> call, Throwable t) {

            }
        });
    }

    public void saveToRecentlyPlayedPlaylist(String username) {
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
    }

    public void getTwoItemSet() {

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

                            if (a.getConfidence() >= THRESHOLD) {

                                SyntonesWebAPI.Factory.getInstance(sContext).getAllSongsFromDB().enqueue(new Callback<SongListResponse>() {
                                    @Override
                                    public void onResponse(Call<SongListResponse> call, Response<SongListResponse> response) {
                                        SongListResponse songListResponse = response.body();
                                        List<Song> songList = songListResponse.getSongs();

                                        for (Song b : songList) {

                                            if (b.getSongId() == Long.parseLong(a.getRecom_song())) {


                                                arrayAdapter.add(b.getSongTitle() + " by " + b.getArtist().getArtistName());


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
    }

    public void getThreeItemSet() {

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
                            if (b.getConfidence() >= THRESHOLD) {
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
    }

    public void showLyrics(String activityState, String[] song_lyrics) {

        if (activityState.equals("Playlist")) {

            SharedPreferences sharedPrefSongInfo = getSharedPreferences("songInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorSongInfo = sharedPrefSongInfo.edit();
            editorSongInfo.putString("songLyrics", song_lyrics[counter]);
            editorSongInfo.commit();

            Intent intent = new Intent(PlayerActivity.this, LyricsActivity.class);
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
                            Intent intent = new Intent(PlayerActivity.this, LyricsActivity.class);
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
            myHandler.removeCallbacks(UpdateSongTime);
            mediaPlayer.stop();
            mediaPlayer.release();

            Intent intent = new Intent(PlayerActivity.this, SearchActivity.class);
            startActivity(intent);
        } else {
            myHandler.removeCallbacks(UpdateSongTime);
            mediaPlayer.stop();
            mediaPlayer.release();
            Intent intent = new Intent(PlayerActivity.this, PlayListActivity.class);
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

                Intent intent = new Intent(PlayerActivity.this, SongInfoActivity.class);
                startActivity(intent);

            }
        });
    }

}

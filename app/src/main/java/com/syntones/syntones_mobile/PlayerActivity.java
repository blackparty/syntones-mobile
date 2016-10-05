package com.syntones.syntones_mobile;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.syntones.model.Playlist;
import com.syntones.model.SavedOfflineSongs;
import com.syntones.model.Song;
import com.syntones.model.TemporaryDB;
import com.syntones.model.ThreeItemSet;
import com.syntones.model.TwoItemSet;
import com.syntones.model.User;
import com.syntones.remote.DBHelper;
import com.syntones.remote.IpAddressSetting;
import com.syntones.remote.SyntonesWebAPI;
import com.syntones.response.ListenResponse;
import com.syntones.response.SongListResponse;
import com.syntones.response.ThreeItemSetResponse;
import com.syntones.response.TwoItemSetResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    private PlayerActivity sContext;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private RelativeLayout LyricsRl;
    private static MediaPlayer mediaPlayer;
    private ImageView BackIv, ShowLyricsIv;
    private ListView BasketRecomLv;
    private Switch SaveOfflineSw;
    private String song1, song2, username, userID;
    private Button PreviousBtn, PlayBtn, PauseBtn, NextBtn, AddtoPlaylistBtn;
    private TextView SongStartTv, SongEndTv, SongTitleTv, ArtistNameTv;
    private SeekBar SongBarSb;
    private Handler myHandler = new Handler();
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> songs = new ArrayList<>();
    private String[] nextId, nextUrl, nextSongTitle, nextArtistName;
    private int counter = 0, nextSize;
    private int position, startTime, endTime, count = 0;
    private long playlist_id;
    private static int THRESHOLD = 50;
    private static IpAddressSetting iPAddressSetting = new IpAddressSetting();
    private static String IPADDRESS = "http://" + iPAddressSetting.getiPAddress();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        //Buttons
        PreviousBtn = (Button) findViewById(R.id.btnPrev);
        PlayBtn = (Button) findViewById(R.id.btnPlay);
        PauseBtn = (Button) findViewById(R.id.btnPause);
        NextBtn = (Button) findViewById(R.id.btnNext);
        AddtoPlaylistBtn = (Button) findViewById(R.id.btnAddToPlaylist);

        //TextViews
        ArtistNameTv = (TextView) findViewById(R.id.tvArtistName);
        SongStartTv = (TextView) findViewById(R.id.tvSongStart);
        SongEndTv = (TextView) findViewById(R.id.tvSongEnd);
        SongTitleTv = (TextView) findViewById(R.id.tvSongTitle);

        //ImageViews
        BackIv = (ImageView) findViewById(R.id.ivBack);
        ShowLyricsIv = (ImageView) findViewById(R.id.ivShowLyrics);

        //SeekBars
        SongBarSb = (SeekBar) findViewById(R.id.sbSongBar);

        //ListViews
        BasketRecomLv = (ListView) findViewById(R.id.lvBasketRecom);

        //Switch
        SaveOfflineSw = (Switch) findViewById(R.id.swSaveOffline);


        SharedPreferences sharedPrefUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPrefUserInfo.getString("username", "");
        userID = String.valueOf(sharedPrefUserInfo.getLong("userID", 0));

        SharedPreferences sharedPrefActivityInfo = getSharedPreferences("activityInfo", Context.MODE_PRIVATE);
        final String activityState = sharedPrefActivityInfo.getString("activityState", "");
        SharedPreferences sharedPrefPlaylistInfo = getSharedPreferences("playlistInfo", Context.MODE_PRIVATE);
        if (activityState.equals("Playlist")) {
            final long playlist_id = Long.parseLong(sharedPrefPlaylistInfo.getString("playlistId", ""));

        } else if (activityState.equals("SearchActivity")) {
            PreviousBtn.setEnabled(false);
            NextBtn.setEnabled(false);
        }

/*        SharedPreferences.Editor editorPlaylistInfo = sharedPrefPlaylistInfo.edit();
        editorPlaylistInfo.clear();
        editorPlaylistInfo.apply();*/
        final ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


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
                    if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
                        Log.d("PLAY OFFLINE", "FALSE");
                        previous(size, songs_ids, songs_urls, songs_titles, songs_artists, playlist_id);
                    } else {
                        Log.d("PLAY OFFLINE", "TRUE");
                        previousOffline(size, songs_ids, songs_urls, songs_titles, songs_artists, userID);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        PlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
                        Log.d("PLAY OFFLINE", "FALSE");
                        play(size, songs_ids, songs_urls, songs_titles, songs_artists, playlist_id);
                    } else {
                        Log.d("PLAY OFFLINE", "TRUE");
                        playOffline(size, songs_ids, songs_urls, songs_titles, songs_artists, userID);
                    }
                    PlayBtn.setEnabled(false);

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
                    if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
                        Log.d("PLAY OFFLINE", "FALSE");
                        next(size, songs_ids, songs_urls, songs_titles, songs_artists, playlist_id);
                    } else {
                        nextOffline(size, songs_ids, songs_urls, songs_titles, songs_artists, userID);
                    }
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

        ShowLyricsIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLyrics(activityState, songs_lyrics);
            }
        });

        BackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToPlaylist();
            }
        });

        SaveOfflineSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isSongSaved(songs_ids[counter], username) == false) {
                    if (SaveOfflineSw.isChecked()) {

                        saveSongsOffline(userID, songs_ids, songs_urls, songs_titles, songs_artists, songs_lyrics);
                    }
                }

            }
        });

        if (isSongSaved(songs_ids[counter], username) == true) {
            SaveOfflineSw.setChecked(true);
            SaveOfflineSw.setClickable(false);
        }

    }

    public void saveSongsOffline(final String userID, final String[] songs_ids, final String[] songs_urls, final String[] songs_titles, final String[] songs_artists, final String[] songs_lyrics) {
        DBHelper db = new DBHelper(PlayerActivity.this);
        SavedOfflineSongs savedOfflineSongs = new SavedOfflineSongs();

        savedOfflineSongs.setUserName(userID);
        savedOfflineSongs.setSongId(songs_ids[counter]);
        savedOfflineSongs.setArtistName(songs_artists[counter]);
        savedOfflineSongs.setSongTitle(songs_titles[counter]);
        savedOfflineSongs.setLyrics(songs_lyrics[counter]);
        savedOfflineSongs.setFilePath(songs_urls[counter]);

        db.insertSavedSong(savedOfflineSongs);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(IPADDRESS + songs_urls[counter]));
        String fileName = URLUtil.guessFileName(songs_urls[counter], null, MimeTypeMap.getFileExtensionFromUrl(songs_urls[counter]));
        request.setTitle(fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        File cacheDir = getCacheDir();
        request.setDestinationInExternalPublicDir(cacheDir.getAbsolutePath(), userID + "-" + fileName);
        DownloadManager downloadManager = (DownloadManager) PlayerActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);


        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Log.d("COMPLETE", "DOWNLOAD IS DONE");
                Toast.makeText(PlayerActivity.this, "DONE", Toast.LENGTH_LONG).show();

            }
        };
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


    }


    public boolean isSongSaved(String song_id, String username) {

        boolean isSaved = false;

        DBHelper db = new DBHelper(this);

        ArrayList<SavedOfflineSongs> savedOfflineSongsArrayList = db.getAllSavedOfflineSongsFromUser(username);

        for (SavedOfflineSongs a : savedOfflineSongsArrayList) {

            if (song_id.equals(a.getSongId())) {
                isSaved = true;
            }

        }
        return isSaved;
    }

    public void previousOffline(final int size, final String[] songs_ids, final String[] songs_urls, final String[] songs_titles, final String[] songs_artists, final String userID) throws IOException {
        if (counter > 0) {
            counter = counter - 1;

            if (counter == 0) {

                counter = 0;
            }

            File cacheDir = getCacheDir();
            String fileName = URLUtil.guessFileName(songs_urls[counter], null, MimeTypeMap.getFileExtensionFromUrl(songs_urls[counter]));
            File downloadDir = new File(cacheDir,  userID + "-" + fileName);
            Log.d("FILE", downloadDir.getName());
            Log.d("NAME", fileName);

            if (downloadDir.getName().equals(userID+"-"+fileName)) {
                Log.d("FILE", downloadDir.toString());

                mediaPlayer.reset();
                mediaPlayer.setDataSource("/storage/sdcard/" + downloadDir.toString());
                mediaPlayer.prepare();
                mediaPlayer.start();
            }

            mediaPlayer.reset();

            SongTitleTv.setText(songs_titles[counter]);
            PlayBtn.setEnabled(true);
            PauseBtn.setEnabled(false);


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
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try {
                        nextOffline(size, songs_ids, songs_urls, songs_titles, songs_artists, userID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            SongTitleTv.setText(songs_titles[counter]);
            ArtistNameTv.setText(songs_artists[counter]);
            PauseBtn.setEnabled(true);
            PlayBtn.setEnabled(false);
        }
    }

    public void playOffline(final int size, final String[] songs_ids, final String[] songs_urls, final String[] songs_titles, final String[] songs_artists, final String userID) throws IOException {

        SharedPreferences sharedControllerPref = getSharedPreferences("playerControls", Context.MODE_PRIVATE);
        String isPaused = sharedControllerPref.getString("isPaused", "");

        Log.d("IS PLAYING", String.valueOf(mediaPlayer.isPlaying()));
        if (mediaPlayer.isPlaying()) {
            myHandler.removeCallbacks(UpdateSongTime);
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Log.d("PAUSED STATE:", isPaused);

        if (isPaused.equals("paused")) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
            mediaPlayer.start();
        } else {
            File cacheDir = getCacheDir();
            String fileName = URLUtil.guessFileName(songs_urls[counter], null, MimeTypeMap.getFileExtensionFromUrl(songs_urls[counter]));
            File downloadDir = new File(cacheDir, userID + "-" + fileName);
            Log.d("FILE", downloadDir.getName());
            Log.d("NAME", fileName);
            if (downloadDir.getName().equals(userID + "-" + fileName)) {
                Log.d("FILE", downloadDir.toString());
                mediaPlayer.setDataSource("/storage/sdcard/" + downloadDir.toString());
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        }
        endTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
        SongBarSb.setMax(endTime);

        SongStartTv.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
        );

        SongBarSb.setProgress(startTime);
        myHandler.postDelayed(UpdateSongTime, 100);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    nextOffline(size, songs_ids, songs_urls, songs_titles, songs_artists, username);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        SongTitleTv.setText(songs_titles[counter]);
        ArtistNameTv.setText(songs_artists[counter]);


    }

    public void nextOffline(final int size, final String[] songs_ids, final String[] songs_urls, final String[] songs_titles, final String[] songs_artists, final String userID) throws IOException {

        if (counter < size) {

            counter = counter + 1;

            if (counter == size) {

                counter = 0;
            }


            File cacheDir = getCacheDir();
            String fileName = URLUtil.guessFileName(songs_urls[counter], null, MimeTypeMap.getFileExtensionFromUrl(songs_urls[counter]));
            File downloadDir = new File(cacheDir, userID + "-" + fileName);
            Log.d("FILE", downloadDir.getName());
            Log.d("NAME", fileName);
            if (downloadDir.getName().equals(userID+"-"+fileName)) {
                Log.d("FILE", downloadDir.toString());
                mediaPlayer.reset();
                mediaPlayer.setDataSource("/storage/sdcard/" + downloadDir.toString());
                mediaPlayer.prepare();
                mediaPlayer.start();
                endTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();
                SongBarSb.setMax(endTime);
            }

        }

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

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    nextOffline(size, songs_ids, songs_urls, songs_titles, songs_artists, username);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        SongTitleTv.setText(songs_titles[counter]);
        ArtistNameTv.setText(songs_artists[counter]);

        PlayBtn.setEnabled(false);
        PauseBtn.setEnabled(true);


    }


    public void previous(final int size, final String[] songs_ids, final String[] songs_urls, final String[] songs_titles, final String[] songs_artists, final long playlist_id) throws IOException {

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
                editorStorage.apply();
                song1 = sharedPrefStorage.getString("song1", "");


            } else if (count == 2) {
                Log.d("COUNT 2:", String.valueOf(counter));
                editorStorage.putString("song2", songs_ids[counter]);
                editorStorage.apply();
                song1 = sharedPrefStorage.getString("song1", "");
                song2 = sharedPrefStorage.getString("song2", "");


            } else if (count == 3) {

                editorStorage.putString("song1", sharedPrefStorage.getString("song2", ""));
                Log.d("COUNT 3:", String.valueOf(counter));
                editorStorage.putString("song2", songs_ids[counter]);
                editorStorage.apply();
                song1 = sharedPrefStorage.getString("song1", "");
                song2 = sharedPrefStorage.getString("song2", "");
                count = 0;

            }

            mediaPlayer.reset();

            SongTitleTv.setText(songs_titles[counter]);
            PlayBtn.setEnabled(true);
            PauseBtn.setEnabled(false);
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
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try {
                        next(size, songs_ids, songs_urls, songs_titles, songs_artists, playlist_id);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            SongTitleTv.setText(songs_titles[counter]);
            ArtistNameTv.setText(songs_artists[counter]);
            PauseBtn.setEnabled(true);
            PlayBtn.setEnabled(false);
        }
    }

    public void play(final int size, final String[] songs_ids, final String[] songs_urls, final String[] songs_titles, final String[] songs_artists, final long playlist_id) throws IOException {
        SharedPreferences sharedControllerPref = getSharedPreferences("playerControls", Context.MODE_PRIVATE);
        String isPaused = sharedControllerPref.getString("isPaused", "");

        Log.d("IS PLAYING", String.valueOf(mediaPlayer.isPlaying()));
        if (mediaPlayer.isPlaying()) {
            myHandler.removeCallbacks(UpdateSongTime);
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Log.d("PAUSED STATE:", isPaused);

        if (isPaused.equals("paused")) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
            mediaPlayer.start();
        } else {
            Log.d("PLAY:", IPADDRESS + songs_urls[counter]);
            mediaPlayer.setDataSource(IPADDRESS + songs_urls[counter]);
            mediaPlayer.prepare();
            mediaPlayer.start();
            SharedPreferences sharedPrefStorage = getSharedPreferences("storage", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorStorage = sharedPrefStorage.edit();

            SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

            SharedPreferences sharedPrefActivityInfo = getSharedPreferences("activityInfo", Context.MODE_PRIVATE);


            count++;
            if (count == 1) {
                editorStorage.putString("song1", songs_ids[counter]);
                editorStorage.apply();
                song1 = sharedPrefStorage.getString("song1", "");


            } else if (count == 2) {
                editorStorage.putString("song2", songs_ids[counter]);
                editorStorage.apply();
                song1 = sharedPrefStorage.getString("song1", "");
                song2 = sharedPrefStorage.getString("song2", "");


            } else if (count == 3) {

                editorStorage.putString("song1", sharedPrefStorage.getString("song2", ""));
                editorStorage.putString("song2", songs_ids[counter]);
                editorStorage.apply();
                song1 = sharedPrefStorage.getString("song1", "");
                song2 = sharedPrefStorage.getString("song2", "");
                count = 0;
            }

            getTwoItemSet();
            getThreeItemSet();
            displayRecommendation();
            arrayAdapter.notifyDataSetChanged();
            BasketRecomLv.setAdapter(arrayAdapter);
            saveToTemporaryDB(songs_ids[counter], username);

            String activityState = sharedPrefActivityInfo.getString("activityState", "");
            if (activityState.equals("Playlist")) {
                saveToRecentlyPlayedPlaylist(username);
            }


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

        if (size != 1) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try {
                        next(size, songs_ids, songs_urls, songs_titles, songs_artists, playlist_id);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        PlayBtn.setEnabled(false);


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
        PlayBtn.setEnabled(true);
        PauseBtn.setEnabled(false);
    }


    public void next(final int size, final String[] songs_ids, final String[] songs_urls, final String[] songs_titles, final String[] songs_artists, final long playlist_id) throws IOException {

        SharedPreferences sharedControllerPref = getSharedPreferences("playerControls", Context.MODE_PRIVATE);
        String isPaused = sharedControllerPref.getString("isPaused", "");

        SharedPreferences sharedPrefStorage = getSharedPreferences("storage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorStorage = sharedPrefStorage.edit();
        Log.d("PAUSE", isPaused);
        Log.d("POSITION NEXT: ", String.valueOf(counter));
        if (counter < size) {

            counter = counter + 1;

            if (counter == size) {

                counter = 0;
            }

            mediaPlayer.reset();

            mediaPlayer.setDataSource(IPADDRESS + songs_urls[counter]);
            mediaPlayer.prepare();
            mediaPlayer.start();

            endTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            SongBarSb.setMax(endTime);


            count++;
            if (count == 1) {
                editorStorage.putString("song1", songs_ids[counter]);
                editorStorage.apply();
                song1 = sharedPrefStorage.getString("song1", "");


            } else if (count == 2) {
                editorStorage.putString("song2", songs_ids[counter]);
                editorStorage.apply();
                song1 = sharedPrefStorage.getString("song1", "");
                song2 = sharedPrefStorage.getString("song2", "");


            } else if (count == 3) {

                editorStorage.putString("song1", sharedPrefStorage.getString("song2", ""));
                editorStorage.putString("song2", songs_ids[counter]);
                editorStorage.apply();
                song1 = sharedPrefStorage.getString("song1", "");
                song2 = sharedPrefStorage.getString("song2", "");
                count = 0;
            }


            arrayAdapter.clear();
            arrayAdapter.notifyDataSetChanged();
            getTwoItemSet();
            getThreeItemSet();
            BasketRecomLv.setAdapter(arrayAdapter);
            displayRecommendation();
            arrayAdapter.notifyDataSetChanged();
            saveToTemporaryDB(songs_ids[counter], username);


            saveToRecentlyPlayedPlaylist(username);
        }

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
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    next(size, songs_ids, songs_urls, songs_titles, songs_artists, playlist_id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        SongTitleTv.setText(songs_titles[counter]);
        ArtistNameTv.setText(songs_artists[counter]);

        PlayBtn.setEnabled(false);
        PauseBtn.setEnabled(true);

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
            editorSongInfo.apply();
            startActivity(new Intent(PlayerActivity.this, LyricsActivity.class));
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
                            editorSongInfo.apply();
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
//            myHandler.removeCallbacks(UpdateSongTime);
//            mediaPlayer.stop();

            Intent intent = new Intent(PlayerActivity.this, SearchActivity.class);
            startActivity(intent);
        } else if (activityState.equals("Playlist")) {
//            myHandler.removeCallbacks(UpdateSongTime);
//            mediaPlayer.stop();
            Intent intent = new Intent(PlayerActivity.this, PlayListActivity.class);
            startActivity(intent);
        } else if (activityState.equals("SavedOffline")) {
            Intent intent = new Intent(PlayerActivity.this, SavedSongsOfflineActivity.class);
            startActivity(intent);
        }

    }

    public void displayRecommendation() {
        BasketRecomLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String song = String.valueOf(parent.getItemAtPosition(position));

                SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

                syntonesWebAPI.getAllSongsFromDB().enqueue(new Callback<SongListResponse>() {
                    @Override
                    public void onResponse(Call<SongListResponse> call, Response<SongListResponse> response) {

                        SongListResponse songListResponse = response.body();
                        List<Song> songList = songListResponse.getSongs();

                        SharedPreferences sharedPrefPlayedSongInfo = getSharedPreferences("playedSongInfo", 0);
                        final SharedPreferences.Editor editorPlayedSongInfo = sharedPrefPlayedSongInfo.edit();

                        SharedPreferences sharedPrefSongInfo = getSharedPreferences("songInfo", Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editorSongInfo = sharedPrefSongInfo.edit();

                        editorPlayedSongInfo.clear();
                        editorPlayedSongInfo.apply();
                        editorSongInfo.clear();
                        editorSongInfo.apply();

                        String[] song_urls = new String[1];
                        String[] song_titles = new String[1];
                        String[] song_artists = new String[1];
                        String[] song_lyrics = new String[1];
                        String[] song_ids = new String[1];
                        editorPlayedSongInfo.putInt("song_url_array" + "_size", song_urls.length);

                        String[] song_info = song.split("\\s(by)\\s");

                        editorSongInfo.putString("songTitle", song_info[0]);
                        editorSongInfo.putString("artistName", song_info[1]);

                        editorSongInfo.apply();

                        editorSongInfo.apply();
                        int b = 0;
                        for (Song a : songList) {

                            if (a.getSongTitle().equals(song_info[0]) && a.getArtist().getArtistName().equals(song_info[1])) {


                                song_urls[b] = String.valueOf(a.getFilePath());
                                song_ids[b] = String.valueOf(a.getSongId());
                                song_titles[b] = a.getSongTitle();
                                song_artists[b] = a.getArtist().getArtistName();
                                song_lyrics[b] = a.getLyrics();


                                editorPlayedSongInfo.putString("song_url_array" + "_" + b, song_urls[b]);
                                editorPlayedSongInfo.putString("song_id_array" + "_" + b, song_ids[b]);
                                editorPlayedSongInfo.putString("song_titles_array" + "_" + b, song_titles[b]);
                                editorPlayedSongInfo.putString("song_artists_array" + "_" + b, song_artists[b]);
                                editorPlayedSongInfo.putString("song_lyrics_array" + "_" + b, song_lyrics[b]);
                                b++;


                            }
                        }
                        editorPlayedSongInfo.apply();
                        myHandler.removeCallbacks(UpdateSongTime);
                        mediaPlayer.stop();
                        SharedPreferences sharedPrefActivityInfo = getSharedPreferences("activityInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorActivityInfo = sharedPrefActivityInfo.edit();
                        editorActivityInfo.putString("activityState", "SearchActivity");
                        editorActivityInfo.apply();

                        Intent intent = new Intent(PlayerActivity.this, PlayerActivity.class);
                        startActivity(intent);

                    }

                    @Override
                    public void onFailure(Call<SongListResponse> call, Throwable t) {

                    }
                });
            }
        });
    }


    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}

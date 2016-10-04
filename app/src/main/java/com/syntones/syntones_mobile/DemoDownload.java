package com.syntones.syntones_mobile;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.syntones.remote.IpAddressSetting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

public class DemoDownload extends AppCompatActivity {

    private Switch DownloadSw;
    private Button GetFileBtn, PlayOfflineBtn;
    private IpAddressSetting iPAddressSetting = new IpAddressSetting();
    private String song = "http://" + iPAddressSetting.getiPAddress() + "/songUploaded/51552-643504.mp3";
    private MediaPlayer mediaPlayer;

    public DemoDownload() throws SocketException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_download);

        DownloadSw = (Switch) findViewById(R.id.swDownload);
        GetFileBtn = (Button) findViewById(R.id.btnGetFile);
        PlayOfflineBtn = (Button) findViewById(R.id.btnPlayOffline);
        mediaPlayer = new MediaPlayer();
        DownloadSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (DownloadSw.isChecked()) {
                    Log.d("SWITCH IS:", "ON");

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(song));
                    String fileName = URLUtil.guessFileName(song, null, MimeTypeMap.getFileExtensionFromUrl(song));
                    request.setTitle(fileName);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    DownloadManager downloadManager = (DownloadManager) DemoDownload.this.getSystemService(Context.DOWNLOAD_SERVICE);
                    downloadManager.enqueue(request);


                } else {
                    Log.d("SWITCH IS:", "OFF");
                }
            }
        });

        GetFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFile();

            }
        });

        PlayOfflineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    playOffline();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void playOffline() throws IOException {

        mediaPlayer.setDataSource("/storage/sdcard/Download/51651-506351.mp3");
        mediaPlayer.prepare();
        mediaPlayer.start();


    }

    public void getFile() {
        File downloadDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

        String fileName = URLUtil.guessFileName(song, null, MimeTypeMap.getFileExtensionFromUrl(song));
        File listAllFiles[] = downloadDir.listFiles();

        if (listAllFiles != null && listAllFiles.length > 0) {
            for (File currentFile : listAllFiles) {
                if (currentFile.isDirectory()) {

                    Log.d("DIR", currentFile.toString());


                } else {
                    if (currentFile.getName().endsWith("")) {
                        Log.d("FILE", currentFile.toString());
/*
                        // File absolute path
                        Log.e("File path", currentFile.getAbsolutePath());
                        // File Name
                        Log.e("File path", currentFile.getName());
*/

                    }
                }
            }
        }
    }


}



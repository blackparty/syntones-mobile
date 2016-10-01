package com.syntones.syntones_mobile;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class DemoDownload extends AppCompatActivity {

    private Switch DownloadSw;
    private DownloadManager downloadManager;
    private Long downloadReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_download);

        DownloadSw = (Switch) findViewById(R.id.swDownload);


        DownloadSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (DownloadSw.isChecked()) {



                    // Create request for android download manager
                    downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri song_uri = Uri.parse("http://192.168.137.1/songUploaded/51552-643504.mp3");
                    DownloadManager.Request request = new DownloadManager.Request(song_uri);

/*                    //Setting title of request
                    request.setTitle("Data Download");

                    //Setting description of request
                    request.setDescription("Android Data download using DownloadManager.");*/

                    //Set the local destination for the downloaded file to a path
                    //within the application's external files directory
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


/*                    request.setDestinationInExternalFilesDir(DemoDownload.this,
                            Environment.DIRECTORY_DOWNLOADS, "AndroidTutorialPoint.mp3");*/


                    //Enqueue download and save into referenceId
                    downloadReference = downloadManager.enqueue(request);


                    Log.d("SWITCH IS:", "ON");
                } else {
                    Log.d("SWITCH IS:", "OFF");
                }
            }
        });

    }


}

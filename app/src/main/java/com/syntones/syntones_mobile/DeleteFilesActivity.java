package com.syntones.syntones_mobile;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class DeleteFilesActivity extends AppCompatActivity {
    private Button DeleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_files);

        DeleteBtn = (Button) findViewById(R.id.btnDelete);

        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File downloadDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

                File listAllFiles[] = downloadDir.listFiles();

                if (listAllFiles != null && listAllFiles.length > 0) {
                    for (File currentFile : listAllFiles) {
                        if (currentFile.isDirectory()) {
/*
                            if (currentFile.getName().equals(fileName)) {
                                Log.d("DIR", currentFile.toString());
                            }
*/

                        } else {
                            if (currentFile.getName().endsWith("")) {

                                File file = new File(currentFile.getAbsolutePath());

                                    file.delete();
                            }
                        }
                    }
                }
            }
        });
    }


}

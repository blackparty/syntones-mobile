package com.syntones.syntones_mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class GeneratePlaylistMenuActivity extends AppCompatActivity {

    private ImageView ByArtistIv, ByTagsIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_playlist_menu);

        ByArtistIv = (ImageView) findViewById(R.id.ivArtist);
        ByTagsIv = (ImageView) findViewById(R.id.ivTags);


        final Intent intent = new Intent(this, GenerateByListActivity.class);

        ByArtistIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("GenerateBy", "Artists");
                startActivity(intent);
            }
        });

        ByTagsIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("GenerateBy", "Tags");
                startActivity(intent);
            }
        });

    }


}

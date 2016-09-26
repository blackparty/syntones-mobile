package com.syntones.syntones_mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GeneratePlaylistMenuActivity extends AppCompatActivity {

    private Button ByArtistBtn, ByTagsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_playlist_menu);

        ByArtistBtn = (Button) findViewById(R.id.btnByArtist);
        ByTagsBtn = (Button) findViewById(R.id.btnByTags);

    }

    public void generateBy(View view) {
        String btnText;
        btnText = ((Button) view).getText().toString();
        Intent intent = new Intent(this, GenerateByListActivity.class);
        if (btnText.equals("Artist")) {
            intent.putExtra("GenerateBy", "Artists");

        } else if (btnText.equals("Tags")) {
            intent.putExtra("GenerateBy", "Tags");
        }


        startActivity(intent);
    }
}

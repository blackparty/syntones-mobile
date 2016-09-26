package com.syntones.syntones_mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.syntones.model.Artist;
import com.syntones.model.Tag;
import com.syntones.remote.SyntonesWebAPI;
import com.syntones.response.ArtistResponse;
import com.syntones.response.TagsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenerateByListActivity extends AppCompatActivity {

    private ListView GenerateByLv;
    private TextView tvGenerateBy;
    private GenerateByListActivity sContext;
    private ArrayList<String> artist_list = new ArrayList<>();
    private ArrayList<String> tag_list = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_by_list);

        GenerateByLv = (ListView) findViewById(R.id.lvGenerateBy);
        tvGenerateBy = (TextView) findViewById(R.id.tvGenerateBy);

        Bundle extras = getIntent().getExtras();
        String generateBy = extras.get("GenerateBy").toString();

        tvGenerateBy.setText(generateBy);

        if (generateBy.equals("Artists")) {

            this.insertAllArtists();

            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, artist_list);
            GenerateByLv.setAdapter(arrayAdapter);

            this.displayAllArtists();

        } else if (generateBy.equals("Tags")) {

            this.insertAllTagss();

            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, tag_list);
            GenerateByLv.setAdapter(arrayAdapter);

            this.displayAllTags();
        }


    }

    public void insertAllArtists() {

        SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

        syntonesWebAPI.getAllArtists().enqueue(new Callback<ArtistResponse>() {
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {
                ArtistResponse artistResponse = response.body();
                List<Artist> artistList = artistResponse.getArtists();

                for (Artist a : artistList) {

                    arrayAdapter.add(a.getArtistName());
                    arrayAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {

            }
        });
    }

    public void displayAllArtists() {

        GenerateByLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String artistName = String.valueOf(parent.getItemAtPosition(position));
                Intent intent = new Intent(GenerateByListActivity.this, GenerateBySongsActivity.class);
                intent.putExtra("artistInfo", artistName);
                intent.putExtra("generateBy", "Artists");
                startActivity(intent);

            }
        });
    }

    public void insertAllTagss() {

        SyntonesWebAPI syntonesWebAPI = SyntonesWebAPI.Factory.getInstance(sContext);

        syntonesWebAPI.getAllTags().enqueue(new Callback<TagsResponse>() {
            @Override
            public void onResponse(Call<TagsResponse> call, Response<TagsResponse> response) {
                TagsResponse tagsResponse = response.body();
                List<Tag> tagList = tagsResponse.getTags();

                for (Tag a : tagList) {

                    arrayAdapter.add(a.getTag());
                    arrayAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<TagsResponse> call, Throwable t) {

            }
        });
    }

    public void displayAllTags() {

        GenerateByLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tag = String.valueOf(parent.getItemAtPosition(position));
                Intent intent = new Intent(GenerateByListActivity.this, GenerateBySongsActivity.class);
                intent.putExtra("tagInfo", tag);
                intent.putExtra("generateBy", "Tags");
                startActivity(intent);

            }
        });
    }
}

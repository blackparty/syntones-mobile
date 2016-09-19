package com.syntones.syntones_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.syntones.model.Product;

import org.w3c.dom.Text;

public class YourLibraryActivity extends AppCompatActivity {


    private ListView RecentlyPlayedLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_library);

        RecentlyPlayedLv = (ListView) findViewById(R.id.lvRecentlyPlayed);
        Product[] items = {
                new Product(1, "Milk", 21.50),
                new Product(2, "Butter", 15.99),
                new Product(3, "Yogurt", 14.90),
                new Product(4, "Toothpaste", 7.99),
                new Product(5, "Ice Cream", 10.00),
                new Product(6, "Chicken", 21.50),
                new Product(7, "Pork", 15.99),
                new Product(8, "Eggs", 14.90),
                new Product(9, "Cheese", 7.99),
                new Product(10, "Strawberry", 10.00),
        };
        ArrayAdapter<Product> arrayAdapter = new ArrayAdapter<Product>(this, android.R.layout.simple_list_item_1, items);
        RecentlyPlayedLv.setAdapter(arrayAdapter);
        RecentlyPlayedLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String playlist = String.valueOf(parent.getItemAtPosition(position));
                Intent intent = new Intent(YourLibraryActivity.this, PlayListActivity.class);
                intent.putExtra("Info", playlist);
                startActivity(intent);
                Toast.makeText(getBaseContext(), playlist, Toast.LENGTH_SHORT).show();
            }


        });

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.yourlibrary, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public void bottomBar(View view) {
        String btnText;

        btnText = ((Button) view).getText().toString();

        if (btnText.equals("Home")) {

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);

        } else if (btnText.equals("Search")) {

            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }

    }

    public void viewPlayLists(View view) {

        Intent intent = new Intent(this, PlayListActivity.class);
        startActivity(intent);

    }


}

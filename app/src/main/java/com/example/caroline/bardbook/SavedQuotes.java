package com.example.caroline.bardbook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class SavedQuotes extends AppCompatActivity {

    MyRecyclerViewAdapter adapter;

    String TAG = "SavedQuotes debugging tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_quotes);

        final Context context = getApplicationContext();
        final BardBook book = new BardBook(context);

        Log.d(TAG, "Arrived at SavedQuotes");

        ArrayList<String> lines = book.getSavedLines();

        String line;

        if (lines.size() == 0) {
            lines.add("No lines saved yet");
        }
        else{
            for(int i = 0; i < lines.size(); i++){
                line = lines.get(i);
                lines.set(i,  line.substring(line.indexOf("Line:")+("Line:").length(), line.indexOf(";Title:")));
                Log.d(TAG, lines.get(i));
            }
        }


        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.saved);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, lines);
        recyclerView.setAdapter(adapter);

        adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(SavedQuotes.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putInt("FromFollowed", 0);
                b.putInt("FavPoems", -1); //Your id
                b.putInt("SavedQuotes", position);
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();

            }
        });


    }
}

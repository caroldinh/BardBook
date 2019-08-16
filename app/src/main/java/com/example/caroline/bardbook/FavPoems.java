package com.example.caroline.bardbook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class FavPoems extends AppCompatActivity {

    MyRecyclerViewAdapter adapter;

    String TAG = "FavPoems debugging tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_poems);

        final Context context = getApplicationContext();
        final BardBook book = new BardBook(context);

        Log.d(TAG, "Arrived at favPoems");

        String poem;

        ArrayList<String> getPoems = book.getFavPoems();

        if (getPoems.size() == 0) {
            getPoems.add("No poems saved yet");
        }
        else{
            for(int i = 0; i < getPoems.size(); i++){
                poem = getPoems.get(i);
                getPoems.set(i,  poem.substring(0, poem.indexOf(";Author:")) + " by " +
                        poem.substring(poem.indexOf(";Author:") + (";Author:").length()));
                Log.d(TAG, getPoems.get(i));
            }
        }


        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.saved);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, getPoems);
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(FavPoems.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putInt("FromFollowed", 0);
                b.putInt("FavPoems", position); //Your id
                b.putInt("SavedQuotes", -1);
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();

            }
        });

    }
}

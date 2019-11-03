package com.example.caroline.bardbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FavPoems extends AppCompatActivity {

    MyRecyclerViewAdapter adapter;

    String TAG = "FavPoems debugging tag";
    ArrayList<String> poems = new ArrayList<String>();
    String poem;
    ArrayList<String> getPoems;
    ArrayList<String> body = new ArrayList<String>();
    ProgressBar prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_poems);

        final Context context = getApplicationContext();
        final BardBook book = new BardBook(context);

        Log.d(TAG, "Arrived at favPoems");

        TextView none = findViewById(R.id.none);
        none.setVisibility(View.INVISIBLE);

        prog = findViewById(R.id.progressBar);
        prog.setVisibility(View.INVISIBLE);

        getPoems = book.getFavPoems();

        if (getPoems.size() == 0) {
            none.setVisibility(View.VISIBLE);
        }
        else {
            for (int i = 0; i < getPoems.size(); i++) {
                poem = getPoems.get(i);
                String title = poem.substring(0, poem.indexOf(";Author:"));
                String author = poem.substring(poem.indexOf(";Author:") + (";Author:").length());
                if (title.endsWith(", The")) {
                    title = "The " + title.substring(title.length() - ", The".length());
                }
                Log.d(TAG, poem);
                Log.d(TAG, title);
                poems.add(title + " by " + author);
                Log.d(TAG, poems.get(i));

                String[] temp;
                try {
                    temp = book.getSpecificPoem(title, author);
                    Log.d(TAG, temp[1]);
                    String preview;
                    if (temp[2].length() > 100) {
                        preview = temp[2].substring(0, 100) + "...";
                    } else {
                        preview = temp[2];
                    }
                    body.add(preview);
                } catch (Exception e) {
                    body.add("Poem text not retrievable");
                }
            }
        }



        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.saved);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, getApplicationContext(), poems, body, "FavPoems");
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                prog.setVisibility(View.VISIBLE);

                if(isNetworkAvailable()) {
                    Log.d(TAG, "Clicked");
                    Intent intent = new Intent(FavPoems.this, MainActivity.class);
                    Bundle b = new Bundle();
                    poem = getPoems.get(position);
                    if (poem.indexOf(";Author") != -1) {
                        Log.d(TAG, poem);
                        b.putString("Action", "Get Specific Poem");
                        b.putString("Poet", poem.substring(poem.indexOf(";Author:") + (";Author:").length()));
                        Log.d(TAG, "Put Poet");
                        b.putString("Poem", poem.substring(0, poem.indexOf(";Author:")));
                        Log.d(TAG, "Put Poem");
                        intent.putExtras(b); //Put your id to your next Intent
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(context, "Cannot retrieve poem", Toast.LENGTH_SHORT);
                    }
                }
                else{
                    Context context = getApplicationContext();
                    Toast.makeText(context, "An error has occured. Please check your Internet connection and try again.", Toast.LENGTH_SHORT).show();
                }

                prog.setVisibility(View.INVISIBLE);

            }
        });

        adapter.setLongClickListener(new MyRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClicked(View view, int position) {
                ((Activity) context).openContextMenu(view);
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        prog.setVisibility(View.VISIBLE);
        startActivity(new Intent(FavPoems.this, Home.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            prog.setVisibility(View.VISIBLE);
            startActivity(new Intent(FavPoems.this, Home.class));
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }



}

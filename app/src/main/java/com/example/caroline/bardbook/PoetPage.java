package com.example.caroline.bardbook;

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
import android.content.ClipboardManager;

import java.util.ArrayList;

public class PoetPage extends AppCompatActivity {

    MyRecyclerViewAdapter adapter;
    private String TAG = "PoetPage Page Debugging Tag";
    String poet;
    ArrayList<String> poems;
    ArrayList<String> body;
    ProgressBar prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poet);
        BottomNavigationView nav = findViewById(R.id.navigationView);

        Bundle b = getIntent().getExtras();
        if(b != null){
            poet = b.getString("Poet");
        }

        final Context context = getApplicationContext();
        final BardBook book = new BardBook(context);
        final Toast toast = new Toast(context);

        TextView head = findViewById(R.id.heading);
        head.setText("More Poems By " + poet);

        prog = findViewById(R.id.progressBar);
        prog.setVisibility(View.INVISIBLE);

        nav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.homeButton:

                                startActivity(new Intent(PoetPage.this, Home.class));
                                finish();
                                break;

                            case R.id.follow:
                                if (book.getFollowedPoets().indexOf(poet) == -1) {
                                    book.followPoet(poet);
                                    toast.makeText(context, "Poet followed successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    toast.makeText(context, "You are already following this poet", Toast.LENGTH_SHORT).show();
                                }
                                for(String poet: book.getFollowedPoets()){
                                    Log.d(TAG, poet);
                                }
                                break;

                        }
                        return true;
                    }
                });

        nav.setVisibility(View.VISIBLE);

        Log.d(TAG, "Arrived at PoetPage");

        poems = new ArrayList<String>();
        body = new ArrayList<String>();

        try {
            poems = book.getAuthorsPoems(poet);
        }
        catch(Exception e){ }

        String title;

        for(int i = 0; i < poems.size(); i++){
            title = poems.get(i);
            if(title.endsWith(", The")){
                title = "The " + title.substring(title.length() - ", The".length());
            }
            Log.d(TAG, title);
            poems.set(i, title);

            String[] temp;
            try {
                temp = book.getSpecificPoem(title, poet);
                Log.d(TAG, temp[1]);
                String preview;
                if(temp[2].length() > 100){
                    preview = temp[2].substring(0, 100) + "...";
                }
                else{
                    preview = temp[2];
                }
                body.add(preview);
            } catch (Exception e) {
                body.add("Poem text not retrievable");
            }

            /***
            if(!temp[1].equals("Not found")){
                preview = temp[2].substring(0, 50) + "...";
            }
            else{
                preview = "Poem text unretrievable";
            }
            body.add(preview);
             ***/
        }


        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.poems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, getApplicationContext(), poems, body,"PoetPage");
        recyclerView.setAdapter(adapter);

        // Create divider between rows
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                prog.setVisibility(View.VISIBLE);

                if(isNetworkAvailable()) {
                    Intent intent = new Intent(PoetPage.this, MainActivity.class);
                    Bundle b = new Bundle();

                    b.putString("Action", "Get Specific Poem");
                    b.putString("Poet", poet);
                    b.putString("Poem", poems.get(position));

                    intent.putExtras(b); //Put your id to your next Intent
                    startActivity(intent);
                    finish();
                }
                else{
                    Context context = getApplicationContext();
                    Toast.makeText(context, "An error has occured. Please check your Internet connection and try again.", Toast.LENGTH_SHORT).show();
                }

                prog.setVisibility(View.INVISIBLE);

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
        startActivity(new Intent(PoetPage.this, Home.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            prog.setVisibility(View.VISIBLE);
            startActivity(new Intent(PoetPage.this, Home.class));
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}

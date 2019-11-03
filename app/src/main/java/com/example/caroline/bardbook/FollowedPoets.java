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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FollowedPoets extends AppCompatActivity {

    MyRecyclerViewAdapter adapter;

    String TAG = "FollowedPoets debugging tag";
    ArrayList<String> poets;
    ArrayList<String> body = new ArrayList<String>();
    ProgressBar prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followed_poets);

        final Context context = getApplicationContext();
        final BardBook book = new BardBook(context);

        TextView none = findViewById(R.id.none);
        none.setVisibility(View.INVISIBLE);

        prog = findViewById(R.id.progressBar);
        prog.setVisibility(View.INVISIBLE);

        poets = book.getFollowedPoets();

        if (poets.size() == 0) {
            none.setVisibility(View.VISIBLE);
        }

        for(String poet: poets){
            ArrayList<String> temp = new ArrayList<String>();
            String preview = "";
            try {
                temp = book.getAuthorsPoems(poet);
                if(temp.size() < 5){
                    for(String title: temp){
                        preview += title + "\n";
                    }
                    preview = preview.substring(0, preview.length() - 1);
                }
                else{
                    for(int i = 0; i < 5; i++){
                        preview += temp.get(i) + "\n";
                    }
                    preview += "& more";
                }

            } catch (Exception e) {

            }

            body.add(preview);
        }


        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.followed);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, getApplicationContext(), poets, body, "FollowedPoets");
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                prog.setVisibility(View.VISIBLE);

                if(isNetworkAvailable()) {
                    Intent intent = new Intent(FollowedPoets.this, PoetPage.class);
                    Bundle b = new Bundle();
                    b.putString("Action", "Get Poet Page");
                    b.putString("Poet", poets.get(position));
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
        startActivity(new Intent(FollowedPoets.this, Home.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            prog.setVisibility(View.VISIBLE);
            startActivity(new Intent(FollowedPoets.this, Home.class));
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}

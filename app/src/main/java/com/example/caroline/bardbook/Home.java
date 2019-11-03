package com.example.caroline.bardbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class Home extends AppCompatActivity {

    ProgressBar prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prog = findViewById(R.id.progressBar);

    }

    public void start(View view){
        prog.setVisibility(View.VISIBLE);
        startActivity(new Intent(Home.this, Selection.class));
        finish();
    }

    public void savedQuotes(View view){
        prog.setVisibility(View.VISIBLE);
        startActivity(new Intent(Home.this, SavedQuotes.class));
        finish();
    }

    public void favPoems(View view){
        prog.setVisibility(View.VISIBLE);
        startActivity(new Intent(Home.this, FavPoems.class));
        finish();
    }

    public void followedPoets(View view){
        prog.setVisibility(View.VISIBLE);
        startActivity(new Intent(Home.this, FollowedPoets.class));
        finish();
    }

    public void about(View view){
        prog.setVisibility(View.VISIBLE);
        startActivity(new Intent(Home.this, AboutApp.class));
        finish();
    }

}

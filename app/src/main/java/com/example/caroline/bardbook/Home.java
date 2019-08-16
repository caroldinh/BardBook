package com.example.caroline.bardbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void start(View view){
        startActivity(new Intent(Home.this, Selection.class));
        finish();
    }

    public void savedQuotes(View view){
        startActivity(new Intent(Home.this, SavedQuotes.class));
        finish();
    }

    public void favPoems(View view){
        startActivity(new Intent(Home.this, FavPoems.class));
        finish();
    }

}

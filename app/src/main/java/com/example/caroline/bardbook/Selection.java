package com.example.caroline.bardbook;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

public class Selection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
    }

    public void selectFromAll(View view) {
        Intent intent = new Intent(Selection.this, MainActivity.class);
        Bundle b = new Bundle();
        b.putInt("FromFollowed", 0); //Your id
        b.putInt("FavPoems", -1);
        b.putInt("SavedQuotes", -1);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        finish();
    }

    public void selectFromFollowed(View view){

        Context context = getApplicationContext();
        BardBook book = new BardBook(context);

        if(book.getFollowedPoets().size() > 0) {

            Intent intent = new Intent(Selection.this, MainActivity.class);
            Bundle b = new Bundle();
            b.putInt("FromFollowed", 1); //Your id
            b.putInt("FavPoems", -1);
            b.putInt("SavedQuotes", -1);
            intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);
            finish();

        }

        else {
            Toast toast = new Toast(context);
            toast.makeText(context, "Please follow a poet to use this feature!", Toast.LENGTH_SHORT).show();

        }

    }
}

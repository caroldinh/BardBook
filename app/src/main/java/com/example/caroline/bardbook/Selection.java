package com.example.caroline.bardbook;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

public class Selection extends AppCompatActivity {

    ProgressBar prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        prog = findViewById(R.id.progressBar);
        prog.setVisibility(View.INVISIBLE);
    }

    public void selectFromAll(View view) {

        prog.setVisibility(View.VISIBLE);

        if(isNetworkAvailable()) {
            Intent intent = new Intent(Selection.this, MainActivity.class);
            Bundle b = new Bundle();
            b.putString("Action", "Get Random");
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

    public void selectFromFollowed(View view){

        prog.setVisibility(View.VISIBLE);

        if(isNetworkAvailable()) {

            Context context = getApplicationContext();
            BardBook book = new BardBook(context);

            for (String poet : book.getFollowedPoets()) {
                Log.d("Selection debugging tag", poet);
            }

            if (book.getFollowedPoets().size() > 0) {

                Intent intent = new Intent(Selection.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putString("Action", "Get From Followed");
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();

            } else {
                Toast toast = new Toast(context);
                toast.makeText(context, "Please follow a poet to use this feature!", Toast.LENGTH_SHORT).show();

            }
        }

        else{
            Context context = getApplicationContext();
            Toast.makeText(context, "An error has occured. Please check your Internet connection and try again.", Toast.LENGTH_SHORT).show();
        }

        prog.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onBackPressed() {
        prog.setVisibility(View.VISIBLE);
        startActivity(new Intent(Selection.this, Home.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            prog.setVisibility(View.VISIBLE);
            startActivity(new Intent(Selection.this, Home.class));
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}

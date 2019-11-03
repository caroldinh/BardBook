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

import java.util.ArrayList;

public class SavedQuotes extends AppCompatActivity {

    MyRecyclerViewAdapter adapter;

    String TAG = "SavedQuotes debugging tag";
    ArrayList<String> getLines;
    ArrayList<String> lines = new ArrayList<String>();
    String line;
    ArrayList<String> body = new ArrayList<String>();
    ProgressBar prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_quotes);

        final Context context = getApplicationContext();
        final BardBook book = new BardBook(context);

        Log.d(TAG, "Arrived at SavedQuotes");

        TextView none = findViewById(R.id.none);
        none.setVisibility(View.INVISIBLE);

        prog = findViewById(R.id.progressBar);
        prog.setVisibility(View.INVISIBLE);

        getLines = book.getSavedLines();

        if (getLines.size() == 0) {
            none.setVisibility(View.VISIBLE);
        }

        else{
            for(int i = 0; i < getLines.size(); i++){
                line = getLines.get(i);
                lines.add(line.substring(line.indexOf("Line:")+("Line:").length(), line.indexOf(";Title:")));
                body.add(line.substring(line.indexOf(";Annotation:")+(";Annotation:").length()));
                Log.d(TAG, lines.get(i));
            }
        }


        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.saved);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, getApplicationContext(), lines, body,"SavedQuotes");
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


        adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                prog.setVisibility(View.VISIBLE);

                if(isNetworkAvailable()) {
                    Intent intent = new Intent(SavedQuotes.this, MainActivity.class);
                    Bundle b = new Bundle();
                    b.putString("Action", "Get Specific Poem");
                    line = getLines.get(position);
                    if (line.indexOf(";Author") != -1) {
                        b.putString("Poet", line.substring(line.indexOf(";Author:") + (";Author:").length(), line.indexOf(";Annotation:")));
                        b.putString("Poem", line.substring(line.indexOf(";Title:") + (";Title:").length(), line.indexOf(";Author:")));
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
        startActivity(new Intent(SavedQuotes.this, Home.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            prog.setVisibility(View.VISIBLE);
            startActivity(new Intent(SavedQuotes.this, Home.class));
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}

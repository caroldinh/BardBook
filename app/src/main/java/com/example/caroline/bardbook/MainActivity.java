package com.example.caroline.bardbook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    String[] poem = {"Title", "Author", "Text"};
    boolean fromFollowed;

    String TAG = "MainActivity Debugging Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BardBook.enableStrictMode();

        final TextView display = findViewById(R.id.textView); //Body
        display.setMovementMethod(new ScrollingMovementMethod());
        display.setTextIsSelectable(false);
        display.setTextIsSelectable(true);
        display.setVisibility(View.VISIBLE);

        final TextView head = findViewById(R.id.heading); //Heading
        head.setMovementMethod(new ScrollingMovementMethod());
        head.setVisibility(View.VISIBLE);

        final Context context = getApplicationContext();
        final Toast toast = new Toast(context);

        final BardBook book = new BardBook(context);
        Log.d(TAG, "Created object");

        BottomNavigationView nav = findViewById(R.id.navigationView);

        Bundle b = getIntent().getExtras();
        int value;
        int favPoems;
        int savedQuotes;
        if(b != null){
            value = b.getInt("FromFollowed");
            favPoems = b.getInt("FavPoems");
            savedQuotes = b.getInt("SavedQuotes");
            try{
                if(value==1){
                    fromFollowed=true;
                    displayFromFollowed(display, head);
                }
                else if(value==0){
                    fromFollowed=false;
                    if(favPoems != -1){
                        displayFav(favPoems, display, head);
                    }
                    else if (savedQuotes != -1){
                        displayLine(savedQuotes, display,head);
                    }
                    else {
                        displayRand(display, head);
                    }
                }
            }
            catch(Exception e){
                display.setText("En error has occurred, please try again");
            }
        }

        nav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.homeButton:

                                startActivity(new Intent(MainActivity.this, Home.class));
                                finish();
                                break;

                            case R.id.getNewPoem:

                                try {
                                    if(fromFollowed){
                                        if(book.getFollowedPoets().size() > 0) {
                                            displayFromFollowed(display, head);
                                        }
                                    }
                                    else{
                                        displayRand(display, head);
                                    }
                                }
                                catch(Exception e) {
                                    toast.makeText(context, "An error has occurred, please try again.", Toast.LENGTH_LONG).show();
                                }

                                break;

                            case R.id.favoritePoem:

                                book.favPoem(poem[0], poem[1]);
                                toast.makeText(context, "Added To Favorites", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.saveQuote:

                                int startSelection=display.getSelectionStart();
                                int endSelection=display.getSelectionEnd();
                                String line = display.getText().toString().substring(startSelection, endSelection);
                                Log.d(TAG, line);

                                if(line.length() > 0){
                                    book.saveLine(line, poem[0], poem[1]);
                                    Log.d(TAG, "Quote Saved Successfully");
                                    display.clearFocus();
                                    line = "";
                                    toast.makeText(context, "Quote Saved Successfully", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    toast.makeText(context, "Please make a selection to save quote", Toast.LENGTH_SHORT).show();
                                }

                                break;

                            case R.id.moreByPoet:

                                break;
                        }
                        return true;
                    }
                });

        nav.setVisibility(View.VISIBLE);

    }

    protected void displayRand(TextView display, TextView head) throws JSONException, IOException{
        Context context = getApplicationContext();
        BardBook book = new BardBook(context);
        poem = book.getRandPoem();
        head.setText(poem[0] + " by " + poem[1]);
        display.setText(poem[2]);
    }

    protected void displayFromFollowed(TextView display, TextView head) throws JSONException, IOException{
        Context context = getApplicationContext();
        BardBook book = new BardBook(context);
        poem = book.getFromFollowed();
        head.setText(poem[0] + " by " + poem[1]);
        display.setText(poem[2]);
    }

    protected void displayFav(int position, TextView display, TextView head) throws JSONException, IOException{
        Context context = getApplicationContext();
        BardBook book = new BardBook(context);
        poem = book.getFromFavs(position);
        head.setText(poem[0] + " by " + poem[1]);
        display.setText(poem[2]);
    }

    protected void displayLine(int position, TextView display, TextView head) throws JSONException, IOException{
        Context context = getApplicationContext();
        BardBook book = new BardBook(context);
        String line = book.getSavedLines().get(position);
        line = line.substring(line.indexOf("Line:")+("Line:").length(), line.indexOf(";"));
        poem = book.getFromQuote(position);
        head.setText(poem[0] + " by " + poem[1]);
        display.setText(poem[2]);

        int start = poem[2].indexOf(line);
        int end = start + line.length();



    }

}

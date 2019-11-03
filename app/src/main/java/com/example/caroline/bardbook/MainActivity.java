package com.example.caroline.bardbook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import java.util.concurrent.ExecutionException;
import android.app.AlertDialog;


public class MainActivity extends AppCompatActivity {

    String[] poem = {"Title", "Author", "Text"};
    boolean fromFollowed;

    String TAG = "MainActivity Debugging Tag";
    ProgressBar prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //BardBook.enableStrictMode();

        final TextView display = findViewById(R.id.textView); //Body
        display.setMovementMethod(new ScrollingMovementMethod());
        display.setTextIsSelectable(false);
        display.setTextIsSelectable(true);
        display.setVisibility(View.VISIBLE);

        final TextView head = findViewById(R.id.heading); //Heading
        head.setMovementMethod(new ScrollingMovementMethod());
        head.setVisibility(View.VISIBLE);

        prog = findViewById(R.id.progressBar); // Progress bar
        prog.setVisibility(View.INVISIBLE);


        final Context context = getApplicationContext();
        final Toast toast = new Toast(context);

        final BardBook book = new BardBook(context);
        Log.d(TAG, "Created object");

        BottomNavigationView nav = findViewById(R.id.navigationView);

        Bundle b = getIntent().getExtras();
        String action;
        if (b != null) {
            action = b.getString("Action");
            try {
                if (action.equals("Get From Followed")) {
                    fromFollowed = true;
                    displayFromFollowed(display, head, book);
                } else if (action.equals("Get Specific Poem")) {
                    Log.d(TAG, "Arrived In MainActivity");
                    fromFollowed = false;
                    String author = b.getString("Poet");
                    Log.d(TAG, author);
                    String title = b.getString("Poem");
                    Log.d(TAG, title);

                    poem = book.getSpecificPoem(title, author);
                    if (poem[0].endsWith(", The")) {
                        poem[0] = "The " + poem[0].substring(poem[0].length() - ", The".length());
                    }
                    head.setText(poem[0] + " by " + poem[1]);
                    display.setText(poem[2]);

                } else if (action.equals("Get Random")) {
                    displayRand(display, head, book);
                }
            } catch (Exception e) {
                Toast.makeText(context, "An error has occurred, please check your Internet connection and try again.", Toast.LENGTH_SHORT);
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

                                prog.setVisibility(View.VISIBLE);

                                try {
                                    if (fromFollowed) {
                                        if (book.getFollowedPoets().size() > 0) {
                                            displayFromFollowed(display, head, book);
                                        }
                                    } else {
                                        displayRand(display, head, book);
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(context, "An error has occurred, please check your Internet connection and try again.", Toast.LENGTH_SHORT);
                                }

                                prog.setVisibility(View.INVISIBLE);

                                break;

                            case R.id.favoritePoem:

                                // Check if the poem is already in favorites
                                if (book.getFavPoems().indexOf(poem[0] + ";Author:" + poem[1]) == -1) {
                                    book.favPoem(poem[0], poem[1]);
                                    toast.makeText(context, "Added To Favorites", Toast.LENGTH_SHORT).show();
                                } else {
                                    toast.makeText(context, "This poem is already in your favorites", Toast.LENGTH_SHORT).show();
                                }

                                break;

                            case R.id.saveQuote:

                                int startSelection = display.getSelectionStart();
                                int endSelection = display.getSelectionEnd();
                                String line = display.getText().toString().substring(startSelection, endSelection);
                                final String LINE;

                                if (line.length() > 0) {

                                    //Eliminate all line breaks and replace with "/"
                                    while (line.indexOf("\n") != -1) {
                                        line = line.substring(0, line.indexOf("\n")) + " / " + line.substring(line.indexOf("\n") + 1);
                                    }

                                    LINE = line;

                                    if (book.getSavedLines().indexOf("Line:" + line + ";Title:" + poem[0] + ";Author:" + poem[1]) == -1) {

                                        //Create dialog to retrieve annotation
                                        final EditText taskEditText = new EditText(MainActivity.this);
                                        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("Add Annotation (Optional)")
                                                .setView(taskEditText)
                                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        String annotation = String.valueOf(taskEditText.getText());
                                                        if(annotation.length() == 0){
                                                            annotation = "No annotation";
                                                        }
                                                        else if(annotation.indexOf("\n") > 0){
                                                            String temp = annotation;
                                                            annotation = "";
                                                            while(temp.indexOf("\n") > 0){
                                                                annotation += temp.substring(0, temp.indexOf("\n")) + " ";
                                                                temp = temp.substring(temp.indexOf("\n") + 1);
                                                            }
                                                            annotation += temp;
                                                        }
                                                        book.saveLine(LINE, poem[0], poem[1], annotation);
                                                        Log.d(TAG, "Quote Saved Successfully");
                                                        display.clearFocus();
                                                        toast.makeText(context, "Quote Saved Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                //.setNegativeButton("Cancel", null)
                                                .create();
                                        dialog.show();


                                    } else {
                                        toast.makeText(context, "This quote is already in your favorites", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    toast.makeText(context, "Please make a selection to save quote", Toast.LENGTH_SHORT).show();
                                }

                                break;

                            case R.id.moreByPoet:

                                prog.setVisibility(View.VISIBLE);

                                Intent intent = new Intent(MainActivity.this, PoetPage.class);
                                Bundle b = new Bundle();
                                b.putString("Poet", poem[1]);
                                intent.putExtras(b); //Put your id to your next Intent
                                startActivity(intent);
                                finish();
                                break;
                        }
                        return true;
                    }
                });

        nav.setVisibility(View.VISIBLE);

    }

    protected void displayRand(TextView display, TextView head, BardBook book) throws JSONException, ExecutionException, InterruptedException {
        Log.d(TAG, "Net: " + isNetworkAvailable());
        if (isNetworkAvailable()) {
            poem = book.getRandPoem();
            if (poem[0].endsWith(", The")) {
                poem[0] = "The " + poem[0].substring(poem[0].length() - ", The".length());
            }
            head.setText(poem[0] + " by " + poem[1]);
            display.setText(poem[2]);
        } else {
            Context context = getApplicationContext();
            Toast.makeText(context, "An error has occured. Please check your Internet connection and try again.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void displayFromFollowed(TextView display, TextView head, BardBook book) throws JSONException, ExecutionException, InterruptedException {
        Log.d(TAG, "Net: " + isNetworkAvailable());
        if (isNetworkAvailable()) {
            poem = book.getFromFollowed();
            if (poem[0].endsWith(", The")) {
                poem[0] = "The " + poem[0].substring(poem[0].length() - ", The".length());
            }
            head.setText(poem[0] + " by " + poem[1]);
            display.setText(poem[2]);
        } else {
            Context context = getApplicationContext();
            Toast.makeText(context, "An error has occured. Please check your Internet connection and try again.", Toast.LENGTH_SHORT).show();
        }
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
        startActivity(new Intent(MainActivity.this, Home.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            prog.setVisibility(View.VISIBLE);
            startActivity(new Intent(MainActivity.this, Home.class));
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
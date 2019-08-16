package com.example.caroline.bardbook;

import android.content.Context;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

public class BardBook {

    private ArrayList<String> followedPoets;
    private ArrayList<String> favPoems;
    private ArrayList<String> savedLines;
    private static final String TAG = "BardBook debugging tag";
    private Context c;

    private String dir;


    public BardBook(Context context){

        c = context;

        dir = c.getFilesDir().getAbsolutePath();

        //String[] files = c.fileList();

        followedPoets = new ArrayList<String>();
        favPoems = new ArrayList<String>();
        savedLines = new ArrayList<String>(); //Format: Line:"line";Title:"title";Author:"author"

        Log.d(TAG, "Created object");

        try {

            File f = new File(dir + "/BB_FavoritePoems.txt");
            Log.d(TAG, dir);
            f.createNewFile(); // if file already exists will do nothing

            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            String all = "";
            String cont;

            while ((line = br.readLine()) != null) {
                all += line + "\n";
                Log.d(TAG, all);
            }


            while(all.indexOf("\n") != -1){
                cont = all.substring(0, all.indexOf("\n"));
                all = all.substring(all.indexOf("\n") + 1);
                favPoems.add(cont);

            }

            File f2 = new File(dir + "/BB_SavedLines.txt");
            Log.d(TAG, dir);
            f2.createNewFile(); // if file already exists will do nothing

            BufferedReader br2 = new BufferedReader(new FileReader(f2));

            all = "";

            while ((line = br2.readLine()) != null) {
                all += line + "\n";
                Log.d(TAG, all);
            }

            br.close();

            while(all.indexOf("\n") != -1){
                cont = all.substring(0, all.indexOf("\n"));
                all = all.substring(all.indexOf("\n") + 1);
                savedLines.add(cont);
                Log.d(TAG, cont);

            }

        } catch (Exception e){

            Log.d(TAG, e.toString());
        }


    }


    public void followPoet(String cont){

        followedPoets.add(cont);

        FileOutputStream outputStream;

        try {
            outputStream = c.openFileOutput(c.getFilesDir() + "/" + "BB_FollowedPoets", Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            osw.write(cont);
            osw.flush();
            osw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void favPoem(String title, String author){

        favPoems.add(title + ";Author:" + author);

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/BB_FavoritePoems.txt", true));
            writer.write(title + ";Author:" + author + "\n");
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getFollowedPoets(){

        return followedPoets;
    }

    public ArrayList<String> getFavPoems(){

        Log.d(TAG, "Arrived at getFavPoems");

        Log.d(TAG, Integer.toString(favPoems.size()));


        return favPoems;
    }

    public ArrayList<String> getSavedLines(){

        Log.d(TAG, "arrived at getSavedLines");
        Log.d(TAG, Integer.toString(savedLines.size()));

        return savedLines;
    }

    public void saveLine(String line, String title, String author){

        Log.d(TAG, "arrived at saveline");

        //Eliminate all line breaks and replace with "/"
        while(line.indexOf("\n") !=-1){
            line = line.substring(0, line.indexOf("\n")) + " / " + line.substring(line.indexOf("\n")+1);
        }

        String text = "Line:" + line + ";Title:" + title + ";Author:" + author;

        savedLines.add(text);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/BB_SavedLines.txt", true));
            writer.write(text + "\n");
            writer.close();

        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONArray readJsonArrFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray json = new JSONArray(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public JSONObject readJsonObjFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public static void enableStrictMode()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public ArrayList<String> getContent(JSONArray arr, String name) throws JSONException {
        //Log.d(TAG, "arrived at getContent");
        String json = "";
        ArrayList<String> lines = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++)
        {
            json += arr.getString(i) + "\n";
        }
        //Log.d(TAG, json);
        //json = json.substring(json.indexOf("[")+1);
        while(json.indexOf("\n") != -1) {
            lines.add(json.substring(0, json.indexOf("\n")));
            json = json.substring(json.indexOf("\n")+1);
        }
        return lines;
    }

    public String getLines(JSONArray arr) throws JSONException {
        ArrayList<String> arrLines = getContent(arr, "lines");
        String lines = "";
        for(int i = 0; i < arrLines.size(); i++){
            lines += arrLines.get(i) + "\n";
        }
        return lines;
    }

    public ArrayList<JSONArray> getPoems(JSONArray arr) throws JSONException {
        ArrayList<JSONArray> poems = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            poems.add(arr.getJSONObject(i).getJSONArray("lines"));
        }
        return poems;
    }

    public ArrayList<JSONObject> splitArray(JSONArray arr) throws JSONException{
        ArrayList<JSONObject> poems = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            poems.add(arr.getJSONObject(i));
        }
        return poems;
    }

    public String[] getRandPoem() throws JSONException, IOException{

        //Get JSON object from total list of authors
        JSONObject json = readJsonObjFromUrl("http://poetrydb.org/author");

        //Divide object into JSON Array of each individual title
        JSONArray authorsArr = json.getJSONArray("authors");

        //Get random poet from list of followed poets
        Random rand = new Random();
        ArrayList<String> authors = getContent(authorsArr, "authors");
        String poet = authors.get(rand.nextInt(authors.size()));

        //Get JSON array of poems by that poet
        JSONArray json2 = readJsonArrFromUrl("http://poetrydb.org/author/"+poet);
        ArrayList<JSONObject> poems = splitArray(json2);

        //Make array of poem info (title, author, lines), return array
        String[] poemInfo = new String[3];
        int poemNum = rand.nextInt(poems.size());
        poemInfo[0] = poems.get(poemNum).getString("title"); //Title
        poemInfo[1] = poems.get(poemNum).getString("author"); //Author
        poemInfo[2] = getLines(poems.get(poemNum).getJSONArray("lines")); //Poem
        return poemInfo;

    }

    public String[] getFromFollowed() throws JSONException, IOException{

        //Get random poet from list of followed poets
        Random rand = new Random();
        String poet = followedPoets.get(rand.nextInt(followedPoets.size()));

        //Get JSON array of poems by that poet
        JSONArray json = readJsonArrFromUrl("http://poetrydb.org/author/"+poet);
        ArrayList<JSONObject> poems = splitArray(json);

        //Make array of poem info (title, author, lines), return array
        String[] poemInfo = new String[3];
        int poemNum = rand.nextInt(poems.size());
        poemInfo[0] = poems.get(poemNum).getString("title"); //Title
        poemInfo[1] = poems.get(poemNum).getString("author"); //Author
        poemInfo[2] = getLines(poems.get(poemNum).getJSONArray("lines")); //Poem
        return poemInfo;

    }

    public boolean ExternalStorageIsWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public String[] getSpecificPoem(String title, String author) throws JSONException, IOException{
        JSONArray json = readJsonArrFromUrl("http://poetrydb.org/author,title/"+author+";"+title);
        JSONObject obj = json.getJSONObject(0);

        //Make array of poem info (title, author, lines), return array
        String[] poemInfo = new String[3];
        poemInfo[0] = obj.getString("title"); //Title
        poemInfo[1] = obj.getString("author"); //Author
        poemInfo[2] = getLines(obj.getJSONArray("lines")); //Poem
        return poemInfo;

    }

    public String[] getFromFavs(int position) throws JSONException, IOException{
        String entry = favPoems.get(position);
        String title = entry.substring(0, entry.indexOf(";Author:"));
        String author = entry.substring(entry.indexOf(";Author:")+(";Author:").length());
        return getSpecificPoem(title, author);
    }

    public String[] getFromQuote(int position) throws JSONException, IOException{
        String entry = savedLines.get(position);
        String title = entry.substring(entry.indexOf(";Title:") + (";Title:").length(), entry.indexOf(";Author:"));
        String author = entry.substring(entry.indexOf(";Author:")+(";Author:").length());
        return getSpecificPoem(title, author);
    }


}

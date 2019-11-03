package com.example.caroline.bardbook;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class BardBook {

    private ArrayList<String> followedPoets;
    private ArrayList<String> favPoems;
    private ArrayList<String> savedLines;
    private static final String TAG = "BardBook debugging tag";
    private Context c;

    private String dir;

    private JSONObject objTemp = new JSONObject();
    private JSONArray arrTemp = new JSONArray();

    private JSONObject authorsObj = new JSONObject();
    private JSONArray authorsArr = new JSONArray();

    public BardBook(Context context){

        c = context;

        dir = c.getFilesDir().getAbsolutePath();


        followedPoets = new ArrayList<String>();
        favPoems = new ArrayList<String>();
        savedLines = new ArrayList<String>(); //Format: Line:"line";Title:"title";Author:"author";Annotation:"Annotation"

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

            while(all.indexOf("\n") != -1){
                cont = all.substring(0, all.indexOf("\n"));
                all = all.substring(all.indexOf("\n") + 1);
                savedLines.add(cont);
                Log.d(TAG, cont);

            }

            br2.close();

            File f3 = new File(dir + "/BB_FollowedPoets.txt");
            Log.d(TAG, dir);
            f3.createNewFile(); // if file already exists will do nothing

            BufferedReader br3 = new BufferedReader(new FileReader(f3));

            all = "";

            while ((line = br3.readLine()) != null) {
                all += line + "\n";
                Log.d(TAG, all);
            }

            while(all.indexOf("\n") != -1){
                cont = all.substring(0, all.indexOf("\n"));
                all = all.substring(all.indexOf("\n") + 1);
                followedPoets.add(cont);
                Log.d(TAG, cont);

            }

            br3.close();

        } catch (Exception e){

            Log.d(TAG, e.toString());
        }

        try{
            authorsObj = readJsonObjFromUrl("http://poetrydb.org/author");
        } catch (Exception e){
        }

    }


    public void followPoet(String cont){

        followedPoets.add(cont);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/BB_FollowedPoets.txt", true));
            writer.write(cont + "\n");
            writer.close();

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

    public void saveLine(String line, String title, String author, String annotation){

        Log.d(TAG, "arrived at saveline");

        String text = "Line:" + line + ";Title:" + title + ";Author:" + author + ";Annotation:" + annotation;

        savedLines.add(text);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/BB_SavedLines.txt", true));
            writer.write(text + "\n");
            writer.close();

        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

    }

    public void removePoem(int index){

        favPoems.remove(index);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/BB_FavoritePoems.txt", false));
            for(String entry: favPoems){
                writer.write(entry + "\n");
            }
            writer.close();

        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

    }

    public void removePoet(int index){

        followedPoets.remove(index);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/BB_FollowedPoets.txt", false));
            for(String entry: followedPoets){
                writer.write(entry + "\n");
            }
            writer.close();

        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

    }

    public void removeLine(int index){

        savedLines.remove(index);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/BB_SavedLines.txt", false));
            for(String entry: savedLines){
                writer.write(entry + "\n");
            }
            writer.close();

        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

    }


    ////////////////



    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONArray readJsonArrFromUrl(String url) throws ExecutionException, InterruptedException, JSONException {

        Log.d(TAG, "Arrived at readJsonArrFromUrl");
        String get = new getJSONArray().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url).get();
        arrTemp = new JSONArray(get);
        return arrTemp;
    }

    public JSONObject readJsonObjFromUrl(String url) throws ExecutionException, InterruptedException, JSONException{

        Log.d(TAG, "Arrived at readJsonObjFromUrl");
        String get = new getJSONObject().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url).get();
        objTemp = new JSONObject(get);
        return objTemp;
    }

    public ArrayList<String> getContent(JSONArray arr, String name) throws JSONException {
        String json = "";
        ArrayList<String> lines = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++)
        {
            json += arr.getString(i) + "\n";
        }
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


    public ArrayList<JSONObject> splitArray(JSONArray arr) throws JSONException{
        Log.d(TAG, "Array: " + arr);
        ArrayList<JSONObject> poems = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            poems.add(arr.getJSONObject(i));
        }
        return poems;
    }

    public ArrayList<String> getAuthorsPoems(String author) throws JSONException, ExecutionException, InterruptedException{

        JSONArray json = readJsonArrFromUrl("http://poetrydb.org/author/" + author + "/title");
        ArrayList<JSONObject> obj = splitArray(json);
        ArrayList<String> titles = new ArrayList<String>();
        for(int i = 0; i < obj.size(); i++){
            titles.add(obj.get(i).getString("title"));
            Log.d(TAG, obj.get(i).getString("title"));
        }
        return titles;

    }

    public String[] getRandPoem() throws JSONException, ExecutionException, InterruptedException{

        Log.d(TAG, "Arrived at getRandPoem()");

        //Get JSON object from total list of authors
        authorsObj = readJsonObjFromUrl("http://poetrydb.org/author");

        //Divide object into JSON Array of each individual title
        authorsArr = authorsObj.getJSONArray("authors");

        Log.d(TAG, "Authors: " + authorsArr);

        //Get random poet from list of followed poets
        Random rand = new Random();
        ArrayList<String> authors = getContent(authorsArr, "authors");
        String poet = authors.get(rand.nextInt(authors.size()));
        Log.d(TAG, "Poet: " + poet);

        //Get JSON array of poems by that poet
        JSONArray json2 = readJsonArrFromUrl("http://poetrydb.org/author/"+poet);
        Log.d(TAG, "3: " + json2);
        ArrayList<JSONObject> poems = splitArray(json2);

        //Make array of poem info (title, author, lines), return array
        String[] poemInfo = new String[3];
        int poemNum = rand.nextInt(poems.size());
        poemInfo[0] = poems.get(poemNum).getString("title"); //Title
        poemInfo[1] = poems.get(poemNum).getString("author"); //Author
        poemInfo[2] = getLines(poems.get(poemNum).getJSONArray("lines")); //Poem
        return poemInfo;

    }

    public String[] getFromFollowed() throws JSONException, ExecutionException, InterruptedException{

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

    public String[] getSpecificPoem(String title, String author) throws JSONException, ExecutionException, InterruptedException{
        JSONArray json = readJsonArrFromUrl("http://poetrydb.org/author,title/"+author+";"+title);
        //FIND A WAY TO GET THIS FROM THE ASYNCTASK THING
        JSONObject obj = json.getJSONObject(0);

        //Make array of poem info (title, author, lines), return array
        String[] poemInfo = new String[3];
        poemInfo[0] = obj.getString("title"); //Title
        poemInfo[1] = obj.getString("author"); //Author
        poemInfo[2] = getLines(obj.getJSONArray("lines")); //Poem
        return poemInfo;

    }


    public class getJSONArray extends AsyncTask<String , Integer, String> {

        @Override
        protected String doInBackground(String... urls) {

            String jsonText = "";
            try {
                for (int i = 0; i < urls.length; i++) {
                    InputStream is = new URL(urls[i]).openStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    is.close();
                }
            } catch(Exception e) {
                Log.d(TAG, "Error returning JSON: " + e);
            }
            return jsonText;
        }

    }

    public class getJSONObject extends AsyncTask<String , Integer, String> {

        @Override
        protected String doInBackground(String... urls) {

            String jsonText = "";
            try {
                for (int i = 0; i < urls.length; i++) {
                    InputStream is = new URL(urls[i]).openStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    is.close();
                }
            } catch(Exception e) {
                Log.d(TAG, "Error returning JSON: " + e);
            }
            return jsonText;
        }

    }


}

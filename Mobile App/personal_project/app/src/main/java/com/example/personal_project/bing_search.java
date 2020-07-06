package com.example.personal_project;

import androidx.appcompat.app.AppCompatActivity;
import java.net.*;
import java.util.*;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class bing_search extends AppCompatActivity  {

    static String subscriptionKey = "faa5f5ac0f214390a525e89e56a869d3";
    static String host = "https://api.cognitive.microsoft.com";
    static String path = "/bing/v7.0/images/search";
    static String searchTerm = "dog"; // 검색내용
    TextView Tlqkf;
    public String txt = "";


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bing_search);


// construct the search request URL (in the form of endpoint + query string)
        Tlqkf = findViewById(R.id.Tlqkf);


        nthread qwert = new nthread();
        qwert.setDaemon(true);
        qwert.start();
        try{
            qwert.join();
        } catch (InterruptedException e) {
        }



    }

    public String asdf(){
        try{ URL url = new URL(host + path + "?q=" +  URLEncoder.encode(searchTerm, "UTF-8"));
            url.openConnection();
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

            // receive JSON body
            InputStream stream = connection.getInputStream();
            String response = new Scanner(stream).useDelimiter("\\A").next();
// construct result object for return
            SearchResults results = new SearchResults(new HashMap<String, String>(), response);

            // extract Bing-related HTTP headers
            Map<String, List<String>> headers = connection.getHeaderFields();
            for (String header : headers.keySet()) {
                if (header == null) continue;      // may have null key
                if (header.startsWith("BingAPIs-") || header.startsWith("X-MSEdge-")) {
                    results.relevantHeaders.put(header, headers.get(header).get(0));
                }
            }
            return results.jsonResponse;
        }
        catch (IOException e){

        }
        return "";
    }

    class nthread extends Thread {
        public void run() {
            txt = asdf();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Tlqkf.setText(txt);
                }
            });
            handler.sendEmptyMessage(0);
        }
    }
    Handler handler = new Handler();

}

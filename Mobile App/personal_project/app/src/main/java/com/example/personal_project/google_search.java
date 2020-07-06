package com.example.personal_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class google_search extends AppCompatActivity {
    String tag = "";
    EditText edit_tag;
    ImageAdapter adapter;
    GridView gridView;
    Button search_btn;
    ArrayList<URL> imgurl = new ArrayList<URL>();
    ArrayList<Bitmap> bitmap = new ArrayList<Bitmap>();
    boolean ck= true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_search);
        Intent this_intent = getIntent();
        tag = this_intent.getStringExtra("tag");
        edit_tag = findViewById(R.id.search_edit_tag);
        edit_tag.setText(tag);
        search_btn = findViewById(R.id.search_button);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag = edit_tag.getText().toString();
                if(tag.length()!=0) {
                    Intent asdf = new Intent(google_search.this, google_search.class);
                    asdf.putExtra("tag", tag);
                    startActivity(asdf);
                    finish();
                }
                else{
                    Toast.makeText(google_search.this,"Please Enter tags",Toast.LENGTH_SHORT).show();
                }
            }
        });

        adapter = new ImageAdapter(this);
        gridView = (GridView) findViewById(R.id.image_search_grid);
        gridView.setAdapter(adapter);

        nthread qwert = new nthread();
        qwert.setDaemon(true);
        qwert.start();
        try{
            qwert.join();
        } catch (InterruptedException e) {
        }

        if(ck) {
            nthread2 zxcv = new nthread2();
            zxcv.setDaemon(true);
            zxcv.start();
            try {
                zxcv.join();
            } catch (InterruptedException e) {
            }
            for (int q = 0; q < 10; q++) {

                adapter.addItem(bitmap.get(q), "", "", "");
            }
        }
        else {
            Toast.makeText(this, "Result dosen't Exists", Toast.LENGTH_SHORT).show();
        }
    }
    public void asdf(){
        try {
            String u = "https://www.bing.com/images/search?form=HDRSC2&cw=10000&ch=10000&q=" + tag + "&first=";
            Connection.Response response = Jsoup.connect(u + "1").execute();
            Document doc = response.parse();
            Elements imgs = doc.select("img.mimg");
            if (imgs.size() == 0){
                ck=false;
            }else {
                int maxima;
                int i = 0, j, k = 1;
                while (i < 10) {
                    maxima = imgs.size();
                    j = 0;
                    while (i < 10 && j < maxima) {
                        if (imgs.get(j).attr("src").contains("https")) {
                            i++;
                            imgurl.add(new URL(imgs.get(j).attr("src")));
                        }
                        j++;
                        k++;
                        Log.d("roTLqkf", Integer.toString(k));
                    }
                    response = Jsoup.connect(u + Integer.toString(k)).execute();
                    imgs = response.parse().select("img.mimg");

                }
            }

        }
        catch (IOException e) {

        }
    }
    public void qwerty(){
        for(int i=0; i<10; i++){
            try {
                bitmap.add(BitmapFactory.decodeStream(imgurl.get(i).openConnection().getInputStream()));
            } catch (IOException e){
            }
        }
    }

    class nthread extends Thread {
        public void run() {
            asdf();
            handler.post(new Runnable() {
                @Override
                public void run() {
                }
            });
            handler.sendEmptyMessage(0);
        }
    }
    class nthread2 extends Thread {
        public void run() {
            qwerty();
            handler.post(new Runnable() {
                @Override
                public void run() {
                }
            });
            handler.sendEmptyMessage(0);
        }
    }
    Handler handler = new Handler();

}

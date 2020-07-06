package com.example.map_pa;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ListViewitem {
    private Bitmap profile;
    private String username;
    private String article;
    private String tags;
    private Bitmap post_img;
    private String id;

    public Bitmap getprofile() {
        return this.profile;
    }
    public String getusername() {
        return this.username;
    }
    public String getarticle() {
        return this.article;
    }
    public String gettags() {
        return this.tags;
    }
    public Bitmap getpost_img() {
        return this.post_img;
    }
    public String getid() {return this.id;}

//    public ListViewitem(Drawable profile, String username, String article, String tags, Drawable post_img){
//        this.profile = profile;
//        this.username = username;
//        this.article = article;
//        this.tags = tags;
//        this.post_img = post_img;
//    }
    public ListViewitem(String username, String article, String tags, Bitmap post_img,Bitmap profile,String id){
        this.username = username;
        this.article = article;
        this.tags = tags;
        this.post_img = post_img;
        this.profile = profile;
        this.id = id;
    }
    public ListViewitem(String username, String article, String tags){
        this.username = username;
        this.article = article;
        this.tags = tags;

    }


}
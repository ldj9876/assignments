package com.example.personal_project;

import android.graphics.Bitmap;

public class GridViewItem {
    private Bitmap image;
    private String tag;
    private String username;
    private double distance;
    private String id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public GridViewItem(Bitmap image, String tag, String username){
        this.image = image;
        this.tag = tag;
        this.username = username;
    }
    public GridViewItem(Bitmap image, String tag, String username,String id){
        this.image = image;
        this.tag = tag;
        this.username = username;
        this.id = id;
    }


}

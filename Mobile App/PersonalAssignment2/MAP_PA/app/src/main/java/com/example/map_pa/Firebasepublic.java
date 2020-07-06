package com.example.map_pa;

import java.util.HashMap;
import java.util.Map;

public class Firebasepublic {
    public String username;
    public String article;
    public String tags;
    public String img;
    public String id;

    public Firebasepublic(){

    }

    public Firebasepublic(String username, String article, String tags, String img,String id){
        this.username = username;
        this.article = article;
        this.tags = tags;
        this.img = img;
        this.id = id;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("article",article);
        result.put("tags",tags);
        result.put("img",img);
        result.put("id",id);
        return result;
    }
}
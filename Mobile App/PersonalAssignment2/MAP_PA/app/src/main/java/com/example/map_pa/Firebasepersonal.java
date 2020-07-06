package com.example.map_pa;

import java.util.HashMap;
import java.util.Map;

public class Firebasepersonal {
    public String article;
    public String tags;
    public String img;
    public String id;

    public Firebasepersonal(){

    }

    public Firebasepersonal(String article, String tags, String img, String id){
        this.article = article;
        this.tags = tags;
        this.img = img;
        this.id = id;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("article",article);
        result.put("tags",tags);
        result.put("img",img);
        result.put("id",id);
        return result;
    }
}
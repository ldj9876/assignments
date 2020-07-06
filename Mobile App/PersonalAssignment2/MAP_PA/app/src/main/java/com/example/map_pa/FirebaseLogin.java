package com.example.map_pa;

import java.util.HashMap;
import java.util.Map;

public class FirebaseLogin {
    public String username;
    public String password;
    public String full_name;
    public String birthday;
    public String email;
    public String profile;

    public FirebaseLogin(){

    }

    public FirebaseLogin(String username, String password, String full_name, String birthday, String email, String profile){
        this.username = username;
        this.password = password;
        this.full_name = full_name;
        this.birthday = birthday;
        this.email = email;
        this.profile = profile;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("password", password);
        result.put("full_name", full_name);
        result.put("birthday", birthday);
        result.put("email", email);
        result.put("profile", profile);
        return result;
    }
}

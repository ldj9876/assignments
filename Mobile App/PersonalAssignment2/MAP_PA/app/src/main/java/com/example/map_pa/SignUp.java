package com.example.map_pa;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private DatabaseReference mPostReference;
    String Username, Password, Full_Name, Birthday, Email;
    EditText Username_ET, Password_ET, Full_Name_ET, Birthday_ET, Email_ET;
    ArrayList<String> user_names;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Username_ET = (EditText) findViewById(R.id.signupUsername);
        Password_ET = (EditText) findViewById(R.id.signupPassword);
        Full_Name_ET = (EditText) findViewById(R.id.signupFullname);
        Birthday_ET = (EditText) findViewById(R.id.signupBirthday);
        Email_ET = (EditText) findViewById(R.id.signupEmail);

        user_names = new ArrayList<String>();

        mPostReference = FirebaseDatabase.getInstance().getReference();


        Button login = (Button)findViewById(R.id.signupButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Username = Username_ET.getText().toString();
                Password = Password_ET.getText().toString();
                Full_Name = Full_Name_ET.getText().toString();
                Birthday = Birthday_ET.getText().toString();
                Email = Email_ET.getText().toString();
                Boolean duplicated = false;
                if (Username.length() * Password.length() * Full_Name.length() * Birthday.length() * Email.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please fill all blanks",Toast.LENGTH_SHORT).show();
                }
                else {
                    for (String name : user_names){
                        if (Username.equals(name)) {
                            duplicated = true;
                            Toast.makeText(getApplicationContext(), "Please use another username",Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    if (!duplicated){
                        Sign_Up(true);
                        Intent signupIntent = new Intent(SignUp.this, MainActivity.class);
                        signupIntent.putExtra("Username_ET", Username);
                        startActivity(signupIntent);
                    }
                }
            }
        });

        getFirebaseDatabase();


    }
    public void getFirebaseDatabase() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_names.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FirebaseLogin get = postSnapshot.getValue(FirebaseLogin.class);
                    String info = get.username;
                    user_names.add(info);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("user_data").addValueEventListener(postListener);
    }

    public void Sign_Up(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> user_info = null;
        if(add){
            FirebaseLogin infomation = new FirebaseLogin(Username, Password, Full_Name, Birthday, Email, "default");
            user_info = infomation.toMap();
        }
        childUpdates.put("/user_data/"+ Username, user_info);
        mPostReference.updateChildren(childUpdates);
        clearET();
    }
    public void clearET() {
        Username_ET.setText("");
        Password_ET.setText("");
        Full_Name_ET.setText("");
        Birthday_ET.setText("");
        Email_ET.setText("");
    }
}

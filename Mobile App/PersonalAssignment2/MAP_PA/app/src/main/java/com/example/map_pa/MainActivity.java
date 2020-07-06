package com.example.map_pa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mPostReference;
    String Username, Password ;
    EditText Username_ET, Password_ET;
    Button login;

    ArrayList<String[]> data;
//    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        data = new ArrayList<String[]>();
        Username_ET = (EditText) findViewById(R.id.userid);
        Password_ET = (EditText) findViewById(R.id.password);
        login = (Button)findViewById(R.id.loginButton);

        mPostReference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        String data_intent = intent.getStringExtra("Username_ET");
        if (data_intent!=null){
            Username_ET.setText(data_intent);
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Username = Username_ET.getText().toString();
                Password = Password_ET.getText().toString();
                Boolean got_username = false;
                for (String[] user_info : data){
                    if(user_info[0].equals(Username)){
                        got_username = true;
                        if(user_info[1].equals(Password)){
                            Intent loginIntent = new Intent(MainActivity.this, postPage.class);
                            loginIntent.putExtra("User_name", Username);
                            loginIntent.putExtra("profile",user_info[5]);
                            startActivity(loginIntent);
                            break;
                        }
                        else{
                            Toast.makeText(MainActivity.this,"Wrong Password",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
                if (!got_username){
                    Toast.makeText(MainActivity.this,"Wrong Username",Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView signup = (TextView)findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(MainActivity.this, SignUp.class);
                startActivity(signupIntent);
            }
        });

        getFirebaseDatabase();

    }

    public void getFirebaseDatabase() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FirebaseLogin get = postSnapshot.getValue(FirebaseLogin.class);
                    String[] info = {get.username, get.password, get.full_name, get.birthday, get.email, get.profile};
                    data.add(info);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("user_data").addValueEventListener(postListener);
    }


}

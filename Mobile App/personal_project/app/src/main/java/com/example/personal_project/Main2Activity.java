package com.example.personal_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {
    EditText Username_Edit, Password_Edit;
    String Username, Password;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button signup = findViewById(R.id.signupButton);
        Username_Edit = findViewById(R.id.signupUsername);
        Password_Edit = findViewById(R.id.signupPassword);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Username = Username_Edit.getText().toString();
                Password = Password_Edit.getText().toString();
                if (Username.length() * Password.length() == 0) {
                    Toast.makeText(Main2Activity.this, "Fill the blanks",Toast.LENGTH_SHORT).show();
                }
                else {
                    DocumentReference docRef = db.collection("users").document(Username);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                Toast.makeText(Main2Activity.this, "There is same Id.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                data_put(Username,Password);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }
    public void data_put(String username, String password){
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);
        db.collection("users").document(username)
                .set(data);
    }
}

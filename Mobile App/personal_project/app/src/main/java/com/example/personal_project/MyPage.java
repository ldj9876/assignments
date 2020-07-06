package com.example.personal_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class MyPage extends AppCompatActivity {
    ImageButton newPost;
    GridView gridView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageAdapter adapter;
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        Intent asdf = getIntent();
        username = asdf.getStringExtra("username");

        final Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(username +"'s posts");
        newPost = findViewById(R.id.newPost);

        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent post = new Intent(MyPage.this, MyPost.class);
                post.putExtra("username",username);
                startActivity(post);
            }
        });
        adapter = new ImageAdapter(this);
        gridView = (GridView) findViewById(R.id.grid_view);

        gridView.setAdapter(adapter);

        db.collection("posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                get_data();
            }
        });
    }
    public void get_data(){
        adapter.clear();
        adapter.notifyDataSetChanged();
        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document: task.getResult()) {
                    Map<String,Object> info = document.getData();
                    final String id = document.getId() + ".jpg";
                    final String name = (String) info.get("username") ;
                    final String tags = (String) info.get("tags");
                    if(info.get("username").equals(username)){
                        mStorageRef.child(id).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                adapter.addItem(bitmap,tags,name,id);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
    }
}

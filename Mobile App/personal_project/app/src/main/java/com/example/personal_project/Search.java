package com.example.personal_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

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

import java.util.Map;

public class Search extends AppCompatActivity {
    Button search_btn,bing;
    EditText search_tag;
    String tag;
    ImageAdapter adapter;
    GridView gridView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        bing = findViewById(R.id.bing_button);
        search_btn = findViewById(R.id.search_button);
        search_tag = findViewById(R.id.search_edit_tag);
        Intent this_intent = getIntent();
        tag = this_intent.getStringExtra("tag");
        search_tag.setText(tag);
        tag = tag.toLowerCase();
        if(tag.length()!=0)
            tag = tag.substring(0,1).toUpperCase() + tag.substring(1);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag = search_tag.getText().toString();

                Intent asdf = new Intent(Search.this, Search.class);
                asdf.putExtra("tag", tag);
                startActivity(asdf);
                finish();

                Toast.makeText(Search.this, "Please Enter the tags", Toast.LENGTH_SHORT).show();

            }
        });

        bing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent asdf = new Intent(Search.this,google_search.class);
                asdf.putExtra("tag",tag);
                startActivity(asdf);
            }
        });

        adapter = new ImageAdapter(this);
        gridView = (GridView) findViewById(R.id.search_grid);
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
                    if(tags.contains(tag+" ")){
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

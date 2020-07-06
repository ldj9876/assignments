package com.example.personal_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPost extends AppCompatActivity {
    Button creat_post;
    ImageView img;
    Uri currentImageUri;
    EditText tag_edit;
    String tag, username;
    Boolean img_empty = true;
    InputImage image;
    List<String> c_tag = new ArrayList<String>();
    List<Float> c_confi = new ArrayList<Float>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);
        creat_post = findViewById(R.id.creat_button);
        img = findViewById(R.id.imageView9);
        tag_edit = findViewById(R.id.creat_tag);
        Intent qwert = getIntent();
        username = qwert.getStringExtra("username");



        creat_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag = tag_edit.getText().toString();
                tag = tag + " ";
                if (img_empty){
                    Toast.makeText(MyPost.this, "Please Enter the image", Toast.LENGTH_SHORT).show();
                } else if (tag.length() == 0) {
                    Toast.makeText(MyPost.this, "Please Enter the Tags", Toast.LENGTH_SHORT).show();
                } else {
                    final String id = Long.toString(Long.MAX_VALUE - System.currentTimeMillis());
                    mStorageRef.child(id+".jpg").putFile(currentImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Map<String, Object> post_input = new HashMap<>();
                            post_input.put("tags", tag);
                            post_input.put("class", c_tag);
                            post_input.put("conf",c_confi);
                            post_input.put("username",username);
                            db.collection("posts").document(id).set(post_input).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    finish();
                                }
                            });
                        }
                    });
                }

            }
        });


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 777);

            }
        });


    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 777) {
            currentImageUri = data.getData();
            img.setImageURI(currentImageUri);
            img_empty = false;

            Bitmap bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();

            image = InputImage.fromBitmap(bitmap,0);
            ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
            labeler.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> imageLabels) {
                            int i = 0;
                            tag_edit.setText("");
                            for (ImageLabel label : imageLabels){

                                i++;
                                String text = label.getText();
                                float confidence = label.getConfidence();
                                if (i<=3){
                                    tag_edit.setText(tag_edit.getText().toString()+ " " + text);
                                }
                                c_tag.add(text);
                                c_confi.add(confidence);
                            }
                        }
                    });

        }
    }
}


package com.example.map_pa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class NewPost extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    String Username= "",Article="", Tag="", id="", Profile2, Profile;
    Intent intent;
    ImageView img;
    EditText article, tag;
    CheckBox checkbox;
    boolean check;
    Uri currentImageUri;
    TextView Username_txt;
    ImageView Profile_img;
    MenuItem Fullname_txt, Birthday_txt, Email_txt;

    private DatabaseReference mPostReference, mPostReference2;
    private StorageReference mStorageRef, mStorageRef2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        intent = getIntent();
        Username = intent.getStringExtra("User_name");
        Profile2 = intent.getStringExtra("profile");
        check = false;
        mPostReference = FirebaseDatabase.getInstance().getReference("posts");
        mStorageRef = FirebaseStorage.getInstance().getReference("Posts");

        mPostReference2 = FirebaseDatabase.getInstance().getReference();
        mStorageRef2 = FirebaseStorage.getInstance().getReference("Profile");

        check = false;



        img = findViewById(R.id.post_image);
        article = findViewById(R.id.postContent);

        tag = findViewById(R.id.postTags);
        checkbox = findViewById(R.id.publicPost);
        final DatabaseReference database1 = mPostReference.push();
        id = Long.toString(Long.MAX_VALUE - System.currentTimeMillis());

        Button createPost = (Button)findViewById(R.id.createPost);

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Article = article.getText().toString();
                Tag = tag.getText().toString();
                if (Article.equals("")) {
                    Toast.makeText(NewPost.this,"Please input contents",Toast.LENGTH_SHORT).show();
                }
                else  {
                    if (checkbox.isChecked()) {
                        Map<String, Object> PostUpdate = new HashMap<>();
                        PostUpdate.put("article",Article);
                        PostUpdate.put("tags",Tag);
                        PostUpdate.put("username",Username);
                        PostUpdate.put("id",id);

                        if (check) {
                            PostUpdate.put("img",id+".jpg");
                        } else {
                            PostUpdate.put("img","none");
                        }
                        mPostReference.child("publid/"+id).updateChildren(PostUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent createPostIntent = new Intent(NewPost.this, postPage.class);
                                createPostIntent.putExtra("User_name",Username);
                                createPostIntent.putExtra("profile",Profile2);
                                startActivity(createPostIntent);
                            }
                        });


                    } else {
                        Map<String, Object> PostUpdate = new HashMap<>();
                        PostUpdate.put("article",Article);
                        PostUpdate.put("tags",Tag);
                        PostUpdate.put("username",Username);
                        if (check) {
                            PostUpdate.put("img",id+".jpg");
                        } else {
                            PostUpdate.put("img","none");
                        }
                        PostUpdate.put("id",id);

                        mPostReference.child(Username+"/"+id).updateChildren(PostUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent createPostIntent = new Intent(NewPost.this, postPage.class);
                                createPostIntent.putExtra("User_name",Username);
                                createPostIntent.putExtra("profile",Profile2);
                                startActivity(createPostIntent);
                            }
                        });

                    }
                }


            }
        });

        img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery,333);
            }
        });

        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, tb, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();

        View header = navigationView.getHeaderView(0);
        Menu menu = navigationView.getMenu();

        Username_txt = (TextView) header.findViewById(R.id.drawer_username);
        Fullname_txt =  menu.findItem(R.id.navigationFullname);
        Birthday_txt = menu.findItem(R.id.navigationBirthday);
        Email_txt = menu.findItem(R.id.navigationEmail);

        Profile_img = header.findViewById(R.id.drawer_profile);
        Profile_img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery,777);
            }
        });

        Username_txt.setText(Username);
        getFirebaseDatabase();


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        closeDrawer();

        switch (item.getItemId()){
            case R.id.navigationBirthday:
                break;

            case R.id.navigationEmail:
                break;

            case R.id.navigationFullname:
                break;
        }


        return false;

    }

    private void closeDrawer(){
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            closeDrawer();
        }
        super.onBackPressed();
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 777) {
            currentImageUri = data.getData();
            check = true;
            Profile_img.setImageURI(data.getData());
            String asdf = currentImageUri.toString();

            if(Profile2.equals(Username+".jpg")){
                StorageReference ref = mStorageRef.child(Username+"1.jpg");
                ref.putFile(currentImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Map<String, Object> profileUpdate = new HashMap<>();
                        profileUpdate.put("user_data/"+Username+"/profile", Username+"1.jpg");
                        Profile2 = Username+"1.jpg";
                        mPostReference2.updateChildren(profileUpdate);

                    }
                });
            }
            else {
                StorageReference ref = mStorageRef.child(Username+".jpg");
                ref.putFile(currentImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Map<String, Object> profileUpdate = new HashMap<>();
                        profileUpdate.put("user_data/"+Username+"/profile", Username+".jpg");
                        Profile2 = Username+".jpg";
                        mPostReference2.updateChildren(profileUpdate);
                    }
                });
            }



        }
        else if (requestCode == 333) {
            currentImageUri = data.getData();
            check = true;
            img.setImageURI(currentImageUri);
            StorageReference ref = mStorageRef.child(id+".jpg");
            UploadTask uploadTask = ref.putFile(currentImageUri);
        }
    }
    public void getFirebaseDatabase() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FirebaseLogin get = postSnapshot.getValue(FirebaseLogin.class);

                    if (Username.equals(get.username)){
                        Fullname_txt.setTitle(get.full_name);

                        Birthday_txt.setTitle(get.birthday);
                        Email_txt.setTitle(get.email);
                        Profile = get.profile;
                        if(!(Profile.equals("default"))){
                            StorageReference  islandRef = mStorageRef.child(Profile);
                            final long ONE_MEGABYTE = 1024 * 1024;
                            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                    Profile_img.setImageBitmap(bitmap);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference2.child("user_data").addValueEventListener(postListener);
    }
}

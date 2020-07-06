package com.example.map_pa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class postPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    private DatabaseReference mPostReference;
    private StorageReference mStorageRef;
    Boolean check;
    Uri currentImageUri;
    String Username, Profile2, Profile;
    TextView Username_txt;
    ArrayList<String> info;
    MenuItem Fullname_txt, Birthday_txt, Email_txt;
    ImageView Profile_img;
    Intent intent;
    myFragmentStateAdapter frag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);
        intent = getIntent();
        Username = intent.getStringExtra("User_name");
        Profile2 = intent.getStringExtra("profile");

        check = false;


        mPostReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference("Profile");


        ImageButton newPost = (ImageButton)findViewById(R.id.newPost);
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPostIntent = new Intent(postPage.this, NewPost.class);
                newPostIntent.putExtra("User_name",Username);
                newPostIntent.putExtra("profile",Profile2);
                startActivity(newPostIntent);
            }
        });


        PersonalFragment personal = new PersonalFragment();
        Bundle bundle = new Bundle();
        bundle.putString("username",Username);
        personal.setArguments(bundle);




        /* to use toolbar,
        1. add implementation
                implementation 'com.android.support:appcompat-v7:29.0.3'
           into build.gradle(Module:app)  !!!! version is same with buildToolsversion

        2. add toolbar in your layout
        3. Set toolbar when onCreate (you must import androidx.appcompat.widget.Toolbar
        4. If not Refactor -> Migrate to Androidx
         */


        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);






        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, tb, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();

        getFirebaseDatabase();

        ViewPager2 viewPager2 = findViewById(R.id.viewpager);
        frag = new myFragmentStateAdapter(this, Username);

        viewPager2.setAdapter(frag);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("Personal");
                        break;
                    case 1:
                        tab.setText("Public");
                        break;
                }
            }
        });
        tabLayoutMediator.attach();

        View header = navigationView.getHeaderView(0);
        Menu menu = navigationView.getMenu();

        Username_txt = (TextView) header.findViewById(R.id.drawer_username);
        Fullname_txt =  menu.findItem(R.id.navigationFullname);
        Birthday_txt = menu.findItem(R.id.navigationBirthday);
        Email_txt = menu.findItem(R.id.navigationEmail);

        Profile_img = header.findViewById(R.id.drawer_profile);

        Profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery,777);
            }
        });
        Username_txt.setText(Username);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

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
        mPostReference.child("user_data").addValueEventListener(postListener);
    }

    @Override
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
                       mPostReference.updateChildren(profileUpdate);

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
                        mPostReference.updateChildren(profileUpdate);
                    }
                });
            }



        }
    }




}

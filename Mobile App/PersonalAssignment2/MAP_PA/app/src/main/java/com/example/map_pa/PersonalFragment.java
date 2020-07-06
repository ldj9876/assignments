package com.example.map_pa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;


public class PersonalFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private DatabaseReference mPostRefenrence = FirebaseDatabase.getInstance().getReference();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    ListViewAdapter mAdapter;
    String profile;


    String Username = "";
    ListView posts;
    View v;
    ArrayList<ListViewitem> post_data;


    public PersonalFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_personal,container,false);

        if (getArguments() != null) {
            Username = getArguments().getString("username");

        }

        posts = v.findViewById(R.id.personal_List);

        mAdapter = new ListViewAdapter(getActivity());
        posts.setAdapter(mAdapter);
        FirebaseLogin asdf = new FirebaseLogin();
//        mPostRefenrence.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : (dataSnapshot.child("posts/"+Username)).getChildren()){
//
//                    final Firebasepersonal get = postSnapshot.getValue(Firebasepersonal.class);
//                    final FirebaseLogin info = dataSnapshot.child("user_data/"+Username).getValue(FirebaseLogin.class);
//                    profile = info.profile;
//
//                    if (profile.equals("default")){
//                        if (get.img.equals("none")) {
//                            mAdapter.addItem(Username, get.article, get.tags,null,null);
//                        }
//                        else{
//                            StorageReference islandRef = mStorageRef.child("Posts/"+get.img);
//                            islandRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                                @Override
//                                public void onSuccess(byte[] bytes) {
//                                    Bitmap map = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                                    mAdapter.addItem(Username,get.article, get.tags,map,null);
//                                }
//                            });
//                        }
//                    }
//                    else{
//                        StorageReference profile_ref = mStorageRef.child("Profile/"+profile);
//                        profile_ref.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                            @Override
//                            public void onSuccess(byte[] bytes) {
//                                final Bitmap profile_img = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//
//                                if (get.img.equals("none")) {
//                                    mAdapter.addItem(Username, get.article, get.tags,null,profile_img);
//                                }
//                                else{
//                                    StorageReference islandRef = mStorageRef.child("Posts/"+get.img);
//                                    islandRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                                        @Override
//                                        public void onSuccess(byte[] bytes) {
//                                            Bitmap map = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                                            mAdapter.addItem(Username,get.article, get.tags,map,profile_img);
//                                        }
//                                    });
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


        getFirebaseDatabase();
        return v;
    }

    public void getFirebaseDatabase() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextView no_posts = v.findViewById(R.id.textView_personal);
                Boolean Check = false;

                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                for (DataSnapshot postSnapshot : (dataSnapshot.child("posts/"+Username)).getChildren()){
                    Check = true;


                    final Firebasepersonal get = postSnapshot.getValue(Firebasepersonal.class);
                    final FirebaseLogin info = dataSnapshot.child("user_data/"+Username).getValue(FirebaseLogin.class);
                    profile = info.profile;


                    if (profile.equals("default")){
                        if (get.img.equals("none")) {
                            mAdapter.addItem(Username, get.article, get.tags,null,null,get.id);
                            mAdapter.notifyDataSetChanged();
                        }
                        else{
                            StorageReference islandRef = mStorageRef.child("Posts/"+get.img);
                            islandRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap map = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                    mAdapter.addItem(Username,get.article, get.tags,map,null,get.id);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                    else{
                        StorageReference profile_ref = mStorageRef.child("Profile/"+profile);
                        Log.d("?????????",get.img);
                        profile_ref.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                final Bitmap profile_img = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                                if (get.img.equals("none")) {
                                    mAdapter.addItem(Username, get.article, get.tags,null,profile_img,get.id);
                                    mAdapter.notifyDataSetChanged();
                                }
                                else{
                                    StorageReference islandRef = mStorageRef.child("Posts/"+get.img);
                                    islandRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Bitmap map = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                            mAdapter.addItem(Username,get.article, get.tags,map,profile_img,get.id);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
                if (Check) {
                    no_posts.setText("");
                }
                else{
                    no_posts.setText("No posts");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mPostRefenrence.addValueEventListener(postListener);
    }
}